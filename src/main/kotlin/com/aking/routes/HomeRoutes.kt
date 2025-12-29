package com.aking.routes

import com.aking.data.ArtistRepository
import com.aking.data.CollectionRepository
import com.aking.data.WallpaperRepository
import com.aking.model.HomeResponse
import com.aking.model.success
import io.ktor.server.routing.*

/**
 * 首页相关路由
 */
fun Route.homeRoutes() {
    // GET /api/home - 获取首页聚合数据
    // 包含精选、编辑推荐、最新上传、分类、推荐专题和推荐艺术家
    get("/home") {
        val featured = WallpaperRepository.getFeaturedWallpapers(6)
        val editorsChoice = WallpaperRepository.getEditorsChoiceWallpapers(6)
        val newArrivals = WallpaperRepository.getNewArrivals(10)
        val categories = WallpaperRepository.getAllCategories()
        val featuredCollections = CollectionRepository.getFeaturedCollections(4)
        val featuredArtists = ArtistRepository.getFeaturedArtists(4)

        call.success(HomeResponse(
            featured = featured,
            editorsChoice = editorsChoice,
            newArrivals = newArrivals,
            categories = categories,
            featuredCollections = featuredCollections,
            featuredArtists = featuredArtists
        ))
    }
}
