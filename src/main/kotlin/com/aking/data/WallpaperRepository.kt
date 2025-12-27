package com.aking.data

import com.aking.model.Category
import com.aking.model.Tag
import com.aking.model.Wallpaper

object WallpaperRepository {

    private val categories = listOf(
        Category("abstract", "Abstract", "https://example.com/thumbnails/abstract.jpg", 420),
        Category("nature", "Nature", "https://example.com/thumbnails/nature.jpg", 385),
        Category("minimal", "Minimal", "https://example.com/thumbnails/minimal.jpg", 501),
        Category("amoled", "Amoled", "https://example.com/thumbnails/amoled.jpg", 276),
        Category("gradient", "Gradient", "https://example.com/thumbnails/gradient.jpg", 198),
        Category("architecture", "Architecture", "https://example.com/thumbnails/architecture.jpg", 142)
    )

    private val wallpapers = mutableListOf(
        // Abstract
        Wallpaper("1", "Vibrant Waves", "https://example.com/wallpapers/vibrant-waves.jpg", "https://example.com/thumbnails/vibrant-waves.jpg", "abstract", listOf("colorful", "waves", "abstract"), listOf("#FF6B6B", "#4ECDC4"), 1080, 1920, 1250, isFeatured = true, isEditorsChoice = true),
        Wallpaper("2", "Alpine Glow", "https://example.com/wallpapers/alpine-glow.jpg", "https://example.com/thumbnails/alpine-glow.jpg", "abstract", listOf("mountain", "glow", "abstract"), listOf("#667eea", "#764ba2"), 1080, 1920, 890, isEditorsChoice = true),
        Wallpaper("3", "Neon Dreams", "https://example.com/wallpapers/neon-dreams.jpg", "https://example.com/thumbnails/neon-dreams.jpg", "abstract", listOf("neon", "colorful"), listOf("#f093fb", "#f5576c"), 1080, 1920, 2100, isFeatured = true),

        // Nature
        Wallpaper("4", "Forest Morning", "https://example.com/wallpapers/forest-morning.jpg", "https://example.com/thumbnails/forest-morning.jpg", "nature", listOf("forest", "morning", "green"), listOf("#134E5E", "#71B280"), 1080, 1920, 1560),
        Wallpaper("5", "Ocean Sunset", "https://example.com/wallpapers/ocean-sunset.jpg", "https://example.com/thumbnails/ocean-sunset.jpg", "nature", listOf("ocean", "sunset", "beach"), listOf("#ff7e5f", "#feb47b"), 1080, 1920, 2340, isFeatured = true, isEditorsChoice = true),
        Wallpaper("6", "Mountain Peak", "https://example.com/wallpapers/mountain-peak.jpg", "https://example.com/thumbnails/mountain-peak.jpg", "nature", listOf("mountain", "snow", "landscape"), listOf("#E6DADA", "#274046"), 1080, 1920, 980),

        // Minimal
        Wallpaper("7", "Clean Lines", "https://example.com/wallpapers/clean-lines.jpg", "https://example.com/thumbnails/clean-lines.jpg", "minimal", listOf("minimal", "lines", "simple"), listOf("#FFFFFF", "#000000"), 1080, 1920, 1120),
        Wallpaper("8", "Soft Gradient", "https://example.com/wallpapers/soft-gradient.jpg", "https://example.com/thumbnails/soft-gradient.jpg", "minimal", listOf("gradient", "minimal", "soft"), listOf("#a8edea", "#fed6e3"), 1080, 1920, 1890, isEditorsChoice = true),
        Wallpaper("9", "Geometric", "https://example.com/wallpapers/geometric.jpg", "https://example.com/thumbnails/geometric.jpg", "minimal", listOf("geometric", "shapes", "minimal"), listOf("#ee9ca7", "#ffdde1"), 1080, 1920, 756),

        // Amoled
        Wallpaper("10", "OLED Dark", "https://example.com/wallpapers/oled-dark.jpg", "https://example.com/thumbnails/oled-dark.jpg", "amoled", listOf("dark", "black", "amoled"), listOf("#000000", "#1a1a2e"), 1080, 1920, 3200, isFeatured = true),
        Wallpaper("11", "Dark Space", "https://example.com/wallpapers/dark-space.jpg", "https://example.com/thumbnails/dark-space.jpg", "amoled", listOf("space", "stars", "dark"), listOf("#000000", "#16213e"), 1080, 1920, 2870),
        Wallpaper("12", "Abstract 3D", "https://example.com/wallpapers/abstract-3d.jpg", "https://example.com/thumbnails/abstract-3d.jpg", "amoled", listOf("3d", "abstract", "dark"), listOf("#000000", "#0f3460"), 1080, 1920, 1450, isEditorsChoice = true),

        // Gradient
        Wallpaper("13", "Liquid Gradient", "https://example.com/wallpapers/liquid-gradient.jpg", "https://example.com/thumbnails/liquid-gradient.jpg", "gradient", listOf("gradient", "liquid", "colorful"), listOf("#667eea", "#764ba2"), 1080, 1920, 1670, isFeatured = true),
        Wallpaper("14", "Green Abstract", "https://example.com/wallpapers/green-abstract.jpg", "https://example.com/thumbnails/green-abstract.jpg", "gradient", listOf("green", "abstract", "gradient"), listOf("#11998e", "#38ef7d"), 1080, 1920, 890),
        Wallpaper("15", "Matcha Green", "https://example.com/wallpapers/matcha-green.jpg", "https://example.com/thumbnails/matcha-green.jpg", "gradient", listOf("green", "matcha", "soft"), listOf("#96e6a1", "#d4fc79"), 1080, 1920, 1230),

        // Architecture
        Wallpaper("16", "Cyberpunk City", "https://example.com/wallpapers/cyberpunk-city.jpg", "https://example.com/thumbnails/cyberpunk-city.jpg", "architecture", listOf("city", "cyberpunk", "neon"), listOf("#0c0c0c", "#e94560"), 1080, 1920, 2560, isFeatured = true, isEditorsChoice = true),
        Wallpaper("17", "Minimalist Mountains", "https://example.com/wallpapers/minimalist-mountains.jpg", "https://example.com/thumbnails/minimalist-mountains.jpg", "architecture", listOf("mountain", "minimal"), listOf("#2C3E50", "#4CA1AF"), 1080, 1920, 1100),
        Wallpaper("18", "Pixel Art City", "https://example.com/wallpapers/pixel-art-city.jpg", "https://example.com/thumbnails/pixel-art-city.jpg", "architecture", listOf("pixel", "city", "retro"), listOf("#141E30", "#243B55"), 1080, 1920, 1890)
    )

