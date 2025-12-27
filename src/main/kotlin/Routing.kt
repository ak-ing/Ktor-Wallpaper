package com.aking

import com.aking.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Wallpaper API Server v0.0.1")
        }

        // API routes under /api prefix
        route("/api") {
            homeRoutes()
            wallpaperRoutes()
            categoryRoutes()
            searchRoutes()
        }
    }
}
