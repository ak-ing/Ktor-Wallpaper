package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.HomeResponse
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homeRoutes() {
    // GET /api/home - Aggregated data for home screen
    get("/home") {
        val featured = WallpaperRepository.getFeaturedWallpapers(6)
        val editorsChoice = WallpaperRepository.getEditorsChoiceWallpapers(6)
        val newArrivals = WallpaperRepository.getNewArrivals(10)
        val categories = WallpaperRepository.getAllCategories()

        call.respond(HomeResponse(featured, editorsChoice, newArrivals, categories))
    }
}
