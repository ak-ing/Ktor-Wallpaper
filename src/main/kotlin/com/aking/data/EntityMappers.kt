package com.aking.data

import com.aking.config.StorageConfig
import com.aking.database.*
import com.aking.model.Wallpaper
import kotlinx.serialization.json.Json

/**
 * 实体转换工具
 * 集中管理 Entity -> Model 的转换逻辑，避免重复代码
 */
object EntityMappers {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * WallpaperEntity -> Wallpaper
     */
    fun WallpaperEntity.toWallpaper(): Wallpaper {
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
