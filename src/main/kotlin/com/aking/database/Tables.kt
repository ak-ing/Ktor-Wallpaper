package com.aking.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * 分类表
 * 存储壁纸分类信息
 */
object Categories : IntIdTable("categories") {
    val name = varchar("name", 100).uniqueIndex()           // 分类名称（唯一）
    val thumbnailUrl = varchar("thumbnail_url", 500).nullable()  // 分类缩略图 URL
    val createdAt = timestamp("created_at")                 // 创建时间
    val updatedAt = timestamp("updated_at")                 // 更新时间
}

/**
 * 壁纸表
 * 存储壁纸基本信息
 */
object Wallpapers : IntIdTable("wallpapers") {
    val name = varchar("name", 200)                         // 壁纸名称
    val filename = varchar("filename", 500)                 // 文件名
    val thumbnailFilename = varchar("thumbnail_filename", 500).nullable()  // 缩略图文件名
    val categoryId = reference("category_id", Categories)   // 所属分类
    val colors = text("colors").default("[]")               // 主色调 JSON 数组
    val width = integer("width").default(1080)              // 图片宽度
    val height = integer("height").default(1920)            // 图片高度
    val downloads = integer("downloads").default(0)         // 下载次数
    val isFeatured = bool("is_featured").default(false)     // 是否精选
    val isEditorsChoice = bool("is_editors_choice").default(false)  // 是否编辑推荐
    val createdAt = timestamp("created_at")                 // 创建时间
    val updatedAt = timestamp("updated_at")                 // 更新时间
}

/**
 * 壁纸标签关联表
 * 存储壁纸和标签的多对多关系
 */
object WallpaperTags : Table("wallpaper_tags") {
    val wallpaperId = reference("wallpaper_id", Wallpapers) // 壁纸 ID
    val tag = varchar("tag", 100)                           // 标签名

    override val primaryKey = PrimaryKey(wallpaperId, tag)

    init {
        index(true, wallpaperId, tag)   // 唯一索引：壁纸+标签
        index(false, tag)               // 标签索引：方便按标签查询
    }
}
