package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.HomeResponse
import com.aking.model.success
import io.ktor.server.routing.*

/**
 * 首页相关路由
 */
fun Route.homeRoutes() {
    // GET /api/home - 获取首页聚合数据
    // 包含精选、编辑推荐、最新上传和所有分类
    get("/home") {
        val featured = WallpaperRepository.getFeaturedWallpapers(6)
        val editorsChoice = WallpaperRepository.getEditorsChoiceWallpapers(6)
        val newArrivals = WallpaperRepository.getNewArrivals(10)
        val categories = WallpaperRepository.getAllCategories()

        call.success(HomeResponse(featured, editorsChoice, newArrivals, categories))
    }
}
