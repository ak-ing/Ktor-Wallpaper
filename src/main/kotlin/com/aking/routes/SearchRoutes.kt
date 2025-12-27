package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.SearchResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.searchRoutes() {
    route("/search") {
        // GET /api/search?q=query - Search wallpapers
        get {
            val query = call.request.queryParameters["q"]
            if (query.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Query parameter 'q' is required"))
                return@get
            }

            val wallpapers = WallpaperRepository.searchWallpapers(query)
            call.respond(SearchResponse(wallpapers, query, wallpapers.size))
        }
    }

    route("/tags") {
        // GET /api/tags/popular - Popular tags for search screen
        get("/popular") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val tags = WallpaperRepository.getPopularTags(limit)
            call.respond(tags)
        }

        // GET /api/tags/{tag}/wallpapers - Wallpapers by tag
        get("/{tag}/wallpapers") {
            val tag = call.parameters["tag"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val wallpapers = WallpaperRepository.getWallpapersByTag(tag, page, pageSize)
            call.respond(wallpapers)
        }
    }
}
