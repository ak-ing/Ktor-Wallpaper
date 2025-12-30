# Wallpaper API 接口文档

Base URL: `http://localhost:8080`

## 接口速查表

### 公开接口

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
| GET | /api/artists | 艺术家列表（分页） |
| GET | /api/artists/featured | 推荐艺术家 |
| GET | /api/artists/{id} | 艺术家详情 |
| GET | /api/artists/{id}/wallpapers | 艺术家作品（分页） |
| GET | /api/artists/{id}/packages | 艺术家赞助包 |
| GET | /api/collections | 专题列表（分页） |
| GET | /api/collections/featured | 推荐专题 |
| GET | /api/collections/{slug} | 专题详情 |
| GET | /api/collections/{slug}/wallpapers | 专题壁纸（分页） |
| GET | /api/search?q={query} | 搜索壁纸、艺术家、专题 |
| GET | /api/tags/popular | 热门标签 |
| GET | /api/tags/{tag}/wallpapers | 标签下的壁纸（分页） |
| POST | /api/donations | 创建打赏订单 |
| GET | /api/donations/{id} | 打赏详情 |
| POST | /api/donations/{id}/confirm | 确认打赏支付 |
| GET | /api/donations/stats | 打赏统计 |
| GET | /api/donations/recent | 最近打赏 |
| POST | /api/sponsor/order | 创建赞助订单 |
| GET | /api/sponsor/orders/{id} | 赞助订单详情 |
| POST | /api/sponsor/orders/{id}/confirm | 确认赞助支付 |

### 上传接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/upload/wallpaper | 上传壁纸图片 |
| POST | /api/upload/thumbnail | 上传缩略图 |
| POST | /api/upload/category | 上传分类缩略图 |

### 管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/admin/categories | 创建分类 |
| PUT | /api/admin/categories/{id} | 更新分类 |
| DELETE | /api/admin/categories/{id} | 删除分类 |
| POST | /api/admin/wallpapers | 创建壁纸 |
| PUT | /api/admin/wallpapers/{id} | 更新壁纸 |
| DELETE | /api/admin/wallpapers/{id} | 删除壁纸 |
| POST | /api/admin/artists | 创建艺术家 |
| PUT | /api/admin/artists/{id} | 更新艺术家 |
| DELETE | /api/admin/artists/{id} | 删除艺术家 |
| POST | /api/admin/artists/{id}/verify | 认证艺术家 |
| POST | /api/admin/artists/{id}/unverify | 取消认证 |
| POST | /api/admin/sponsor-packages | 创建赞助包 |
| PUT | /api/admin/sponsor-packages/{id} | 更新赞助包 |
| DELETE | /api/admin/sponsor-packages/{id} | 删除赞助包 |
| POST | /api/admin/collections | 创建专题 |
| PUT | /api/admin/collections/{id} | 更新专题 |
| DELETE | /api/admin/collections/{id} | 删除专题 |
| POST | /api/admin/collections/{id}/wallpapers | 添加壁纸到专题 |
| DELETE | /api/admin/collections/{id}/wallpapers/{wallpaperId} | 从专题移除壁纸 |
| GET | /api/admin/donations | 打赏列表（分页） |
| GET | /api/admin/donations/stats | 打赏统计 |
| GET | /api/admin/sponsor-orders | 赞助订单列表（分页） |
| GET | /api/admin/sponsor-orders/stats | 赞助统计 |

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
- [艺术家](#艺术家)
- [专题](#专题)
- [搜索](#搜索)
- [标签](#标签)
- [打赏](#打赏)
- [赞助](#赞助)
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
    "categories": [ /* Category[] */ ],
    "featuredCollections": [ /* Collection[] */ ],
    "featuredArtists": [ /* Artist[] */ ]
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
    "artistId": 1,
    "artistName": "John Doe",
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

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Wallpaper> */ } }`

---

## 艺术家

### 获取艺术家列表

**请求：** `GET /api/artists?page=1&pageSize=20`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "list": [ /* Artist[] */ ],
    "page": 1,
    "pageSize": 20,
    "total": 50,
    "totalPages": 3,
    "hasMore": true
  }
}
```

---

### 获取推荐艺术家

**请求：** `GET /api/artists/featured?limit=6`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Artist[] */ ] }`

---

### 获取艺术家详情

**请求：** `GET /api/artists/{id}`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "John Doe",
    "avatar": "http://localhost:8080/static/avatars/john.jpg",
    "bio": "Digital artist from Tokyo",
    "website": "https://johndoe.art",
    "socialLinks": {
      "twitter": "johndoe",
      "instagram": "johndoe_art"
    },
    "isVerified": true,
    "isFeatured": true,
    "wallpaperCount": 25,
    "totalDownloads": 12500,
    "createdAt": 1703750400000
  }
}
```

---

### 获取艺术家作品

**请求：** `GET /api/artists/{id}/wallpapers?page=1&pageSize=20`

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Wallpaper> */ } }`

---

### 获取艺术家赞助包

**请求：** `GET /api/artists/{id}/packages`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "artistId": 1,
      "name": "Coffee Supporter",
      "description": "Buy me a coffee",
      "price": 9.99,
      "currency": "CNY",
      "benefits": ["Early access to new wallpapers", "Name in credits"],
      "isActive": true
    }
  ]
}
```

