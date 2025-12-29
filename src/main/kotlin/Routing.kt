package com.aking

import com.aking.config.StorageConfig
import com.aking.config.StorageType
import com.aking.routes.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

/**
 * 配置应用程序路由
 * 包括静态文件服务、API 路由和管理接口
 */
fun Application.configureRouting() {
    routing {
        // 根路径 - 服务器状态检查
        get("/") {
            call.respondText("Wallpaper API Server v1.0.0")
        }

        // 静态文件服务 - 壁纸和缩略图
        // 仅在本地存储模式下启用
        if (StorageConfig.storageType == StorageType.LOCAL) {
            staticFiles("/static/wallpapers", File(StorageConfig.Local.wallpaperPath))
            staticFiles("/static/thumbnails", File(StorageConfig.Local.thumbnailPath))
        }

        // API 路由 - /api 前缀
        route("/api") {
            // 首页数据
            homeRoutes()

            // 壁纸相关接口
            wallpaperRoutes()

            // 分类相关接口
            categoryRoutes()

            // 搜索和标签接口
            searchRoutes()

            // 文件上传接口
            uploadRoutes()

            // 艺术家相关接口
            artistRoutes()

            // 专题/合集相关接口
            collectionRoutes()

            // 打赏相关接口
            donationRoutes()

            // 赞助相关接口
            sponsorRoutes()

            // 管理接口 (CRUD)
            adminRoutes()
        }
    }
}
