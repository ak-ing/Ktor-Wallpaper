package com.aking.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CategoryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CategoryEntity>(Categories)

    var name by Categories.name
    var thumbnailUrl by Categories.thumbnailUrl
    var createdAt by Categories.createdAt
    var updatedAt by Categories.updatedAt

    val wallpapers by WallpaperEntity referrersOn Wallpapers.categoryId
}

class ArtistEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistEntity>(Artists)

    var name by Artists.name
    var avatar by Artists.avatar
    var bio by Artists.bio
    var website by Artists.website
    var socialLinks by Artists.socialLinks
    var isVerified by Artists.isVerified
    var isFeatured by Artists.isFeatured
    var totalEarnings by Artists.totalEarnings
    var createdAt by Artists.createdAt
    var updatedAt by Artists.updatedAt

    val wallpapers by WallpaperEntity optionalReferrersOn Wallpapers.artistId
    val sponsorPackages by ArtistSponsorPackageEntity referrersOn ArtistSponsorPackages.artistId
}

class WallpaperEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WallpaperEntity>(Wallpapers)

    var name by Wallpapers.name
    var filename by Wallpapers.filename
    var thumbnailFilename by Wallpapers.thumbnailFilename
    var category by CategoryEntity referencedOn Wallpapers.categoryId
    var artist by ArtistEntity optionalReferencedOn Wallpapers.artistId
    var colors by Wallpapers.colors
    var width by Wallpapers.width
    var height by Wallpapers.height
    var downloads by Wallpapers.downloads
    var isFeatured by Wallpapers.isFeatured
    var isEditorsChoice by Wallpapers.isEditorsChoice
    var createdAt by Wallpapers.createdAt
    var updatedAt by Wallpapers.updatedAt
}

class ArtistSponsorPackageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ArtistSponsorPackageEntity>(ArtistSponsorPackages)

    var artist by ArtistEntity referencedOn ArtistSponsorPackages.artistId
    var name by ArtistSponsorPackages.name
    var description by ArtistSponsorPackages.description
    var price by ArtistSponsorPackages.price
    var currency by ArtistSponsorPackages.currency
    var benefits by ArtistSponsorPackages.benefits
    var isActive by ArtistSponsorPackages.isActive
    var createdAt by ArtistSponsorPackages.createdAt
    var updatedAt by ArtistSponsorPackages.updatedAt
}

class SponsorOrderEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SponsorOrderEntity>(SponsorOrders)

    var sponsorPackage by ArtistSponsorPackageEntity referencedOn SponsorOrders.packageId
    var artist by ArtistEntity referencedOn SponsorOrders.artistId
    var userId by SponsorOrders.userId
    var amount by SponsorOrders.amount
    var currency by SponsorOrders.currency
    var status by SponsorOrders.status
    var paymentMethod by SponsorOrders.paymentMethod
    var transactionId by SponsorOrders.transactionId
    var createdAt by SponsorOrders.createdAt
    var paidAt by SponsorOrders.paidAt
}

class DonationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DonationEntity>(Donations)

    var userId by Donations.userId
    var nickname by Donations.nickname
    var amount by Donations.amount
    var currency by Donations.currency
    var message by Donations.message
    var status by Donations.status
    var paymentMethod by Donations.paymentMethod
    var transactionId by Donations.transactionId
    var isAnonymous by Donations.isAnonymous
    var createdAt by Donations.createdAt
    var paidAt by Donations.paidAt
}

class CollectionEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CollectionEntity>(Collections)

    var name by Collections.name
    var slug by Collections.slug
    var description by Collections.description
    var coverUrl by Collections.coverUrl
    var bannerUrl by Collections.bannerUrl
    var brandName by Collections.brandName
    var brandLogoUrl by Collections.brandLogoUrl
    var isSponsored by Collections.isSponsored
    var sponsorInfo by Collections.sponsorInfo
    var isFeatured by Collections.isFeatured
    var displayOrder by Collections.displayOrder
    var startAt by Collections.startAt
    var endAt by Collections.endAt
    var createdAt by Collections.createdAt
    var updatedAt by Collections.updatedAt
}
