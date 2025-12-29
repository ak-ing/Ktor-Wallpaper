package com.aking.routes

import com.aking.data.CollectionRepository
import com.aking.model.PageData
import com.aking.model.badRequest
import com.aking.model.notFound
import com.aking.model.success
import io.ktor.server.routing.*

fun Route.collectionRoutes() {
    route("/collections") {
        // GET /api/collections - List all collections with pagination
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            val collections = CollectionRepository.getAllCollections(page, pageSize)
            val total = CollectionRepository.getCollectionCount()

            call.success(PageData.of(collections, page, pageSize, total))
        }

        // GET /api/collections/featured - Featured collections
        get("/featured") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 6
            val collections = CollectionRepository.getFeaturedCollections(limit)
            call.success(collections)
        }

        // GET /api/collections/{slug} - Collection detail by slug
        get("/{slug}") {
            val slug = call.parameters["slug"]
                ?: badRequest("Invalid slug")

            val collection = CollectionRepository.getCollectionBySlug(slug)
                ?: notFound("Collection not found")

            call.success(collection)
        }

        // GET /api/collections/{slug}/wallpapers - Wallpapers in collection
        get("/{slug}/wallpapers") {
            val slug = call.parameters["slug"]
                ?: badRequest("Invalid slug")
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

            // 获取专题 ID
            val collection = CollectionRepository.getCollectionBySlug(slug)
                ?: notFound("Collection not found")

            val wallpapers = CollectionRepository.getCollectionWallpapers(collection.id, page, pageSize)
            val total = CollectionRepository.getCollectionWallpaperCount(collection.id)

            call.success(PageData.of(wallpapers, page, pageSize, total))
        }
    }
}
