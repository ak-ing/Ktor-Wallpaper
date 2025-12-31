# Ktor Wallpaper

壁纸 App 服务端 REST API，基于 Ktor 框架开发。

## 功能特性

- 壁纸管理（列表、精选、编辑推荐、最新）
- 分类管理
- 艺术家管理（作品、赞助包）
- 专题/合集管理（品牌联名）
- 搜索和标签
- 打赏和赞助功能
- 文件上传
- 管理后台接口

## 技术栈

- Kotlin 2.1
- Ktor 3.0.3
- Exposed ORM
- PostgreSQL
- HikariCP 连接池
- kotlinx.serialization

## 快速开始

### 前置条件

- JDK 17+
- PostgreSQL 数据库

### 运行项目

```bash
# 运行服务器 (端口 8080)
./gradlew run

# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 构建 Fat JAR
./gradlew buildFatJar

# 构建 Docker 镜像
./gradlew buildImage
```

## 环境变量

### 数据库配置

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/wallpaper
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
```

### 存储配置

```bash
STORAGE_TYPE=LOCAL              # LOCAL 或 COS
SERVER_BASE_URL=http://localhost:8080
```

本地存储：
```bash
WALLPAPER_PATH=data/wallpapers
THUMBNAIL_PATH=data/thumbnails
```

腾讯云 COS：
```bash
COS_SECRET_ID=your_secret_id
COS_SECRET_KEY=your_secret_key
COS_REGION=ap-guangzhou
COS_BUCKET=your_bucket_name
COS_BASE_URL=https://your-bucket.cos.ap-guangzhou.myqcloud.com
```

## 项目结构

```
src/main/kotlin/
├── Application.kt          # 入口点
└── com/aking/
    ├── config/             # 存储配置
    ├── database/           # 表定义、实体、数据库配置
    ├── data/               # Repository 层
    ├── model/              # DTO 和响应模型
    └── routes/             # API 路由
```

## API 文档

详见 [API.md](./API.md)
