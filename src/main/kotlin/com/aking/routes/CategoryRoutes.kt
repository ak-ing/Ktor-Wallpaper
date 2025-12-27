package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.WallpaperListResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes() {
    route("/categories") {
        // GET /api/categories - List all categories with wallpaper counts
        get {
            val categories = WallpaperRepository.getAllCategories()
            call.respond(categories)
        }

        // GET /api/categories/{id} - Get category details
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val category = WallpaperRepository.getCategoryById(id)
            if (category == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Category not found"))
            } else {
                call.respond(category)
            }
        }

        // GET /api/categories/{id}/wallpapers - Wallpapers by category
        get("/{id}/wallpapers") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val category = WallpaperRepository.getCategoryById(id)
            if (category == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Category not found"))
                return@get
            }

            val wallpapers = WallpaperRepository.getWallpapersByCategory(id, page, pageSize)
            val total = WallpaperRepository.getWallpaperCountByCategory(id)
            val hasMore = page * pageSize < total

            call.respond(WallpaperListResponse(wallpapers, page, pageSize, total, hasMore))
        }
    }
}
