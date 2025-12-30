package com.aking.data

import com.aking.data.EntityMappers.toWallpaper
import com.aking.database.*
import com.aking.model.AddWallpaperToCollectionRequest
import com.aking.model.CollectionDetail
import com.aking.model.CreateCollectionRequest
import com.aking.model.SponsorInfo
import com.aking.model.UpdateCollectionRequest
import com.aking.model.Wallpaper
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.aking.model.Collection as WallpaperCollection

object CollectionRepository {

    private val json = Json { ignoreUnknownKeys = true }

    // ==================== Collection 查询 ====================

    suspend fun getAllCollections(page: Int = 1, pageSize: Int = 20): List<WallpaperCollection> = suspendTransaction {
        CollectionEntity.all()
            .orderBy(Collections.displayOrder to SortOrder.DESC, Collections.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toCollection() }
    }

    suspend fun getCollectionCount(): Int = suspendTransaction {
        CollectionEntity.count().toInt()
    }

    suspend fun getFeaturedCollections(limit: Int = 6): List<WallpaperCollection> = suspendTransaction {
        CollectionEntity.find { Collections.isFeatured eq true }
            .orderBy(Collections.displayOrder to SortOrder.DESC, Collections.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toCollection() }
    }

    suspend fun getCollectionBySlug(slug: String): CollectionDetail? = suspendTransaction {
        CollectionEntity.find { Collections.slug eq slug }.firstOrNull()?.toCollectionDetail()
    }

    suspend fun getCollectionById(id: Int): WallpaperCollection? = suspendTransaction {
        CollectionEntity.findById(id)?.toCollection()
    }

    suspend fun getCollectionWallpapers(collectionId: Int, page: Int = 1, pageSize: Int = 20): List<Wallpaper> = suspendTransaction {
        val wallpaperIds = CollectionWallpapers
            .select(CollectionWallpapers.wallpaperId)
            .where { CollectionWallpapers.collectionId eq collectionId }
            .orderBy(CollectionWallpapers.displayOrder to SortOrder.ASC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it[CollectionWallpapers.wallpaperId].value }

        if (wallpaperIds.isEmpty()) return@suspendTransaction emptyList()

        WallpaperEntity.find { Wallpapers.id inList wallpaperIds }
            .map { it.toWallpaper() }
    }

    suspend fun getCollectionWallpaperCount(collectionId: Int): Int = suspendTransaction {
        CollectionWallpapers
            .select(CollectionWallpapers.wallpaperId)
            .where { CollectionWallpapers.collectionId eq collectionId }
            .count().toInt()
    }

    suspend fun searchCollections(query: String): List<WallpaperCollection> = suspendTransaction {
        val lowerQuery = "%${query.lowercase()}%"
        CollectionEntity.find {
            (Collections.name.lowerCase() like lowerQuery) or
            (Collections.brandName.lowerCase() like lowerQuery)
        }.map { it.toCollection() }
    }

    // ==================== Collection 管理 ====================

    suspend fun createCollection(request: CreateCollectionRequest): WallpaperCollection = suspendTransaction {
        val now = Clock.System.now()
        CollectionEntity.new {
            name = request.name
            slug = request.slug
            description = request.description
            coverUrl = request.coverUrl
            bannerUrl = request.bannerUrl
            brandName = request.brandName
            brandLogoUrl = request.brandLogoUrl
            isSponsored = request.isSponsored
            sponsorInfo = request.sponsorInfo?.let { json.encodeToString(it) }
            isFeatured = request.isFeatured
            displayOrder = request.displayOrder
            createdAt = now
            updatedAt = now
        }.toCollection()
    }

