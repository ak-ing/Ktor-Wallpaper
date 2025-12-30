package com.aking.routes

import com.aking.config.StorageConfig
import com.aking.config.StorageType
import com.aking.model.UploadResponse
import com.aking.model.badRequest
import com.aking.model.created
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.util.cio.*
import java.io.File
import java.util.*

/**
 * 上传类型枚举
 */
private enum class UploadType(val path: String, val urlGenerator: (String) -> String) {
    WALLPAPER(
        path = StorageConfig.Local.wallpaperPath,
        urlGenerator = { StorageConfig.getWallpaperUrl(it) }
    ),
    THUMBNAIL(
        path = StorageConfig.Local.thumbnailPath,
        urlGenerator = { StorageConfig.getThumbnailUrl(it) }
    ),
    CATEGORY(
        path = "${StorageConfig.Local.thumbnailPath}/categories",
        urlGenerator = { StorageConfig.getThumbnailUrl("categories/$it") }
    )
}

/**
 * 通用文件上传处理
 */
private suspend fun RoutingCall.handleUpload(
    uploadType: UploadType,
    includeThumbnailUrl: Boolean = false
) {
    val multipart = receiveMultipart()
    var uploadedFile: UploadResponse? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val originalFileName = part.originalFileName ?: "unknown"
                val extension = originalFileName.substringAfterLast('.', "jpg")
                val newFileName = "${UUID.randomUUID()}.$extension"

                when (StorageConfig.storageType) {
                    StorageType.LOCAL -> {
                        val uploadDir = File(uploadType.path)
                        if (!uploadDir.exists()) uploadDir.mkdirs()

                        val file = File(uploadDir, newFileName)
                        part.provider().copyAndClose(file.writeChannel())

                        uploadedFile = UploadResponse(
                            filename = newFileName,
                            url = uploadType.urlGenerator(newFileName),
                            thumbnailUrl = if (includeThumbnailUrl) {
                                StorageConfig.getThumbnailUrl(newFileName)
                            } else null
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
        created(uploadedFile, "File uploaded")
    } else {
        badRequest("No file uploaded")
    }
}

fun Route.uploadRoutes() {
    route("/upload") {
        // POST /api/upload/wallpaper - 上传壁纸图片
        post("/wallpaper") {
            call.handleUpload(UploadType.WALLPAPER, includeThumbnailUrl = true)
        }

        // POST /api/upload/thumbnail - 上传缩略图
        post("/thumbnail") {
            call.handleUpload(UploadType.THUMBNAIL)
        }

        // POST /api/upload/category - 上传分类缩略图
        post("/category") {
            call.handleUpload(UploadType.CATEGORY)
        }
    }
}
