package com.aking.data

import com.aking.data.EntityMappers.toWallpaper
import com.aking.database.*
import com.aking.model.*
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object ArtistRepository {

    private val json = Json { ignoreUnknownKeys = true }

    // ==================== Artist 查询 ====================

    suspend fun getAllArtists(page: Int = 1, pageSize: Int = 20): List<Artist> = suspendTransaction {
        ArtistEntity.all()
            .orderBy(Artists.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toArtist() }
    }

    suspend fun getArtistCount(): Int = suspendTransaction {
        ArtistEntity.count().toInt()
    }

    suspend fun getFeaturedArtists(limit: Int = 6): List<Artist> = suspendTransaction {
        ArtistEntity.find { Artists.isFeatured eq true }
            .orderBy(Artists.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toArtist() }
    }

    suspend fun getArtistById(id: Int): ArtistDetail? = suspendTransaction {
        ArtistEntity.findById(id)?.toArtistDetail()
    }

    suspend fun getArtistByIdSimple(id: Int): Artist? = suspendTransaction {
        ArtistEntity.findById(id)?.toArtist()
    }

    suspend fun getWallpapersByArtist(artistId: Int, page: Int = 1, pageSize: Int = 20): List<Wallpaper> = suspendTransaction {
        WallpaperEntity.find { Wallpapers.artistId eq artistId }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toWallpaper() }
    }

    suspend fun getWallpaperCountByArtist(artistId: Int): Int = suspendTransaction {
        WallpaperEntity.find { Wallpapers.artistId eq artistId }.count().toInt()
    }

    suspend fun getSponsorPackages(artistId: Int): List<SponsorPackage> = suspendTransaction {
        ArtistSponsorPackageEntity.find {
            (ArtistSponsorPackages.artistId eq artistId) and (ArtistSponsorPackages.isActive eq true)
        }.map { it.toSponsorPackage() }
    }

    suspend fun searchArtists(query: String): List<Artist> = suspendTransaction {
        val lowerQuery = "%${query.lowercase()}%"
        ArtistEntity.find { Artists.name.lowerCase() like lowerQuery }
            .map { it.toArtist() }
    }

    // ==================== Artist 管理 ====================

    suspend fun createArtist(request: CreateArtistRequest): Artist = suspendTransaction {
        val now = Clock.System.now()
        ArtistEntity.new {
            name = request.name
            avatar = request.avatar
            bio = request.bio
            website = request.website
            socialLinks = json.encodeToString(request.socialLinks)
            createdAt = now
            updatedAt = now
        }.toArtist()
    }

    suspend fun updateArtist(id: Int, request: UpdateArtistRequest): Artist? = suspendTransaction {
        val entity = ArtistEntity.findById(id) ?: return@suspendTransaction null
        request.name?.let { entity.name = it }
        request.avatar?.let { entity.avatar = it }
        request.bio?.let { entity.bio = it }
        request.website?.let { entity.website = it }
        request.socialLinks?.let { entity.socialLinks = json.encodeToString(it) }
        request.isVerified?.let { entity.isVerified = it }
        request.isFeatured?.let { entity.isFeatured = it }
        entity.updatedAt = Clock.System.now()
        entity.toArtist()
    }

    suspend fun deleteArtist(id: Int): Boolean = suspendTransaction {
        val entity = ArtistEntity.findById(id) ?: return@suspendTransaction false
        // 删除关联的赞助包
        ArtistSponsorPackages.deleteWhere { artistId eq id }
        // 将关联壁纸的 artistId 设为 null
        WallpaperEntity.find { Wallpapers.artistId eq id }.forEach {
            it.artist = null
        }
        entity.delete()
        true
    }

    suspend fun verifyArtist(id: Int, verified: Boolean): Boolean = suspendTransaction {
        val entity = ArtistEntity.findById(id) ?: return@suspendTransaction false
        entity.isVerified = verified
        entity.updatedAt = Clock.System.now()
        true
    }

    // ==================== Sponsor Package 管理 ====================

    suspend fun createSponsorPackage(request: CreateSponsorPackageRequest): SponsorPackage = suspendTransaction {
        val now = Clock.System.now()
        ArtistSponsorPackageEntity.new {
            artist = ArtistEntity[request.artistId]
            name = request.name
            description = request.description
            price = request.price.toBigDecimal()
            currency = request.currency
            benefits = json.encodeToString(request.benefits)
            createdAt = now
            updatedAt = now
        }.toSponsorPackage()
    }

    suspend fun updateSponsorPackage(id: Int, request: UpdateSponsorPackageRequest): SponsorPackage? = suspendTransaction {
        val entity = ArtistSponsorPackageEntity.findById(id) ?: return@suspendTransaction null
        request.name?.let { entity.name = it }
        request.description?.let { entity.description = it }
        request.price?.let { entity.price = it.toBigDecimal() }
        request.currency?.let { entity.currency = it }
        request.benefits?.let { entity.benefits = json.encodeToString(it) }
        request.isActive?.let { entity.isActive = it }
        entity.updatedAt = Clock.System.now()
        entity.toSponsorPackage()
    }

    suspend fun deleteSponsorPackage(id: Int): Boolean = suspendTransaction {
        val entity = ArtistSponsorPackageEntity.findById(id) ?: return@suspendTransaction false
        entity.delete()
        true
    }

    suspend fun getSponsorPackageById(id: Int): ArtistSponsorPackageEntity? = suspendTransaction {
        ArtistSponsorPackageEntity.findById(id)
    }

    // ==================== 转换方法 ====================

    private fun ArtistEntity.toArtist(): Artist {
        val wallpaperCount = WallpaperEntity.find { Wallpapers.artistId eq this@toArtist.id }.count().toInt()
        val socialLinksMap: Map<String, String> = try {
            json.decodeFromString(socialLinks)
        } catch (e: Exception) {
            emptyMap()
        }
        return Artist(
            id = id.value,
            name = name,
            avatar = avatar,
            bio = bio,
            website = website,
            socialLinks = socialLinksMap,
            isVerified = isVerified,
            isFeatured = isFeatured,
            wallpaperCount = wallpaperCount,
            createdAt = createdAt.toEpochMilliseconds()
        )
    }

    private fun ArtistEntity.toArtistDetail(): ArtistDetail {
        val wallpaperCount = WallpaperEntity.find { Wallpapers.artistId eq this@toArtistDetail.id }.count().toInt()
        val recentWallpapers = WallpaperEntity.find { Wallpapers.artistId eq this@toArtistDetail.id }
            .orderBy(Wallpapers.createdAt to SortOrder.DESC)
            .limit(6)
            .map { it.toWallpaper() }
        val packages = ArtistSponsorPackageEntity.find {
            (ArtistSponsorPackages.artistId eq this@toArtistDetail.id) and (ArtistSponsorPackages.isActive eq true)
        }.map { it.toSponsorPackage() }
        val socialLinksMap: Map<String, String> = try {
            json.decodeFromString(socialLinks)
        } catch (e: Exception) {
            emptyMap()
        }
        return ArtistDetail(
            id = id.value,
            name = name,
            avatar = avatar,
            bio = bio,
            website = website,
            socialLinks = socialLinksMap,
            isVerified = isVerified,
            sponsorPackages = packages,
            recentWallpapers = recentWallpapers,
            wallpaperCount = wallpaperCount
        )
    }

    private fun ArtistSponsorPackageEntity.toSponsorPackage(): SponsorPackage {
        val benefitsList: List<String> = try {
            json.decodeFromString(benefits)
        } catch (e: Exception) {
            emptyList()
        }
        return SponsorPackage(
            id = id.value,
            artistId = artist.id.value,
            name = name,
            description = description,
            price = price.toDouble(),
            currency = currency,
            benefits = benefitsList
        )
    }
}
