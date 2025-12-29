package com.aking.routes

import com.aking.data.ArtistRepository
import com.aking.data.CollectionRepository
import com.aking.data.WallpaperRepository
import com.aking.model.*
import io.ktor.server.routing.*

/**
 * 搜索相关路由
 */
fun Route.searchRoutes() {
    route("/search") {
        // GET /api/search?q=query - 搜索壁纸、艺术家、专题
        // 支持按名称、分类、标签搜索
        get {
            val query = call.request.queryParameters["q"]
            if (query.isNullOrBlank()) {
                badRequest("Query parameter 'q' is required")
            }

            val wallpapers = WallpaperRepository.searchWallpapers(query)
            val artists = ArtistRepository.searchArtists(query)
            val collections = CollectionRepository.searchCollections(query)

            call.success(SearchResponse(
                wallpapers = wallpapers,
                artists = artists,
                collections = collections,
                query = query,
                total = wallpapers.size + artists.size + collections.size
            ))
        }
    }

    route("/tags") {
        // GET /api/tags/popular - 获取热门标签
        get("/popular") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val tags = WallpaperRepository.getPopularTags(limit)
            call.success(tags)
        }

        // GET /api/tags/{tag}/wallpapers - 按标签获取壁纸
        get("/{tag}/wallpapers") {
            val tag = call.parameters["tag"]
                ?: badRequest("Tag is required")

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val wallpapers = WallpaperRepository.getWallpapersByTag(tag, page, pageSize)
            val total = WallpaperRepository.getWallpaperCountByTag(tag)

            call.success(PageData.of(wallpapers, page, pageSize, total))
        }
    }
}
