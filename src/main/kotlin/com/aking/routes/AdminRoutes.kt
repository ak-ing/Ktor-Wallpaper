package com.aking.routes

import com.aking.data.WallpaperRepository
import com.aking.model.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    route("/admin") {

        // ==================== 分类管理 ====================
        route("/categories") {
            // POST /api/admin/categories - 创建分类
            post {
                val request = call.receive<CreateCategoryRequest>()
                val category = WallpaperRepository.createCategory(request)
                call.respond(HttpStatusCode.Created, ApiResponse(true, category, "Category created"))
            }

            // PUT /api/admin/categories/{id} - 更新分类
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Category>(false, message = "Invalid ID"))

                val request = call.receive<UpdateCategoryRequest>()
                val category = WallpaperRepository.updateCategory(id, request)

                if (category == null) {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Category>(false, message = "Category not found"))
                } else {
                    call.respond(ApiResponse(true, category, "Category updated"))
                }
            }

            // DELETE /api/admin/categories/{id} - 删除分类
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, message = "Invalid ID"))

                if (WallpaperRepository.deleteCategory(id)) {
                    call.respond(ApiResponse<Unit>(true, message = "Category deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, message = "Category not found"))
                }
            }
        }

        // ==================== 壁纸管理 ====================
        route("/wallpapers") {
            // POST /api/admin/wallpapers - 创建壁纸
            post {
                val request = call.receive<CreateWallpaperRequest>()

                // 验证分类存在
                if (WallpaperRepository.getCategoryById(request.categoryId) == null) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Wallpaper>(false, message = "Category not found")
                    )
                }

                val wallpaper = WallpaperRepository.createWallpaper(request)
                call.respond(HttpStatusCode.Created, ApiResponse(true, wallpaper, "Wallpaper created"))
            }

            // PUT /api/admin/wallpapers/{id} - 更新壁纸
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Wallpaper>(false, message = "Invalid ID"))

                val request = call.receive<UpdateWallpaperRequest>()

                // 验证分类存在
                request.categoryId?.let { categoryId ->
                    if (WallpaperRepository.getCategoryById(categoryId) == null) {
                        return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Wallpaper>(false, message = "Category not found")
                        )
                    }
                }

                val wallpaper = WallpaperRepository.updateWallpaper(id, request)

                if (wallpaper == null) {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Wallpaper>(false, message = "Wallpaper not found"))
                } else {
                    call.respond(ApiResponse(true, wallpaper, "Wallpaper updated"))
                }
            }

            // DELETE /api/admin/wallpapers/{id} - 删除壁纸
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(false, message = "Invalid ID"))

                if (WallpaperRepository.deleteWallpaper(id)) {
                    call.respond(ApiResponse<Unit>(true, message = "Wallpaper deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(false, message = "Wallpaper not found"))
                }
            }
        }
    }
}
