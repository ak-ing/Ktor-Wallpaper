# Wallpaper API 接口文档

Base URL: `http://localhost:8080`

## 接口速查表

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/home | 首页聚合数据 |
| GET | /api/wallpapers | 壁纸列表（分页） |
| GET | /api/wallpapers/{id} | 壁纸详情 |
| GET | /api/wallpapers/featured | 精选壁纸 |
| GET | /api/wallpapers/editors-choice | 编辑推荐 |
| GET | /api/wallpapers/new | 最新壁纸 |
| POST | /api/wallpapers/{id}/download | 记录下载 |
| GET | /api/categories | 分类列表 |
| GET | /api/categories/{id} | 分类详情 |
| GET | /api/categories/{id}/wallpapers | 分类下的壁纸（分页） |
| GET | /api/search?q={query} | 搜索壁纸 |
| GET | /api/tags/popular | 热门标签 |
| GET | /api/tags/{tag}/wallpapers | 标签下的壁纸（分页） |
| POST | /api/upload/wallpaper | 上传壁纸图片 |
| POST | /api/upload/thumbnail | 上传缩略图 |
| POST | /api/upload/category | 上传分类缩略图 |
| POST | /api/admin/categories | 创建分类 |
| PUT | /api/admin/categories/{id} | 更新分类 |
| DELETE | /api/admin/categories/{id} | 删除分类 |
| POST | /api/admin/wallpapers | 创建壁纸 |
| PUT | /api/admin/wallpapers/{id} | 更新壁纸 |
| DELETE | /api/admin/wallpapers/{id} | 删除壁纸 |

---

## 统一响应格式

所有 API 接口使用统一的响应格式：

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

| 字段 | 类型 | 描述 |
|------|------|------|
| code | int | HTTP 状态码 |
| message | string | 响应消息 |
| data | T | 响应数据（可选） |

### 错误响应示例

```json
{
  "code": 404,
  "message": "Wallpaper not found",
  "data": null
}
```

---

## 目录

- [首页](#首页)
- [壁纸](#壁纸)
- [分类](#分类)
- [搜索](#搜索)
- [标签](#标签)
- [文件上传](#文件上传)
- [管理接口](#管理接口)

---

## 首页

### 获取首页聚合数据

**请求：** `GET /api/home`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "featured": [ /* Wallpaper[] */ ],
    "editorsChoice": [ /* Wallpaper[] */ ],
    "newArrivals": [ /* Wallpaper[] */ ],
    "categories": [ /* Category[] */ ]
  }
}
```

---

## 壁纸

### 获取壁纸列表

**请求：** `GET /api/wallpapers?page=1&pageSize=20`

| 参数 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| page | int | 1 | 页码 |
| pageSize | int | 20 | 每页数量 |

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "list": [ /* Wallpaper[] */ ],
    "page": 1,
    "pageSize": 20,
    "total": 124,
    "totalPages": 7,
    "hasMore": true
  }
}
```

---

### 获取单个壁纸

**请求：** `GET /api/wallpapers/{id}`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Mountain Sunset",
    "url": "http://localhost:8080/static/wallpapers/a1b2c3d4.jpg",
    "thumbnailUrl": "http://localhost:8080/static/thumbnails/a1b2c3d4.jpg",
    "categoryId": 1,
    "categoryName": "Nature",
    "tags": ["nature", "sunset"],
    "colors": ["#FF5733", "#C70039"],
    "width": 1080,
    "height": 1920,
    "downloads": 1250,
    "isFeatured": true,
    "isEditorsChoice": false,
    "createdAt": 1703750400000
  }
}
```

---

### 获取精选壁纸

**请求：** `GET /api/wallpapers/featured?limit=6`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Wallpaper[] */ ] }`

---

### 获取编辑推荐

**请求：** `GET /api/wallpapers/editors-choice?limit=6`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Wallpaper[] */ ] }`

---

### 获取最新壁纸

**请求：** `GET /api/wallpapers/new?limit=10`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Wallpaper[] */ ] }`

---

### 记录下载

**请求：** `POST /api/wallpapers/{id}/download`

**响应：** `{ "code": 200, "message": "Download recorded", "data": null }`

---

## 分类

### 获取所有分类

**请求：** `GET /api/categories`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Category[] */ ] }`

---

### 获取分类详情

**请求：** `GET /api/categories/{id}`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Nature",
    "thumbnailUrl": "http://localhost:8080/static/thumbnails/categories/nature.jpg",
    "wallpaperCount": 45
  }
}
```

---

### 获取分类下的壁纸

**请求：** `GET /api/categories/{id}/wallpapers?page=1&pageSize=20`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "list": [ /* Wallpaper[] */ ],
    "page": 1,
    "pageSize": 20,
    "total": 45,
    "totalPages": 3,
    "hasMore": true
  }
}
```

---

## 搜索

### 搜索壁纸

**请求：** `GET /api/search?q=sunset`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "wallpapers": [ /* Wallpaper[] */ ],
    "query": "sunset",
    "total": 2
  }
}
```

