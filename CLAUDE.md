# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ktor server providing REST API for a wallpaper mobile app. Features categories, tags, search, featured content, file uploads, and admin CRUD operations. Uses PostgreSQL with Exposed ORM.

## Build Commands

```bash
./gradlew run              # Run server on port 8080
./gradlew build            # Build project
./gradlew test             # Run tests
./gradlew buildFatJar      # Build executable fat JAR
./gradlew buildImage       # Build Docker image
./gradlew runDocker        # Run with Docker
```

## Architecture

```
src/main/kotlin/
├── Application.kt              # Entry point, Netty server on :8080, module initialization
├── Routing.kt                  # Route registration, static file serving
└── com/aking/
    ├── config/
    │   └── StorageConfig.kt    # Storage abstraction (LOCAL/COS), URL generation
    ├── database/
    │   ├── DatabaseConfig.kt   # HikariCP + PostgreSQL connection, schema init
    │   ├── Tables.kt           # Exposed table definitions
    │   └── Entities.kt         # Exposed DAO entities
    ├── model/
    │   └── Models.kt           # DTOs and request/response classes
    ├── data/
    │   └── WallpaperRepository.kt  # Data access layer, all DB operations
    └── routes/
        ├── HomeRoutes.kt       # GET /api/home - aggregated data
        ├── WallpaperRoutes.kt  # /api/wallpapers - list, detail, featured
        ├── CategoryRoutes.kt   # /api/categories - CRUD
        ├── SearchRoutes.kt     # /api/search, /api/tags
        ├── UploadRoutes.kt     # /api/upload - multipart file upload
        └── AdminRoutes.kt      # /api/admin - CRUD management
```

## Key Patterns

**Configuration via Extension Functions:** `Application.module()` calls `DatabaseConfig.init()`, `configureSerialization()`, `configureRouting()`

**Data Flow:** Routes → WallpaperRepository (singleton object) → Exposed transaction blocks → Entity.toModel() conversions

**Storage Abstraction:** `StorageConfig` reads `STORAGE_TYPE` env var to switch between local files and Tencent COS. URL generation via `getWallpaperUrl()`/`getThumbnailUrl()`.

## Environment Variables

```bash
# Database (PostgreSQL)
DATABASE_URL=jdbc:postgresql://localhost:5432/wallpaper
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres

# Storage
STORAGE_TYPE=LOCAL              # or COS
SERVER_BASE_URL=http://localhost:8080
WALLPAPER_PATH=data/wallpapers
THUMBNAIL_PATH=data/thumbnails

# Tencent COS (when STORAGE_TYPE=COS)
COS_SECRET_ID=
COS_SECRET_KEY=
COS_REGION=ap-guangzhou
COS_BUCKET=
COS_BASE_URL=
```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/home` | GET | Aggregated home data |
| `/api/wallpapers` | GET | Paginated list (?page, ?pageSize) |
| `/api/wallpapers/{id}` | GET | Single wallpaper |
| `/api/wallpapers/{id}/download` | POST | Increment download count |
| `/api/wallpapers/featured` | GET | Featured wallpapers |
| `/api/wallpapers/editors-choice` | GET | Editor picks |
| `/api/wallpapers/new` | GET | Latest uploads |
| `/api/categories` | GET | All categories with counts |
| `/api/categories/{id}/wallpapers` | GET | Wallpapers by category |
| `/api/search?q=` | GET | Search by name/category/tag |
| `/api/tags/popular` | GET | Popular tags |
| `/api/tags/{tag}/wallpapers` | GET | Wallpapers by tag |
| `/api/upload/wallpaper` | POST | Upload wallpaper (multipart) |
| `/api/upload/thumbnail` | POST | Upload thumbnail |
| `/api/admin/categories` | POST/PUT/DELETE | Category management |
| `/api/admin/wallpapers` | POST/PUT/DELETE | Wallpaper management |

## Database Schema

Three tables: `categories`, `wallpapers`, `wallpaper_tags` (many-to-many). Auto-created via `SchemaUtils.create()` on startup.

## Tech Stack

- Kotlin 2.1, Ktor 3.0.3, Netty
- Exposed 0.57 ORM with DAO pattern
- PostgreSQL + HikariCP connection pool
- kotlinx.serialization for JSON
- Versions in `gradle/libs.versions.toml`
