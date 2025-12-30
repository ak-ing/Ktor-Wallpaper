package com.aking.routes

import com.aking.data.*
import com.aking.model.AddWallpaperToCollectionRequest
import com.aking.model.CreateArtistRequest
import com.aking.model.CreateCategoryRequest
import com.aking.model.CreateCollectionRequest
import com.aking.model.CreateSponsorPackageRequest
import com.aking.model.CreateWallpaperRequest
import com.aking.model.PageData
import com.aking.model.UpdateArtistRequest
import com.aking.model.UpdateCategoryRequest
import com.aking.model.UpdateCollectionRequest
import com.aking.model.UpdateSponsorPackageRequest
import com.aking.model.UpdateWallpaperRequest
import com.aking.model.badRequest
import com.aking.model.created
import com.aking.model.notFound
import com.aking.model.success
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

                // 验证艺术家存在（如果指定）
                request.artistId?.let { artistId ->
                    ArtistRepository.getArtistByIdSimple(artistId)
                        ?: badRequest("Artist not found")
                }

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

                // 验证艺术家存在（如果指定）
                request.artistId?.let { artistId ->
                    ArtistRepository.getArtistByIdSimple(artistId)
                        ?: badRequest("Artist not found")
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

        // ==================== 艺术家管理 ====================
        route("/artists") {
            // POST /api/admin/artists - 创建艺术家
            post {
                val request = call.receive<CreateArtistRequest>()
                val artist = ArtistRepository.createArtist(request)
                call.created(artist, "Artist created")
            }

            // PUT /api/admin/artists/{id} - 更新艺术家
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<UpdateArtistRequest>()
                val artist = ArtistRepository.updateArtist(id, request)
                    ?: notFound("Artist not found")

                call.success(artist, "Artist updated")
            }

            // DELETE /api/admin/artists/{id} - 删除艺术家
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!ArtistRepository.deleteArtist(id)) {
                    notFound("Artist not found")
                }

                call.success<Unit>(message = "Artist deleted")
            }

            // POST /api/admin/artists/{id}/verify - 认证艺术家
            post("/{id}/verify") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!ArtistRepository.verifyArtist(id, true)) {
                    notFound("Artist not found")
                }

                call.success<Unit>(message = "Artist verified")
            }

            // POST /api/admin/artists/{id}/unverify - 取消认证
            post("/{id}/unverify") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!ArtistRepository.verifyArtist(id, false)) {
                    notFound("Artist not found")
                }

                call.success<Unit>(message = "Artist unverified")
            }
        }

        // ==================== 赞助包管理 ====================
        route("/sponsor-packages") {
            // POST /api/admin/sponsor-packages - 创建赞助包
            post {
                val request = call.receive<CreateSponsorPackageRequest>()

                // 验证艺术家存在
                ArtistRepository.getArtistByIdSimple(request.artistId)
                    ?: badRequest("Artist not found")

                val pkg = ArtistRepository.createSponsorPackage(request)
                call.created(pkg, "Sponsor package created")
            }

            // PUT /api/admin/sponsor-packages/{id} - 更新赞助包
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<UpdateSponsorPackageRequest>()
                val pkg = ArtistRepository.updateSponsorPackage(id, request)
                    ?: notFound("Sponsor package not found")

                call.success(pkg, "Sponsor package updated")
            }

            // DELETE /api/admin/sponsor-packages/{id} - 删除赞助包
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!ArtistRepository.deleteSponsorPackage(id)) {
                    notFound("Sponsor package not found")
                }

                call.success<Unit>(message = "Sponsor package deleted")
            }
        }

        // ==================== 专题管理 ====================
        route("/collections") {
            // POST /api/admin/collections - 创建专题
            post {
                val request = call.receive<CreateCollectionRequest>()
                val collection = CollectionRepository.createCollection(request)
                call.created(collection, "Collection created")
            }

            // PUT /api/admin/collections/{id} - 更新专题
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<UpdateCollectionRequest>()
                val collection = CollectionRepository.updateCollection(id, request)
                    ?: notFound("Collection not found")

                call.success(collection, "Collection updated")
            }

            // DELETE /api/admin/collections/{id} - 删除专题
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                if (!CollectionRepository.deleteCollection(id)) {
                    notFound("Collection not found")
                }

                call.success<Unit>(message = "Collection deleted")
            }

            // POST /api/admin/collections/{id}/wallpapers - 添加壁纸到专题
            post("/{id}/wallpapers") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid ID")

                val request = call.receive<AddWallpaperToCollectionRequest>()

                if (!CollectionRepository.addWallpaperToCollection(id, request)) {
                    badRequest("Failed to add wallpaper to collection")
                }

                call.success<Unit>(message = "Wallpaper added to collection")
            }

            // DELETE /api/admin/collections/{id}/wallpapers/{wallpaperId} - 从专题移除壁纸
            delete("/{id}/wallpapers/{wallpaperId}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: badRequest("Invalid collection ID")
                val wallpaperId = call.parameters["wallpaperId"]?.toIntOrNull()
                    ?: badRequest("Invalid wallpaper ID")

                if (!CollectionRepository.removeWallpaperFromCollection(id, wallpaperId)) {
                    notFound("Wallpaper not in collection")
                }

                call.success<Unit>(message = "Wallpaper removed from collection")
            }
        }

        // ==================== 打赏统计 ====================
        route("/donations") {
            // GET /api/admin/donations - 获取打赏列表
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val donations = DonationRepository.getAllDonations(page, pageSize)
                val total = DonationRepository.getDonationCount()

                call.success(PageData.of(donations, page, pageSize, total))
            }

            // GET /api/admin/donations/stats - 获取打赏统计
            get("/stats") {
                val stats = DonationRepository.getDonationStats()
                call.success(stats)
            }
        }

        // ==================== 赞助订单统计 ====================
        route("/sponsor-orders") {
            // GET /api/admin/sponsor-orders - 获取赞助订单列表
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20

                val orders = SponsorRepository.getSponsorOrders(page, pageSize)
                val total = SponsorRepository.getSponsorOrderCount()

                call.success(PageData.of(orders, page, pageSize, total))
            }

            // GET /api/admin/sponsor-orders/stats - 获取赞助统计
            get("/stats") {
                val stats = SponsorRepository.getSponsorStats()
                call.success(stats)
            }
        }
    }
}
