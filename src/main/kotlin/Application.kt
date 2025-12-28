package com.aking

import com.aking.database.DatabaseConfig
import com.aking.model.ApiException
import com.aking.model.ApiResult
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

/**
 * 应用程序入口点
 * 启动 Netty 服务器，监听 8080 端口
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * 应用程序主模块
 * 配置所有必要的插件和路由
 */
fun Application.module() {
    // 初始化数据库连接和表结构
    DatabaseConfig.init()

    // 配置 JSON 序列化
    configureSerialization()

    // 配置统一异常处理
    configureStatusPages()

    // 配置路由
    configureRouting()
}

/**
 * 配置 JSON 序列化
 * 使用 kotlinx.serialization 进行 JSON 处理
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true          // 格式化输出
            isLenient = true            // 宽松解析
            ignoreUnknownKeys = true    // 忽略未知字段
        })
    }
}

/**
 * 配置统一异常处理
 * 使用 StatusPages 插件捕获异常并返回统一格式的错误响应
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // 处理业务异常
        exception<ApiException> { call, cause ->
            call.respond(
                cause.status,
                ApiResult.error<Unit>(cause.status, cause.message)
            )
        }

        // 处理参数类型转换异常
        exception<NumberFormatException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResult.error<Unit>(HttpStatusCode.BadRequest, "Invalid parameter format")
            )
        }

        // 处理其他未知异常
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResult.error<Unit>(HttpStatusCode.InternalServerError, "Internal server error")
            )
        }

        // 处理 404 状态
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResult.error<Unit>(HttpStatusCode.NotFound, "Resource not found")
            )
        }
    }
}
