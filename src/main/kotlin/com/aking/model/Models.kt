package com.aking.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: String,
    val name: String,
    val url: String,
    val thumbnailUrl: String,
    val categoryId: String,
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
    val id: String,
    val name: String,
    val thumbnailUrl: String,
    val wallpaperCount: Int = 0
)

@Serializable
data class Tag(
    val name: String,
    val count: Int = 0
)

@Serializable
data class WallpaperListResponse(
    val wallpapers: List<Wallpaper>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val hasMore: Boolean
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
