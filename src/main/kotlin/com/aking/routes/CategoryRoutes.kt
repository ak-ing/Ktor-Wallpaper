package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.*
import io.ktor.server.routing.*

fun Route.categoryRoutes() {
    route("/categories") {
        // GET /api/categories - List all categories with wallpaper counts
        get {
            val categories = WallpaperRepository.getAllCategories()
            call.success(categories)
        }

        // GET /api/categories/{id} - Get category details
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val category = WallpaperRepository.getCategoryById(id)
                ?: notFound("Category not found")

            call.success(category)
        }

        // GET /api/categories/{id}/wallpapers - Wallpapers by category
        get("/{id}/wallpapers") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            // 验证分类存在
            WallpaperRepository.getCategoryById(id)
                ?: notFound("Category not found")

            val wallpapers = WallpaperRepository.getWallpapersByCategory(id, page, pageSize)
            val total = WallpaperRepository.getWallpaperCountByCategory(id)

            call.success(PageData.of(wallpapers, page, pageSize, total))
        }
    }
}
