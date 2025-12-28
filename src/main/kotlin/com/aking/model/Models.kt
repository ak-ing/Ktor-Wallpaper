package com.aking.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: Int,
    val name: String,
    val url: String,
    val thumbnailUrl: String,
    val categoryId: Int,
    val categoryName: String,
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val width: Int,
    val height: Int,
    val downloads: Int = 0,
    val isFeatured: Boolean = false,
    val isEditorsChoice: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val thumbnailUrl: String? = null,
    val wallpaperCount: Int = 0
)

@Serializable
data class Tag(
    val name: String,
    val count: Int = 0
)

@Serializable
data class HomeResponse(
    val featured: List<Wallpaper>,
    val editorsChoice: List<Wallpaper>,
    val newArrivals: List<Wallpaper>,
    val categories: List<Category>
)

@Serializable
data class SearchResponse(
    val wallpapers: List<Wallpaper>,
    val query: String,
    val total: Int
)

// ==================== 请求 DTO ====================

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val thumbnailUrl: String? = null
)

@Serializable
data class UpdateCategoryRequest(
    val name: String? = null,
    val thumbnailUrl: String? = null
)

@Serializable
data class CreateWallpaperRequest(
    val name: String,
    val filename: String,
    val thumbnailFilename: String? = null,
    val categoryId: Int,
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val width: Int = 1080,
    val height: Int = 1920,
    val isFeatured: Boolean = false,
    val isEditorsChoice: Boolean = false
)

@Serializable
data class UpdateWallpaperRequest(
    val name: String? = null,
    val filename: String? = null,
    val thumbnailFilename: String? = null,
    val categoryId: Int? = null,
    val tags: List<String>? = null,
    val colors: List<String>? = null,
    val width: Int? = null,
    val height: Int? = null,
    val isFeatured: Boolean? = null,
    val isEditorsChoice: Boolean? = null
)

// ==================== 响应 DTO ====================

@Serializable
data class UploadResponse(
    val filename: String,
    val url: String,
    val thumbnailUrl: String? = null
)
