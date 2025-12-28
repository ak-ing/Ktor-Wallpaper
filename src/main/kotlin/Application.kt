package com.aking

import com.aking.database.DatabaseConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
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