    suspend fun updateCollection(id: Int, request: UpdateCollectionRequest): WallpaperCollection? = suspendTransaction {
        val entity = CollectionEntity.findById(id) ?: return@suspendTransaction null
        request.name?.let { entity.name = it }
        request.description?.let { entity.description = it }
        request.coverUrl?.let { entity.coverUrl = it }
        request.bannerUrl?.let { entity.bannerUrl = it }
        request.brandName?.let { entity.brandName = it }
        request.brandLogoUrl?.let { entity.brandLogoUrl = it }
        request.isSponsored?.let { entity.isSponsored = it }
        request.sponsorInfo?.let { entity.sponsorInfo = json.encodeToString(it) }
        request.isFeatured?.let { entity.isFeatured = it }
        request.displayOrder?.let { entity.displayOrder = it }
        entity.updatedAt = Clock.System.now()
        entity.toCollection()
    }

    suspend fun deleteCollection(id: Int): Boolean = suspendTransaction {
        val entity = CollectionEntity.findById(id) ?: return@suspendTransaction false
        // 删除关联的壁纸关系
        CollectionWallpapers.deleteWhere { collectionId eq id }
        entity.delete()
        true
    }

    suspend fun addWallpaperToCollection(collectionId: Int, request: AddWallpaperToCollectionRequest): Boolean = suspendTransaction {
        // 验证专题存在
        CollectionEntity.findById(collectionId) ?: return@suspendTransaction false
        // 验证壁纸存在
        WallpaperEntity.findById(request.wallpaperId) ?: return@suspendTransaction false

        // 检查是否已存在
        val exists = CollectionWallpapers
            .select(CollectionWallpapers.wallpaperId)
            .where {
                (CollectionWallpapers.collectionId eq collectionId) and
                (CollectionWallpapers.wallpaperId eq request.wallpaperId)
            }
            .count() > 0

        if (exists) return@suspendTransaction true

        CollectionWallpapers.insert {
            it[CollectionWallpapers.collectionId] = collectionId
            it[wallpaperId] = request.wallpaperId
            it[displayOrder] = request.displayOrder
        }
        true
    }

    suspend fun removeWallpaperFromCollection(collectionId: Int, wallpaperId: Int): Boolean = suspendTransaction {
        val deleted = CollectionWallpapers.deleteWhere {
            (CollectionWallpapers.collectionId eq collectionId) and
            (CollectionWallpapers.wallpaperId eq wallpaperId)
        }
        deleted > 0
    }

    // ==================== 转换方法 ====================

    private fun CollectionEntity.toCollection(): WallpaperCollection {
        val wallpaperCount = CollectionWallpapers
            .select(CollectionWallpapers.wallpaperId)
            .where { CollectionWallpapers.collectionId eq this@toCollection.id }
            .count().toInt()

        return WallpaperCollection(
            id = id.value,
            name = name,
            slug = slug,
            description = description,
            coverUrl = coverUrl,
            bannerUrl = bannerUrl,
            brandName = brandName,
            brandLogoUrl = brandLogoUrl,
            isSponsored = isSponsored,
            wallpaperCount = wallpaperCount,
            isFeatured = isFeatured
        )
    }

    private fun CollectionEntity.toCollectionDetail(): CollectionDetail {
        val wallpaperIds = CollectionWallpapers
            .select(CollectionWallpapers.wallpaperId)
            .where { CollectionWallpapers.collectionId eq this@toCollectionDetail.id }
            .orderBy(CollectionWallpapers.displayOrder to SortOrder.ASC)
            .limit(20)
            .map { it[CollectionWallpapers.wallpaperId].value }

        val wallpapers = if (wallpaperIds.isNotEmpty()) {
            WallpaperEntity.find { Wallpapers.id inList wallpaperIds }.map { it.toWallpaper() }
        } else emptyList()

        val sponsorInfoObj: SponsorInfo? = sponsorInfo?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }

        return CollectionDetail(
            id = id.value,
            name = name,
            slug = slug,
            description = description,
            coverUrl = coverUrl,
            bannerUrl = bannerUrl,
            brandName = brandName,
            brandLogoUrl = brandLogoUrl,
            isSponsored = isSponsored,
            sponsorInfo = sponsorInfoObj,
            wallpapers = wallpapers,
            wallpaperCount = wallpapers.size
        )
    }
}
