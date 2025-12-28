package com.aking.config

/**
 * 存储类型枚举
 */
enum class StorageType {
    LOCAL,  // 本地文件存储
    COS     // 腾讯云对象存储
}

/**
 * 存储配置对象
 * 管理壁纸图片的存储方式和访问路径
 *
 * 环境变量:
 * - STORAGE_TYPE: 存储类型 (LOCAL/COS，默认: LOCAL)
 * - SERVER_BASE_URL: 服务器基础 URL (默认: http://localhost:8080)
 *
 * 本地存储环境变量:
 * - WALLPAPER_PATH: 壁纸存储路径 (默认: data/wallpapers)
 * - THUMBNAIL_PATH: 缩略图存储路径 (默认: data/thumbnails)
 *
 * 腾讯云 COS 环境变量:
 * - COS_SECRET_ID: 密钥 ID
 * - COS_SECRET_KEY: 密钥 Key
 * - COS_REGION: 地域 (默认: ap-guangzhou)
 * - COS_BUCKET: 存储桶名称
 * - COS_BASE_URL: 访问域名 (可配置 CDN)
 */
object StorageConfig {

    /** 当前存储类型 */
    val storageType: StorageType = when (System.getenv("STORAGE_TYPE")?.uppercase()) {
        "COS" -> StorageType.COS
        else -> StorageType.LOCAL
    }

    /** 本地存储配置 */
    object Local {
        val wallpaperPath: String = System.getenv("WALLPAPER_PATH") ?: "data/wallpapers"
        val thumbnailPath: String = System.getenv("THUMBNAIL_PATH") ?: "data/thumbnails"
    }

    /** 腾讯云 COS 配置 */
    object Cos {
        val secretId: String = System.getenv("COS_SECRET_ID") ?: ""
        val secretKey: String = System.getenv("COS_SECRET_KEY") ?: ""
        val region: String = System.getenv("COS_REGION") ?: "ap-guangzhou"
        val bucket: String = System.getenv("COS_BUCKET") ?: ""
        val baseUrl: String = System.getenv("COS_BASE_URL") ?: ""
    }

    /** 服务器基础 URL */
    val serverBaseUrl: String = System.getenv("SERVER_BASE_URL") ?: "http://localhost:8080"

    /**
     * 获取壁纸完整 URL
     * @param filename 文件名
     * @return 完整的访问 URL
     */
    fun getWallpaperUrl(filename: String): String = when (storageType) {
        StorageType.COS -> "${Cos.baseUrl}/wallpapers/$filename"
        StorageType.LOCAL -> "$serverBaseUrl/static/wallpapers/$filename"
    }

    /**
     * 获取缩略图完整 URL
     * @param filename 文件名
     * @return 完整的访问 URL
     */
    fun getThumbnailUrl(filename: String): String = when (storageType) {
        StorageType.COS -> "${Cos.baseUrl}/thumbnails/$filename"
        StorageType.LOCAL -> "$serverBaseUrl/static/thumbnails/$filename"
    }
}
