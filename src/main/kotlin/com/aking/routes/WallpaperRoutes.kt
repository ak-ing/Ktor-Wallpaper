package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.*
import io.ktor.server.routing.*

fun Route.wallpaperRoutes() {
    route("/wallpapers") {
        // GET /api/wallpapers - List all wallpapers with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val wallpapers = WallpaperRepository.getAllWallpapers(page, pageSize)
            val total = WallpaperRepository.getWallpaperCount()

            call.success(PageData.of(wallpapers, page, pageSize, total))
        }

        // GET /api/wallpapers/featured - Featured wallpapers for home
        get("/featured") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val wallpapers = WallpaperRepository.getFeaturedWallpapers(limit)
            call.success(wallpapers)
        }

        // GET /api/wallpapers/editors-choice - Editor's choice wallpapers
        get("/editors-choice") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val wallpapers = WallpaperRepository.getEditorsChoiceWallpapers(limit)
            call.success(wallpapers)
        }

        // GET /api/wallpapers/new - New arrivals
        get("/new") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val wallpapers = WallpaperRepository.getNewArrivals(limit)
            call.success(wallpapers)
        }

        // GET /api/wallpapers/{id} - Single wallpaper by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val wallpaper = WallpaperRepository.getWallpaperById(id)
                ?: notFound("Wallpaper not found")

            call.success(wallpaper)
        }

        // POST /api/wallpapers/{id}/download - Increment download count
        post("/{id}/download") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            if (!WallpaperRepository.incrementDownloads(id)) {
                notFound("Wallpaper not found")
            }

            call.success<Unit>(message = "Download recorded")
        }
    }
}
