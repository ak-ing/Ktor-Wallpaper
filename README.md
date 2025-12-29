# Ktor Wallpaper

壁纸 App 服务端 REST API，基于 Ktor 框架开发。

## 功能特性

- 壁纸管理（列表、精选、编辑推荐、最新）
- 分类管理
- 艺术家管理
- 专题/合集管理
- 搜索和标签
- 打赏和赞助功能
- 管理后台接口

## 技术栈

- Kotlin 2.1
- Ktor 3.0.3
- Exposed ORM
- PostgreSQL
- HikariCP 连接池
- kotlinx.serialization

## 运行项目

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

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/wallpaper
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
STORAGE_TYPE=LOCAL  # 或 COS
SERVER_BASE_URL=http://localhost:8080
```

## API 文档

详见 [API.md](./API.md)