---

## 专题

### 获取专题列表

**请求：** `GET /api/collections?page=1&pageSize=20`

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Collection> */ } }`

---

### 获取推荐专题

**请求：** `GET /api/collections/featured?limit=6`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Collection[] */ ] }`

---

### 获取专题详情

**请求：** `GET /api/collections/{slug}`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Nothing Tech Style",
    "slug": "nothing-tech-style",
    "description": "Minimalist wallpapers inspired by Nothing Tech",
    "coverUrl": "http://localhost:8080/static/collections/nothing-cover.jpg",
    "bannerUrl": "http://localhost:8080/static/collections/nothing-banner.jpg",
    "brandName": "Nothing",
    "brandLogoUrl": "http://localhost:8080/static/brands/nothing-logo.png",
    "isSponsored": true,
    "sponsorInfo": { "link": "https://nothing.tech" },
    "isFeatured": true,
    "wallpaperCount": 15,
    "createdAt": 1703750400000
  }
}
```

---

### 获取专题壁纸

**请求：** `GET /api/collections/{slug}/wallpapers?page=1&pageSize=20`

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Wallpaper> */ } }`

---

## 搜索

### 搜索壁纸、艺术家、专题

**请求：** `GET /api/search?q=sunset`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "wallpapers": [ /* Wallpaper[] */ ],
    "artists": [ /* Artist[] */ ],
    "collections": [ /* Collection[] */ ],
    "query": "sunset",
    "total": 15
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

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Wallpaper> */ } }`

---

## 打赏

### 创建打赏订单

**请求：** `POST /api/donations`

```json
{
  "userId": "user123",
  "nickname": "Coffee Lover",
  "amount": 9.99,
  "currency": "CNY",
  "message": "Great app!",
  "isAnonymous": false
}
```

**响应：**

```json
{
  "code": 201,
  "message": "Donation order created",
  "data": {
    "id": 1,
    "userId": "user123",
    "nickname": "Coffee Lover",
    "amount": 9.99,
    "currency": "CNY",
    "message": "Great app!",
    "status": "pending",
    "isAnonymous": false,
    "createdAt": 1703750400000
  }
}
```

---

### 获取打赏详情

**请求：** `GET /api/donations/{id}`

**响应：** `{ "code": 200, "message": "Success", "data": { /* Donation */ } }`

---

### 确认打赏支付

**请求：** `POST /api/donations/{id}/confirm`

```json
{
  "transactionId": "wx_pay_123456",
  "paymentMethod": "wechat"
}
```

**响应：** `{ "code": 200, "message": "Donation confirmed", "data": null }`

---

### 获取打赏统计

**请求：** `GET /api/donations/stats`

**响应：**

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "totalAmount": 1234.56,
    "totalCount": 100,
    "todayAmount": 56.78,
    "todayCount": 5
  }
}
```

---

### 获取最近打赏

**请求：** `GET /api/donations/recent?limit=10`

**响应：** `{ "code": 200, "message": "Success", "data": [ /* Donation[] */ ] }`

---

## 赞助

### 创建赞助订单

**请求：** `POST /api/sponsor/order`

