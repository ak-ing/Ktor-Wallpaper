package com.aking.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 数据库配置对象
 * 负责初始化数据库连接和创建表结构
 *
 * 环境变量:
 * - DATABASE_URL: PostgreSQL 连接地址 (默认: jdbc:postgresql://localhost:5432/wallpaper)
 * - DATABASE_USER: 数据库用户名 (默认: postgres)
 * - DATABASE_PASSWORD: 数据库密码 (默认: postgres)
 */
object DatabaseConfig {

    private val jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/wallpaper"
    private val dbUser = System.getenv("DATABASE_USER") ?: "postgres"
    private val dbPassword = System.getenv("DATABASE_PASSWORD") ?: "postgres"

    /**
     * 初始化数据库连接
     * 使用 HikariCP 连接池管理数据库连接
     */
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = this@DatabaseConfig.jdbcUrl
            username = dbUser
            password = dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10                                    // 最大连接数
            isAutoCommit = false                                    // 禁用自动提交
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"    // 事务隔离级别
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        // 自动创建表结构
        transaction {
            SchemaUtils.create(
                Categories,
                Artists,
                Wallpapers,
                WallpaperTags,
                ArtistSponsorPackages,
                SponsorOrders,
                Donations,
                Collections,
                CollectionWallpapers
            )
        }
    }
}

/**
 * 协程安全的数据库事务
 * 将阻塞的数据库操作转移到 IO 调度器执行，避免阻塞主线程
 *
 * @param block 事务代码块
 * @return 事务执行结果
 */
suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)