---

## 标签

### 获取热门标签

**请求：** `GET /api/tags/popular?limit=10`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    { "name": "nature", "count": 35 },
    { "name": "abstract", "count": 28 }
  ]
}
```

---

### 按标签获取壁纸

**请求：** `GET /api/tags/{tag}/wallpapers?page=1&pageSize=20`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "list": [ /* Wallpaper[] */ ],
    "page": 1,
    "pageSize": 20,
    "total": 22,
    "totalPages": 2,
    "hasMore": true
  }
}
```

---

## 文件上传

### 上传壁纸图片

**请求：** `POST /api/upload/wallpaper` (multipart/form-data, field: file)

**响应：**

```json
{
  "code": 201,
  "message": "File uploaded",
  "data": {
    "filename": "550e8400-e29b-41d4-a716-446655440000.jpg",
    "url": "http://localhost:8080/static/wallpapers/550e8400-e29b-41d4-a716-446655440000.jpg",
    "thumbnailUrl": "http://localhost:8080/static/thumbnails/550e8400-e29b-41d4-a716-446655440000.jpg"
  }
}
```

---

### 上传缩略图

**请求：** `POST /api/upload/thumbnail` (multipart/form-data, field: file)

**响应：**

```json
{
  "code": 201,
  "message": "File uploaded",
  "data": {
    "filename": "660e8400-e29b-41d4-a716-446655440001.jpg",
    "url": "http://localhost:8080/static/thumbnails/660e8400-e29b-41d4-a716-446655440001.jpg"
  }
}
```

---

### 上传分类缩略图

**请求：** `POST /api/upload/category` (multipart/form-data, field: file)

**响应：**

```json
{
  "code": 201,
  "message": "File uploaded",
  "data": {
    "filename": "categories/770e8400-e29b-41d4-a716-446655440002.jpg",
    "url": "http://localhost:8080/static/thumbnails/categories/770e8400-e29b-41d4-a716-446655440002.jpg"
  }
}
```

---

## 管理接口

### 创建分类

**请求：** `POST /api/admin/categories`

```json
{ "name": "Minimalist", "thumbnailUrl": "..." }
```

**响应：** `{ "code": 201, "message": "Category created", "data": { /* Category */ } }`

---

### 更新分类

**请求：** `PUT /api/admin/categories/{id}`

```json
{ "name": "Minimalist Art", "thumbnailUrl": "..." }
```

**响应：** `{ "code": 200, "message": "Category updated", "data": { /* Category */ } }`

---

### 删除分类

**请求：** `DELETE /api/admin/categories/{id}`

**响应：** `{ "code": 200, "message": "Category deleted", "data": null }`

---

### 创建壁纸

**请求：** `POST /api/admin/wallpapers`

```json
{
  "name": "Aurora Borealis",
  "filename": "550e8400-e29b-41d4-a716-446655440000.jpg",
  "thumbnailFilename": "660e8400-e29b-41d4-a716-446655440001.jpg",
  "categoryId": 1,
  "tags": ["aurora", "night"],
  "colors": ["#00FF00", "#0000FF"],
  "width": 1080,
  "height": 1920,
  "isFeatured": true,
  "isEditorsChoice": false
}
```

**响应：** `{ "code": 201, "message": "Wallpaper created", "data": { /* Wallpaper */ } }`

---

### 更新壁纸

**请求：** `PUT /api/admin/wallpapers/{id}`

```json
{ "name": "Northern Lights", "isFeatured": true }
```

**响应：** `{ "code": 200, "message": "Wallpaper updated", "data": { /* Wallpaper */ } }`

---

### 删除壁纸

**请求：** `DELETE /api/admin/wallpapers/{id}`

**响应：** `{ "code": 200, "message": "Wallpaper deleted", "data": null }`

---

## 数据模型

### Wallpaper

```typescript
interface Wallpaper {
  id: number
  name: string
  url: string
  thumbnailUrl: string
  categoryId: number
  categoryName: string
  tags: string[]
  colors: string[]
  width: number
  height: number
  downloads: number
  isFeatured: boolean
  isEditorsChoice: boolean
  createdAt: number  // 毫秒时间戳
}
```

### Category

```typescript
interface Category {
  id: number
  name: string
  thumbnailUrl: string | null
  wallpaperCount: number
}
```

### PageData

```typescript
interface PageData<T> {
  list: T[]
  page: number
  pageSize: number
  total: number
  totalPages: number
  hasMore: boolean
}
```

### Tag

```typescript
interface Tag {
  name: string
  count: number
}
```

### UploadResponse

```typescript
interface UploadResponse {
  filename: string
  url: string
  thumbnailUrl?: string  // 仅壁纸上传返回
}
```

---

## HTTP 状态码

| 状态码 | 描述 | 场景 |
|--------|------|------|
| 200 | OK | 请求成功 |
| 201 | Created | 创建成功（上传、新建） |
| 400 | Bad Request | 参数错误、验证失败 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |
