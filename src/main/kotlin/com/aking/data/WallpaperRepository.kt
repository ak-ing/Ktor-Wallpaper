package com.aking.data

import com.aking.config.StorageConfig
import com.aking.database.*
import com.aking.model.*
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object WallpaperRepository {

    private val json = Json { ignoreUnknownKeys = true }

    // ==================== Category 操作 ====================

    suspend fun getAllCategories(): List<Category> = suspendTransaction {
        CategoryEntity.all().map { it.toCategory() }
    }

    suspend fun getCategoryById(id: Int): Category? = suspendTransaction {
        CategoryEntity.findById(id)?.toCategory()
    }

    suspend fun createCategory(request: CreateCategoryRequest): Category = suspendTransaction {
        val now = Clock.System.now()
        CategoryEntity.new {
            name = request.name
            thumbnailUrl = request.thumbnailUrl
            createdAt = now
            updatedAt = now
        }.toCategory()
    }

    suspend fun updateCategory(id: Int, request: UpdateCategoryRequest): Category? = suspendTransaction {
        val entity = CategoryEntity.findById(id) ?: return@suspendTransaction null
        request.name?.let { entity.name = it }
        request.thumbnailUrl?.let { entity.thumbnailUrl = it }
        entity.updatedAt = Clock.System.now()
        entity.toCategory()
    }

    suspend fun deleteCategory(id: Int): Boolean = suspendTransaction {
        val entity = CategoryEntity.findById(id) ?: return@suspendTransaction false
        // 删除该分类下的所有壁纸
        WallpaperEntity.find { Wallpapers.categoryId eq id }.forEach { wallpaper ->
            WallpaperTags.deleteWhere { wallpaperId eq wallpaper.id }
            wallpaper.delete()
        }
        entity.delete()
        true
    }

    // ==================== Wallpaper 操作 ====================

    suspend fun getAllWallpapers(page: Int = 1, pageSize: Int = 20): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.all()
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toWallpaper() }
    }

    suspend fun getWallpaperCount(): Int = suspendTransaction {
        WallpaperEntity.count().toInt()
    }

    suspend fun getWallpaperById(id: Int): Wallpaper? = suspendTransaction {
        WallpaperEntity.findById(id)?.toWallpaper()
    }

    suspend fun getWallpapersByCategory(categoryId: Int, page: Int = 1, pageSize: Int = 20): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.find { Wallpapers.categoryId eq categoryId }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toWallpaper() }
    }

    suspend fun getWallpaperCountByCategory(categoryId: Int): Int = suspendTransaction {
        WallpaperEntity.find { Wallpapers.categoryId eq categoryId }.count().toInt()
    }

    suspend fun getFeaturedWallpapers(limit: Int = 6): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.find { Wallpapers.isFeatured eq true }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toWallpaper() }
    }

    suspend fun getEditorsChoiceWallpapers(limit: Int = 6): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.find { Wallpapers.isEditorsChoice eq true }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toWallpaper() }
    }

    suspend fun getNewArrivals(limit: Int = 10): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.all()
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toWallpaper() }
    }

    suspend fun searchWallpapers(query: String): List<Wallpaper> = suspendTransaction {
        val lowerQuery = "%${query.lowercase()}%"

        // 搜索壁纸名称或分类名称
        val byNameOrCategory = WallpaperEntity.wrapRows(
            Wallpapers.innerJoin(Categories)
                .selectAll()
                .where {
                    (Wallpapers.name.lowerCase() like lowerQuery) or
                    (Categories.name.lowerCase() like lowerQuery)
                }
        ).map { it.toWallpaper() }

        // 搜索标签
        val wallpaperIdsByTag = WallpaperTags
            .select(WallpaperTags.wallpaperId)
            .where { WallpaperTags.tag.lowerCase() like lowerQuery }
            .map { it[WallpaperTags.wallpaperId].value }
            .distinct()

        val byTag = if (wallpaperIdsByTag.isNotEmpty()) {
            WallpaperEntity.find { Wallpapers.id inList wallpaperIdsByTag }
                .map { it.toWallpaper() }
        } else emptyList()

        (byNameOrCategory + byTag).distinctBy { it.id }
    }

    suspend fun createWallpaper(request: CreateWallpaperRequest): Wallpaper = suspendTransaction {
        val now = Clock.System.now()
        val wallpaper = WallpaperEntity.new {
            name = request.name
            filename = request.filename
            thumbnailFilename = request.thumbnailFilename
            category = CategoryEntity[request.categoryId]
            artist = request.artistId?.let { ArtistEntity[it] }
            colors = json.encodeToString(request.colors)
            width = request.width
            height = request.height
            isFeatured = request.isFeatured
            isEditorsChoice = request.isEditorsChoice
            createdAt = now
            updatedAt = now
        }

        // 添加标签
        request.tags.forEach { tag ->
            WallpaperTags.insert {
                it[wallpaperId] = wallpaper.id
                it[WallpaperTags.tag] = tag.lowercase()
            }
        }

        wallpaper.toWallpaper()
    }

    suspend fun updateWallpaper(id: Int, request: UpdateWallpaperRequest): Wallpaper? = suspendTransaction {
        val entity = WallpaperEntity.findById(id) ?: return@suspendTransaction null

        request.name?.let { entity.name = it }
        request.filename?.let { entity.filename = it }
        request.thumbnailFilename?.let { entity.thumbnailFilename = it }
        request.categoryId?.let { entity.category = CategoryEntity[it] }
        request.artistId?.let { entity.artist = ArtistEntity[it] }
        request.colors?.let { entity.colors = json.encodeToString(it) }
        request.width?.let { entity.width = it }
        request.height?.let { entity.height = it }
        request.isFeatured?.let { entity.isFeatured = it }
        request.isEditorsChoice?.let { entity.isEditorsChoice = it }
        entity.updatedAt = Clock.System.now()

        // 更新标签
        request.tags?.let { tags ->
            WallpaperTags.deleteWhere { wallpaperId eq id }
            tags.forEach { tag ->
                WallpaperTags.insert {
                    it[wallpaperId] = entity.id
                    it[WallpaperTags.tag] = tag.lowercase()
                }
            }
        }

        entity.toWallpaper()
    }

    suspend fun deleteWallpaper(id: Int): Boolean = suspendTransaction {
        val entity = WallpaperEntity.findById(id) ?: return@suspendTransaction false
        WallpaperTags.deleteWhere { wallpaperId eq id }
        entity.delete()
        true
    }

    suspend fun incrementDownloads(id: Int): Boolean = suspendTransaction {
        val entity = WallpaperEntity.findById(id) ?: return@suspendTransaction false
        entity.downloads += 1
        true
    }

    // ==================== Tag 操作 ====================

    suspend fun getPopularTags(limit: Int = 10): List<Tag> = suspendTransaction {
        WallpaperTags
            .select(WallpaperTags.tag, WallpaperTags.tag.count())
            .groupBy(WallpaperTags.tag)
            .orderBy(WallpaperTags.tag.count() to SortOrder.DESC)
            .limit(limit)
            .map { Tag(it[WallpaperTags.tag], it[WallpaperTags.tag.count()].toInt()) }
    }

    suspend fun getWallpapersByTag(tag: String, page: Int = 1, pageSize: Int = 20): List<Wallpaper> = suspendTransaction {
        val wallpaperIds = WallpaperTags
            .select(WallpaperTags.wallpaperId)
            .where { WallpaperTags.tag eq tag.lowercase() }
            .map { it[WallpaperTags.wallpaperId].value }

        if (wallpaperIds.isEmpty()) return@suspendTransaction emptyList()

        WallpaperEntity.find { Wallpapers.id inList wallpaperIds }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toWallpaper() }
    }

    suspend fun getWallpaperCountByTag(tag: String): Int = suspendTransaction {
        WallpaperTags
            .select(WallpaperTags.wallpaperId)
            .where { WallpaperTags.tag eq tag.lowercase() }
            .count().toInt()
    }

    // ==================== 转换方法 ====================

    private fun CategoryEntity.toCategory(): Category {
        val count = WallpaperEntity.find { Wallpapers.categoryId eq this@toCategory.id }.count().toInt()
        return Category(
            id = id.value,
            name = name,
            thumbnailUrl = thumbnailUrl,
            wallpaperCount = count
        )
    }

    private fun WallpaperEntity.toWallpaper(): Wallpaper {
        val tags = WallpaperTags
            .select(WallpaperTags.tag)
            .where { WallpaperTags.wallpaperId eq this@toWallpaper.id }
            .map { it[WallpaperTags.tag] }

        val colorList: List<String> = try {
            json.decodeFromString(colors)
        } catch (e: Exception) {
            emptyList()
        }

        return Wallpaper(
            id = id.value,
            name = name,
            url = StorageConfig.getWallpaperUrl(filename),
            thumbnailUrl = StorageConfig.getThumbnailUrl(thumbnailFilename ?: filename),
            categoryId = category.id.value,
            categoryName = category.name,
            artistId = artist?.id?.value,
            artistName = artist?.name,
            tags = tags,
            colors = colorList,
            width = width,
            height = height,
            downloads = downloads,
            isFeatured = isFeatured,
            isEditorsChoice = isEditorsChoice,
            createdAt = createdAt.toEpochMilliseconds()
        )
    }
}
