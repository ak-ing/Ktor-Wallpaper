package com.aking.model

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

/**
 * 统一 API 响应格式
 */
@Serializable
data class ApiResult<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "Success") =
            ApiResult(HttpStatusCode.OK.value, message, data)

        fun <T> created(data: T? = null, message: String = "Created") =
            ApiResult(HttpStatusCode.Created.value, message, data)

        fun <T> error(status: HttpStatusCode, message: String) =
            ApiResult<T>(status.value, message, null)
    }
}

/**
 * 分页数据包装
 */
@Serializable
data class PageData<T>(
    val list: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int,
    val hasMore: Boolean
) {
    companion object {
        fun <T> of(list: List<T>, page: Int, pageSize: Int, total: Int): PageData<T> {
            val totalPages = if (total == 0) 0 else (total + pageSize - 1) / pageSize
            return PageData(
                list = list,
                page = page,
                pageSize = pageSize,
                total = total,
                totalPages = totalPages,
                hasMore = page < totalPages
            )
        }
    }
}

/**
 * 业务异常
 */
class ApiException(
    val status: HttpStatusCode,
    override val message: String
) : RuntimeException(message)

/** 抛出 400 Bad Request */
fun badRequest(message: String): Nothing = throw ApiException(HttpStatusCode.BadRequest, message)

/** 抛出 404 Not Found */
fun notFound(message: String): Nothing = throw ApiException(HttpStatusCode.NotFound, message)

/**
 * 响应扩展函数
 */
suspend inline fun <reified T> RoutingCall.success(data: T? = null, message: String = "Success") {
    respond(HttpStatusCode.OK, ApiResult.success(data, message))
}

suspend inline fun <reified T> RoutingCall.created(data: T? = null, message: String = "Created") {
    respond(HttpStatusCode.Created, ApiResult.created(data, message))
}
