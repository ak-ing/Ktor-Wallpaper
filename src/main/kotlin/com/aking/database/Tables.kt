package com.aking.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.math.BigDecimal

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
 * 艺术家表
 * 存储入驻艺术家/画师信息
 */
object Artists : IntIdTable("artists") {
    val name = varchar("name", 100)                         // 艺术家名称
    val avatar = varchar("avatar", 500).nullable()          // 头像 URL
    val bio = text("bio").nullable()                        // 个人简介
    val website = varchar("website", 500).nullable()        // 个人网站
    val socialLinks = text("social_links").default("{}")    // 社交链接 JSON
    val isVerified = bool("is_verified").default(false)     // 是否认证艺术家
    val isFeatured = bool("is_featured").default(false)     // 是否推荐艺术家
    val totalEarnings = decimal("total_earnings", 12, 2).default(BigDecimal.ZERO)  // 累计收益
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
    val artistId = reference("artist_id", Artists).nullable()  // 所属艺术家（可选）
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

/**
 * 艺术家赞助包表
 * 用户可购买支持画师的赞助包
 */
object ArtistSponsorPackages : IntIdTable("artist_sponsor_packages") {
    val artistId = reference("artist_id", Artists)          // 所属艺术家
    val name = varchar("name", 100)                         // 包名称
    val description = text("description").nullable()        // 描述
    val price = decimal("price", 10, 2)                     // 价格
    val currency = varchar("currency", 10).default("CNY")   // 货币类型
    val benefits = text("benefits").default("[]")           // 权益列表 JSON
    val isActive = bool("is_active").default(true)          // 是否上架
    val createdAt = timestamp("created_at")                 // 创建时间
    val updatedAt = timestamp("updated_at")                 // 更新时间
}

/**
 * 赞助订单表
 * 记录用户对艺术家的赞助购买
 */
object SponsorOrders : IntIdTable("sponsor_orders") {
    val packageId = reference("package_id", ArtistSponsorPackages)  // 赞助包 ID
    val artistId = reference("artist_id", Artists)                  // 艺术家 ID
    val userId = varchar("user_id", 100)                            // 用户标识
    val amount = decimal("amount", 10, 2)                           // 支付金额
    val currency = varchar("currency", 10).default("CNY")           // 货币类型
    val status = varchar("status", 20).default("pending")           // pending/paid/refunded
    val paymentMethod = varchar("payment_method", 50).nullable()    // 支付方式
    val transactionId = varchar("transaction_id", 200).nullable()   // 第三方交易号
    val createdAt = timestamp("created_at")                         // 创建时间
    val paidAt = timestamp("paid_at").nullable()                    // 支付时间
}

/**
 * 打赏表
 * "请开发者喝咖啡" 功能
 */
object Donations : IntIdTable("donations") {
    val userId = varchar("user_id", 100).nullable()         // 用户标识（可匿名）
    val nickname = varchar("nickname", 100).nullable()      // 显示昵称
    val amount = decimal("amount", 10, 2)                   // 打赏金额
    val currency = varchar("currency", 10).default("CNY")   // 货币类型
    val message = varchar("message", 500).nullable()        // 留言
    val status = varchar("status", 20).default("pending")   // pending/paid/refunded
    val paymentMethod = varchar("payment_method", 50).nullable()    // 支付方式
    val transactionId = varchar("transaction_id", 200).nullable()   // 第三方交易号
    val isAnonymous = bool("is_anonymous").default(false)   // 是否匿名
    val createdAt = timestamp("created_at")                 // 创建时间
    val paidAt = timestamp("paid_at").nullable()            // 支付时间
}

/**
 * 专题/合集表
 * 支持品牌联名专题，如 "Nothing Tech 风格专区"
 */
object Collections : IntIdTable("collections") {
    val name = varchar("name", 200)                         // 专题名称
    val slug = varchar("slug", 100).uniqueIndex()           // URL 友好标识
    val description = text("description").nullable()        // 专题描述
    val coverUrl = varchar("cover_url", 500).nullable()     // 封面图
    val bannerUrl = varchar("banner_url", 500).nullable()   // 横幅图
    val brandName = varchar("brand_name", 100).nullable()   // 品牌名称
    val brandLogoUrl = varchar("brand_logo_url", 500).nullable()  // 品牌 Logo
    val isSponsored = bool("is_sponsored").default(false)   // 是否赞助专题
    val sponsorInfo = text("sponsor_info").nullable()       // 赞助信息 JSON
    val isFeatured = bool("is_featured").default(false)     // 是否首页展示
    val displayOrder = integer("display_order").default(0)  // 排序权重
    val startAt = timestamp("start_at").nullable()          // 活动开始时间
    val endAt = timestamp("end_at").nullable()              // 活动结束时间
    val createdAt = timestamp("created_at")                 // 创建时间
    val updatedAt = timestamp("updated_at")                 // 更新时间
}

/**
 * 专题壁纸关联表
 * 多对多关系
 */
object CollectionWallpapers : Table("collection_wallpapers") {
    val collectionId = reference("collection_id", Collections)  // 专题 ID
    val wallpaperId = reference("wallpaper_id", Wallpapers)     // 壁纸 ID
    val displayOrder = integer("display_order").default(0)      // 在专题内的排序

    override val primaryKey = PrimaryKey(collectionId, wallpaperId)

    init {
        index(true, collectionId, wallpaperId)
        index(false, collectionId)
    }
}
