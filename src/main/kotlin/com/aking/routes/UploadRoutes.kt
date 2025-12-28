package com.aking.routes

import com.aking.config.StorageConfig
import com.aking.config.StorageType
import com.aking.model.UploadResponse
import com.aking.model.badRequest
import com.aking.model.created
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*

fun Route.uploadRoutes() {
    route("/upload") {
        // POST /api/upload/wallpaper - 上传壁纸图片
        post("/wallpaper") {
            val multipart = call.receiveMultipart()
            var uploadedFile: UploadResponse? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val originalFileName = part.originalFileName ?: "unknown"
                        val extension = originalFileName.substringAfterLast('.', "jpg")
                        val newFileName = "${UUID.randomUUID()}.$extension"

                        when (StorageConfig.storageType) {
                            StorageType.LOCAL -> {
                                val uploadDir = File(StorageConfig.Local.wallpaperPath)
                                if (!uploadDir.exists()) uploadDir.mkdirs()

                                val file = File(uploadDir, newFileName)
                                part.provider().copyAndClose(file.writeChannel())

                                uploadedFile = UploadResponse(
                                    filename = newFileName,
                                    url = StorageConfig.getWallpaperUrl(newFileName),
                                    thumbnailUrl = StorageConfig.getThumbnailUrl(newFileName)
                                )
                            }
                            StorageType.COS -> {
                                // TODO: 实现腾讯云 COS 上传
                                // 需要添加 COS SDK 依赖
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (uploadedFile != null) {
                call.created(uploadedFile, "File uploaded")
            } else {
                badRequest("No file uploaded")
            }
        }

        // POST /api/upload/thumbnail - 上传缩略图
        post("/thumbnail") {
            val multipart = call.receiveMultipart()
            var uploadedFile: UploadResponse? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val originalFileName = part.originalFileName ?: "unknown"
                        val extension = originalFileName.substringAfterLast('.', "jpg")
                        val newFileName = "${UUID.randomUUID()}.$extension"

                        when (StorageConfig.storageType) {
                            StorageType.LOCAL -> {
                                val uploadDir = File(StorageConfig.Local.thumbnailPath)
                                if (!uploadDir.exists()) uploadDir.mkdirs()

                                val file = File(uploadDir, newFileName)
                                part.provider().copyAndClose(file.writeChannel())

                                uploadedFile = UploadResponse(
                                    filename = newFileName,
                                    url = StorageConfig.getThumbnailUrl(newFileName)
                                )
                            }
                            StorageType.COS -> {
                                // TODO: 实现腾讯云 COS 上传
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (uploadedFile != null) {
                call.created(uploadedFile, "File uploaded")
            } else {
                badRequest("No file uploaded")
            }
        }

        // POST /api/upload/category - 上传分类缩略图
        post("/category") {
            val multipart = call.receiveMultipart()
            var uploadedFile: UploadResponse? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val originalFileName = part.originalFileName ?: "unknown"
                        val extension = originalFileName.substringAfterLast('.', "jpg")
                        val newFileName = "categories/${UUID.randomUUID()}.$extension"

                        when (StorageConfig.storageType) {
                            StorageType.LOCAL -> {
                                val uploadDir = File(StorageConfig.Local.thumbnailPath, "categories")
                                if (!uploadDir.exists()) uploadDir.mkdirs()

                                val file = File(StorageConfig.Local.thumbnailPath, newFileName)
                                part.provider().copyAndClose(file.writeChannel())

                                uploadedFile = UploadResponse(
                                    filename = newFileName,
                                    url = StorageConfig.getThumbnailUrl(newFileName)
                                )
                            }
                            StorageType.COS -> {
                                // TODO: 实现腾讯云 COS 上传
                            }
                        }
                    }
                    else -> {}
                }
                part.dispose()
            }

            if (uploadedFile != null) {
                call.created(uploadedFile, "File uploaded")
            } else {
                badRequest("No file uploaded")
            }
        }
    }
}
