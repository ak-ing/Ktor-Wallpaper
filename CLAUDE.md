# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ktor REST API server for a wallpaper mobile app. Features wallpapers, categories, artists, collections (brand partnerships), donations, and sponsor packages. Uses PostgreSQL with Exposed ORM.

## Build Commands

```bash
./gradlew run              # Run server on port 8080
./gradlew build            # Build project
./gradlew test             # Run tests
./gradlew test --tests "TestClassName"  # Run single test class
./gradlew buildFatJar      # Build executable fat JAR
./gradlew buildImage       # Build Docker image
./gradlew runDocker        # Run with Docker
```

## Architecture

**Entry Point:** `Application.kt` → `Application.module()` initializes database, serialization, and routing.

**Data Flow:** Routes (`routes/`) → Repository objects (`data/`) → Exposed transactions → Entity.toModel() conversions

**Key Directories:**
- `config/` - Storage abstraction (LOCAL/COS)
- `database/` - Tables, Entities, DatabaseConfig (schema auto-created on startup)
- `model/` - DTOs, request/response classes, response helpers
- `data/` - Repository objects for each domain
- `routes/` - Ktor route definitions

## Key Patterns

**Unified API Response:** All endpoints use standardized response helpers from `Models.kt`:
```kotlin
call.success(data)                    // 200 OK with data
call.success<Unit>(message = "...")   // 200 OK message only
call.created(data, "message")         // 201 Created
badRequest("error message")           // 400 throws exception
notFound("error message")             // 404 throws exception
```

**Pagination:** List endpoints support `?page=1&pageSize=20`, return `PageData<T>` wrapper.

**Storage Abstraction:** `StorageConfig` reads `STORAGE_TYPE` env var (LOCAL or COS). URL generation via `getWallpaperUrl()`/`getThumbnailUrl()`.

**Repository Pattern:** Singleton objects with Exposed transaction blocks:
```kotlin
object WallpaperRepository {
    fun getAllWallpapers(page: Int, pageSize: Int) = transaction {
        WallpaperEntity.all().limit(pageSize).offset(offset).map { it.toModel() }
    }
}
```

## API Route Structure

All routes under `/api`:
- `/home` - Aggregated home data
- `/wallpapers`, `/categories`, `/artists`, `/collections` - Public CRUD
- `/search`, `/tags` - Search functionality
- `/donations`, `/sponsor` - Payment flows
- `/upload` - Multipart file uploads
- `/admin/*` - Admin CRUD for all entities

## Environment Variables

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/wallpaper
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
STORAGE_TYPE=LOCAL           # or COS
SERVER_BASE_URL=http://localhost:8080
WALLPAPER_PATH=data/wallpapers
THUMBNAIL_PATH=data/thumbnails
```

## Tech Stack

Kotlin 2.1, Ktor 3.0.3, Exposed 0.57, PostgreSQL, HikariCP, kotlinx.serialization. Versions in `gradle/libs.versions.toml`.
