package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.WallpaperListResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.wallpaperRoutes() {
    route("/wallpapers") {
        // GET /api/wallpapers - List all wallpapers with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val wallpapers = WallpaperRepository.getAllWallpapers(page, pageSize)
            val total = WallpaperRepository.getWallpaperCount()
            val hasMore = page * pageSize < total

            call.respond(WallpaperListResponse(wallpapers, page, pageSize, total, hasMore))
        }

        // GET /api/wallpapers/featured - Featured wallpapers for home
        get("/featured") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val wallpapers = WallpaperRepository.getFeaturedWallpapers(limit)
            call.respond(wallpapers)
        }

        // GET /api/wallpapers/editors-choice - Editor's choice wallpapers
        get("/editors-choice") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val wallpapers = WallpaperRepository.getEditorsChoiceWallpapers(limit)
            call.respond(wallpapers)
        }

        // GET /api/wallpapers/new - New arrivals
        get("/new") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val wallpapers = WallpaperRepository.getNewArrivals(limit)
            call.respond(wallpapers)
        }

        // GET /api/wallpapers/{id} - Single wallpaper by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

            val wallpaper = WallpaperRepository.getWallpaperById(id)
            if (wallpaper == null) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wallpaper not found"))
            } else {
                call.respond(wallpaper)
            }
        }

        // POST /api/wallpapers/{id}/download - Increment download count
        post("/{id}/download") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))

            if (WallpaperRepository.incrementDownloads(id)) {
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Wallpaper not found"))
            }
        }
    }
}
