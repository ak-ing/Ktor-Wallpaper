package com.aking.routes

import com.aking.data.ArtistRepository
import com.aking.model.*
import io.ktor.server.routing.*

fun Route.artistRoutes() {
    route("/artists") {
        // GET /api/artists - List all artists with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val artists = ArtistRepository.getAllArtists(page, pageSize)
            val total = ArtistRepository.getArtistCount()

            call.success(PageData.of(artists, page, pageSize, total))
        }

        // GET /api/artists/featured - Featured artists
        get("/featured") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val artists = ArtistRepository.getFeaturedArtists(limit)
            call.success(artists)
        }

        // GET /api/artists/{id} - Artist detail by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val artist = ArtistRepository.getArtistById(id)
                ?: notFound("Artist not found")

            call.success(artist)
        }

        // GET /api/artists/{id}/wallpapers - Wallpapers by artist
        get("/{id}/wallpapers") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            // 验证艺术家是否存在
            ArtistRepository.getArtistByIdSimple(id) ?: notFound("Artist not found")

            val wallpapers = ArtistRepository.getWallpapersByArtist(id, page, pageSize)
            val total = ArtistRepository.getWallpaperCountByArtist(id)

            call.success(PageData.of(wallpapers, page, pageSize, total))
        }

        // GET /api/artists/{id}/packages - Sponsor packages by artist
        get("/{id}/packages") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            // 验证艺术家是否存在
            ArtistRepository.getArtistByIdSimple(id) ?: notFound("Artist not found")

            val packages = ArtistRepository.getSponsorPackages(id)
            call.success(packages)
        }
    }
}
