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

class WallpaperEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WallpaperEntity>(Wallpapers)

    var name by Wallpapers.name
    var filename by Wallpapers.filename
    var thumbnailFilename by Wallpapers.thumbnailFilename
    var category by CategoryEntity referencedOn Wallpapers.categoryId
    var colors by Wallpapers.colors
    var width by Wallpapers.width
    var height by Wallpapers.height
    var downloads by Wallpapers.downloads
    var isFeatured by Wallpapers.isFeatured
    var isEditorsChoice by Wallpapers.isEditorsChoice
    var createdAt by Wallpapers.createdAt
    var updatedAt by Wallpapers.updatedAt
}