```json
{
  "packageId": 1,
  "userId": "user123"
}
```

**响应：**

```json
{
  "code": 201,
  "message": "Sponsor order created",
  "data": {
    "id": 1,
    "packageId": 1,
    "artistId": 1,
    "userId": "user123",
    "amount": 29.99,
    "currency": "CNY",
    "status": "pending",
    "createdAt": 1703750400000
  }
}
```

---

### 获取赞助订单详情

**请求：** `GET /api/sponsor/orders/{id}`

**响应：** `{ "code": 200, "message": "Success", "data": { /* SponsorOrder */ } }`

---

### 确认赞助支付

**请求：** `POST /api/sponsor/orders/{id}/confirm`

```json
{
  "transactionId": "alipay_123456",
  "paymentMethod": "alipay"
}
```

**响应：** `{ "code": 200, "message": "Order confirmed", "data": null }`

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

### 分类管理

#### 创建分类

**请求：** `POST /api/admin/categories`

```json
{ "name": "Minimalist", "thumbnailUrl": "..." }
```

**响应：** `{ "code": 201, "message": "Category created", "data": { /* Category */ } }`

---

#### 更新分类

**请求：** `PUT /api/admin/categories/{id}`

```json
{ "name": "Minimalist Art", "thumbnailUrl": "..." }
```

**响应：** `{ "code": 200, "message": "Category updated", "data": { /* Category */ } }`

---

#### 删除分类

**请求：** `DELETE /api/admin/categories/{id}`

**响应：** `{ "code": 200, "message": "Category deleted", "data": null }`

---

### 壁纸管理

#### 创建壁纸

**请求：** `POST /api/admin/wallpapers`

```json
{
  "name": "Aurora Borealis",
  "filename": "550e8400-e29b-41d4-a716-446655440000.jpg",
  "thumbnailFilename": "660e8400-e29b-41d4-a716-446655440001.jpg",
  "categoryId": 1,
  "artistId": 1,
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

#### 更新壁纸

**请求：** `PUT /api/admin/wallpapers/{id}`

```json
{ "name": "Northern Lights", "isFeatured": true }
```

**响应：** `{ "code": 200, "message": "Wallpaper updated", "data": { /* Wallpaper */ } }`

---

#### 删除壁纸

**请求：** `DELETE /api/admin/wallpapers/{id}`

**响应：** `{ "code": 200, "message": "Wallpaper deleted", "data": null }`

---

### 艺术家管理

#### 创建艺术家

**请求：** `POST /api/admin/artists`

```json
{
  "name": "Jane Smith",
  "avatar": "https://example.com/avatar.jpg",
  "bio": "Digital artist specializing in abstract art",
  "website": "https://janesmith.art",
  "socialLinks": { "twitter": "janesmith", "instagram": "janesmith_art" },
  "isFeatured": false
}
```

**响应：** `{ "code": 201, "message": "Artist created", "data": { /* Artist */ } }`

---

#### 更新艺术家

**请求：** `PUT /api/admin/artists/{id}`

```json
{ "bio": "Updated bio", "isFeatured": true }
```

**响应：** `{ "code": 200, "message": "Artist updated", "data": { /* Artist */ } }`

---

#### 删除艺术家

**请求：** `DELETE /api/admin/artists/{id}`

**响应：** `{ "code": 200, "message": "Artist deleted", "data": null }`

---

#### 认证艺术家

**请求：** `POST /api/admin/artists/{id}/verify`

**响应：** `{ "code": 200, "message": "Artist verified", "data": null }`

---

#### 取消认证

**请求：** `POST /api/admin/artists/{id}/unverify`

**响应：** `{ "code": 200, "message": "Artist unverified", "data": null }`

---

### 赞助包管理

#### 创建赞助包

**请求：** `POST /api/admin/sponsor-packages`

```json
{
  "artistId": 1,
  "name": "Super Supporter",
  "description": "Get exclusive content",
  "price": 29.99,
  "currency": "CNY",
  "benefits": ["Exclusive wallpapers", "Early access", "Discord role"],
  "isActive": true
}
```

**响应：** `{ "code": 201, "message": "Sponsor package created", "data": { /* SponsorPackage */ } }`

---

#### 更新赞助包

**请求：** `PUT /api/admin/sponsor-packages/{id}`

```json
{ "price": 39.99, "isActive": true }
```

**响应：** `{ "code": 200, "message": "Sponsor package updated", "data": { /* SponsorPackage */ } }`

---

#### 删除赞助包

**请求：** `DELETE /api/admin/sponsor-packages/{id}`

**响应：** `{ "code": 200, "message": "Sponsor package deleted", "data": null }`

---

### 专题管理

#### 创建专题

**请求：** `POST /api/admin/collections`

```json
{
  "name": "Summer Vibes",
  "slug": "summer-vibes",
  "description": "Bright and colorful summer wallpapers",
  "coverUrl": "https://example.com/cover.jpg",
  "bannerUrl": "https://example.com/banner.jpg",
  "brandName": null,
  "brandLogoUrl": null,
  "isSponsored": false,
  "isFeatured": true,
  "displayOrder": 1
}
```

**响应：** `{ "code": 201, "message": "Collection created", "data": { /* Collection */ } }`

---

#### 更新专题

**请求：** `PUT /api/admin/collections/{id}`

```json
{ "description": "Updated description", "isFeatured": true }
```

**响应：** `{ "code": 200, "message": "Collection updated", "data": { /* Collection */ } }`

---

#### 删除专题

**请求：** `DELETE /api/admin/collections/{id}`

**响应：** `{ "code": 200, "message": "Collection deleted", "data": null }`

---

#### 添加壁纸到专题

**请求：** `POST /api/admin/collections/{id}/wallpapers`

```json
{ "wallpaperId": 1, "displayOrder": 0 }
```

**响应：** `{ "code": 200, "message": "Wallpaper added to collection", "data": null }`

---

#### 从专题移除壁纸

**请求：** `DELETE /api/admin/collections/{id}/wallpapers/{wallpaperId}`

**响应：** `{ "code": 200, "message": "Wallpaper removed from collection", "data": null }`

---

### 统计管理

#### 获取打赏列表

**请求：** `GET /api/admin/donations?page=1&pageSize=20`

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<Donation> */ } }`

