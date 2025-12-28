package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    route("/admin") {

        // ==================== 分类管理 ====================
        route("/categories") {
            // POST /api/admin/categories - 创建分类
            post {
                val request = call.receive<CreateCategoryRequest>()
                val category = WallpaperRepository.createCategory(request)
                call.created(category, "Category created")
            }

            // PUT /api/admin/categories/{id} - 更新分类
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<UpdateCategoryRequest>()
                val category = WallpaperRepository.updateCategory(id, request)
                    ?: notFound("Category not found")

                call.success(category, "Category updated")
            }

            // DELETE /api/admin/categories/{id} - 删除分类
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!WallpaperRepository.deleteCategory(id)) {
                    notFound("Category not found")
                }

                call.success<Unit>(message = "Category deleted")
            }
        }

        // ==================== 壁纸管理 ====================
        route("/wallpapers") {
            // POST /api/admin/wallpapers - 创建壁纸
            post {
                val request = call.receive<CreateWallpaperRequest>()

                // 验证分类存在
                WallpaperRepository.getCategoryById(request.categoryId)
                    ?: badRequest("Category not found")

                val wallpaper = WallpaperRepository.createWallpaper(request)
                call.created(wallpaper, "Wallpaper created")
            }

            // PUT /api/admin/wallpapers/{id} - 更新壁纸
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<UpdateWallpaperRequest>()

                // 验证分类存在
                request.categoryId?.let { categoryId ->
                    WallpaperRepository.getCategoryById(categoryId)
                        ?: badRequest("Category not found")
                }

                val wallpaper = WallpaperRepository.updateWallpaper(id, request)
                    ?: notFound("Wallpaper not found")

                call.success(wallpaper, "Wallpaper updated")
            }

            // DELETE /api/admin/wallpapers/{id} - 删除壁纸
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!WallpaperRepository.deleteWallpaper(id)) {
                    notFound("Wallpaper not found")
                }

                call.success<Unit>(message = "Wallpaper deleted")
            }
        }
    }
}
