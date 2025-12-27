# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Ktor server application providing REST API for a wallpaper mobile app. Serves wallpapers with categories, tags, search, and featured content sections.

## Build Commands

```bash
# Run the server (port 8080)
./gradlew run

# Build everything
./gradlew build

# Run tests
./gradlew test

# Build executable fat JAR
./gradlew buildFatJar

# Docker operations
./gradlew buildImage
./gradlew runDocker
```

## Architecture

```
src/main/kotlin/com/aking/
├── Application.kt          # Entry point, server config, serialization setup
├── Routing.kt              # Main routing configuration
├── model/
│   └── Models.kt           # Data classes: Wallpaper, Category, Tag, Response DTOs
├── data/
│   └── WallpaperRepository.kt  # In-memory data store with sample data
└── routes/
    ├── HomeRoutes.kt       # GET /api/home - aggregated home screen data
    ├── WallpaperRoutes.kt  # /api/wallpapers endpoints
    ├── CategoryRoutes.kt   # /api/categories endpoints
    └── SearchRoutes.kt     # /api/search, /api/tags endpoints
```

**Entry Point:** `Application.kt:10` - `main()` starts Netty on port 8080

**Configuration Pattern:** Each feature has its own `Application.configure*()` extension function:
- `configureSerialization()` - JSON content negotiation
- `configureRouting()` - HTTP routes

## API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/home` | Aggregated home screen data (featured, editors choice, new arrivals, categories) |
| `GET /api/wallpapers` | List wallpapers with pagination (?page=1&pageSize=20) |
| `GET /api/wallpapers/featured` | Featured wallpapers |
| `GET /api/wallpapers/editors-choice` | Editor's choice wallpapers |
| `GET /api/wallpapers/new` | New arrivals |
| `GET /api/wallpapers/{id}` | Single wallpaper details |
| `GET /api/categories` | All categories with counts |
| `GET /api/categories/{id}/wallpapers` | Wallpapers by category |
| `GET /api/search?q=query` | Search wallpapers |
| `GET /api/tags/popular` | Popular tags |

## Tech Stack

- **Kotlin** with kotlinx.serialization
- **Ktor** (server-core, server-netty, content-negotiation)
- **Logback** for logging
- **Gradle** with version catalog (versions in `gradle/libs.versions.toml`)

## Testing

Uses `ktor-server-test-host` with JUnit:

```kotlin
@Test
fun testHome() = testApplication {
    application { module() }
    client.get("/api/home").apply {
        assertEquals(HttpStatusCode.OK, status)
    }
}
```