---

#### 获取打赏统计

**请求：** `GET /api/admin/donations/stats`

**响应：** `{ "code": 200, "message": "Success", "data": { /* DonationStats */ } }`

---

#### 获取赞助订单列表

**请求：** `GET /api/admin/sponsor-orders?page=1&pageSize=20`

**响应：** `{ "code": 200, "message": "Success", "data": { /* PageData<SponsorOrder> */ } }`

---

#### 获取赞助统计

**请求：** `GET /api/admin/sponsor-orders/stats`

**响应：** `{ "code": 200, "message": "Success", "data": { /* SponsorStats */ } }`

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
  artistId: number | null
  artistName: string | null
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

### Artist

```typescript
interface Artist {
  id: number
  name: string
  avatar: string | null
  bio: string | null
  website: string | null
  socialLinks: Record<string, string>
  isVerified: boolean
  isFeatured: boolean
  wallpaperCount: number
  totalDownloads: number
  createdAt: number
}
```

### Collection

```typescript
interface Collection {
  id: number
  name: string
  slug: string
  description: string | null
  coverUrl: string | null
  bannerUrl: string | null
  brandName: string | null
  brandLogoUrl: string | null
  isSponsored: boolean
  sponsorInfo: object | null
  isFeatured: boolean
  wallpaperCount: number
  createdAt: number
}
```

### SponsorPackage

```typescript
interface SponsorPackage {
  id: number
  artistId: number
  name: string
  description: string | null
  price: number
  currency: string
  benefits: string[]
  isActive: boolean
}
```

### Donation

```typescript
interface Donation {
  id: number
  userId: string | null
  nickname: string | null
  amount: number
  currency: string
  message: string | null
  status: "pending" | "paid" | "refunded"
  paymentMethod: string | null
  transactionId: string | null
  isAnonymous: boolean
  createdAt: number
  paidAt: number | null
}
```

### SponsorOrder

```typescript
interface SponsorOrder {
  id: number
  packageId: number
  artistId: number
  userId: string
  amount: number
  currency: string
  status: "pending" | "paid" | "refunded"
  paymentMethod: string | null
  transactionId: string | null
  createdAt: number
  paidAt: number | null
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