    fun getAllCategories(): List<Category> = categories

    fun getCategoryById(id: String): Category? = categories.find { it.id == id }

    fun getAllWallpapers(page: Int = 1, pageSize: Int = 20): List<Wallpaper> {
        val start = (page - 1) * pageSize
        return wallpapers.drop(start).take(pageSize)
    }

    fun getWallpaperCount(): Int = wallpapers.size

    fun getWallpaperById(id: String): Wallpaper? = wallpapers.find { it.id == id }

    fun getWallpapersByCategory(categoryId: String, page: Int = 1, pageSize: Int = 20): List<Wallpaper> {
        val filtered = wallpapers.filter { it.categoryId == categoryId }
        val start = (page - 1) * pageSize
        return filtered.drop(start).take(pageSize)
    }

    fun getWallpaperCountByCategory(categoryId: String): Int =
        wallpapers.count { it.categoryId == categoryId }

    fun getFeaturedWallpapers(limit: Int = 6): List<Wallpaper> =
        wallpapers.filter { it.isFeatured }.take(limit)

    fun getEditorsChoiceWallpapers(limit: Int = 6): List<Wallpaper> =
        wallpapers.filter { it.isEditorsChoice }.take(limit)

    fun getNewArrivals(limit: Int = 10): List<Wallpaper> =
        wallpapers.sortedByDescending { it.createdAt }.take(limit)

    fun searchWallpapers(query: String): List<Wallpaper> {
        val lowerQuery = query.lowercase()
        return wallpapers.filter { wallpaper ->
            wallpaper.name.lowercase().contains(lowerQuery) ||
            wallpaper.tags.any { it.lowercase().contains(lowerQuery) } ||
            wallpaper.categoryId.lowercase().contains(lowerQuery)
        }
    }

    fun getPopularTags(limit: Int = 10): List<Tag> {
        return wallpapers
            .flatMap { it.tags }
            .groupingBy { it }
            .eachCount()
            .map { Tag(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(limit)
    }

    fun getWallpapersByTag(tag: String, page: Int = 1, pageSize: Int = 20): List<Wallpaper> {
        val filtered = wallpapers.filter { it.tags.contains(tag.lowercase()) }
        val start = (page - 1) * pageSize
        return filtered.drop(start).take(pageSize)
    }
}
