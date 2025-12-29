package com.aking.model

import kotlinx.serialization.Serializable

@Serializable
data class Wallpaper(
    val id: Int,
    val name: String,
    val url: String,
    val thumbnailUrl: String,
    val categoryId: Int,
    val categoryName: String,
    val artistId: Int? = null,
    val artistName: String? = null,
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val width: Int,
    val height: Int,
    val downloads: Int = 0,
    val isFeatured: Boolean = false,
    val isEditorsChoice: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val thumbnailUrl: String? = null,
    val wallpaperCount: Int = 0
)

@Serializable
data class Tag(
    val name: String,
    val count: Int = 0
)

@Serializable
data class HomeResponse(
    val featured: List<Wallpaper>,
    val editorsChoice: List<Wallpaper>,
    val newArrivals: List<Wallpaper>,
    val categories: List<Category>,
    val featuredCollections: List<Collection> = emptyList(),
    val featuredArtists: List<Artist> = emptyList()
)

@Serializable
data class SearchResponse(
    val wallpapers: List<Wallpaper>,
    val artists: List<Artist> = emptyList(),
    val collections: List<Collection> = emptyList(),
    val query: String,
    val total: Int
)

// ==================== 请求 DTO ====================

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val thumbnailUrl: String? = null
)

@Serializable
data class UpdateCategoryRequest(
    val name: String? = null,
    val thumbnailUrl: String? = null
)

@Serializable
data class CreateWallpaperRequest(
    val name: String,
    val filename: String,
    val thumbnailFilename: String? = null,
    val categoryId: Int,
    val artistId: Int? = null,
    val tags: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val width: Int = 1080,
    val height: Int = 1920,
    val isFeatured: Boolean = false,
    val isEditorsChoice: Boolean = false
)

@Serializable
data class UpdateWallpaperRequest(
    val name: String? = null,
    val filename: String? = null,
    val thumbnailFilename: String? = null,
    val categoryId: Int? = null,
    val artistId: Int? = null,
    val tags: List<String>? = null,
    val colors: List<String>? = null,
    val width: Int? = null,
    val height: Int? = null,
    val isFeatured: Boolean? = null,
    val isEditorsChoice: Boolean? = null
)

// ==================== 响应 DTO ====================

@Serializable
data class UploadResponse(
    val filename: String,
    val url: String,
    val thumbnailUrl: String? = null
)

// ==================== 艺术家相关 ====================

@Serializable
data class Artist(
    val id: Int,
    val name: String,
    val avatar: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val socialLinks: Map<String, String> = emptyMap(),
    val isVerified: Boolean = false,
    val isFeatured: Boolean = false,
    val wallpaperCount: Int = 0,
    val createdAt: Long
)

@Serializable
data class ArtistDetail(
    val id: Int,
    val name: String,
    val avatar: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val socialLinks: Map<String, String> = emptyMap(),
    val isVerified: Boolean = false,
    val sponsorPackages: List<SponsorPackage> = emptyList(),
    val recentWallpapers: List<Wallpaper> = emptyList(),
    val wallpaperCount: Int = 0
)

@Serializable
data class SponsorPackage(
    val id: Int,
    val artistId: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
    val currency: String = "CNY",
    val benefits: List<String> = emptyList()
)

@Serializable
data class CreateArtistRequest(
    val name: String,
    val avatar: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val socialLinks: Map<String, String> = emptyMap()
)

@Serializable
data class UpdateArtistRequest(
    val name: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val website: String? = null,
    val socialLinks: Map<String, String>? = null,
    val isVerified: Boolean? = null,
    val isFeatured: Boolean? = null
)

@Serializable
data class CreateSponsorPackageRequest(
    val artistId: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
    val currency: String = "CNY",
    val benefits: List<String> = emptyList()
)

@Serializable
data class UpdateSponsorPackageRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val benefits: List<String>? = null,
    val isActive: Boolean? = null
)

// ==================== 打赏相关 ====================

@Serializable
data class Donation(
    val id: Int,
    val nickname: String? = null,
    val amount: Double,
    val message: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: Long
)

@Serializable
data class DonationStats(
    val totalAmount: Double,
    val totalCount: Int,
    val recentDonations: List<Donation>
)

@Serializable
data class CreateDonationRequest(
    val userId: String? = null,
    val nickname: String? = null,
    val amount: Double,
    val message: String? = null,
    val isAnonymous: Boolean = false
)

@Serializable
data class ConfirmPaymentRequest(
    val transactionId: String,
    val paymentMethod: String
)

// ==================== 专题相关 ====================

@Serializable
data class Collection(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val bannerUrl: String? = null,
    val brandName: String? = null,
    val brandLogoUrl: String? = null,
    val isSponsored: Boolean = false,
    val wallpaperCount: Int = 0,
    val isFeatured: Boolean = false
)

@Serializable
data class CollectionDetail(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val bannerUrl: String? = null,
    val brandName: String? = null,
    val brandLogoUrl: String? = null,
    val isSponsored: Boolean = false,
    val sponsorInfo: SponsorInfo? = null,
    val wallpapers: List<Wallpaper> = emptyList(),
    val wallpaperCount: Int = 0
)

@Serializable
data class SponsorInfo(
    val sponsorName: String? = null,
    val sponsorLogo: String? = null,
    val sponsorLink: String? = null,
    val campaignMessage: String? = null
)

@Serializable
data class CreateCollectionRequest(
    val name: String,
    val slug: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val bannerUrl: String? = null,
    val brandName: String? = null,
    val brandLogoUrl: String? = null,
    val isSponsored: Boolean = false,
    val sponsorInfo: SponsorInfo? = null,
    val isFeatured: Boolean = false,
    val displayOrder: Int = 0
)

@Serializable
data class UpdateCollectionRequest(
    val name: String? = null,
    val description: String? = null,
    val coverUrl: String? = null,
    val bannerUrl: String? = null,
    val brandName: String? = null,
    val brandLogoUrl: String? = null,
    val isSponsored: Boolean? = null,
    val sponsorInfo: SponsorInfo? = null,
    val isFeatured: Boolean? = null,
    val displayOrder: Int? = null
)

@Serializable
data class AddWallpaperToCollectionRequest(
    val wallpaperId: Int,
    val displayOrder: Int = 0
)

// ==================== 赞助订单相关 ====================

@Serializable
data class SponsorOrder(
    val id: Int,
    val packageId: Int,
    val packageName: String,
    val artistId: Int,
    val artistName: String,
    val userId: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val createdAt: Long,
    val paidAt: Long? = null
)

@Serializable
data class CreateSponsorOrderRequest(
    val packageId: Int,
    val userId: String
)

@Serializable
data class SponsorStats(
    val totalAmount: Double,
    val totalCount: Int,
    val recentOrders: List<SponsorOrder>
)
