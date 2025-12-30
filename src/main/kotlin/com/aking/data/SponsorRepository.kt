package com.aking.data

import com.aking.database.*
import com.aking.model.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.sum

object SponsorRepository {

    // ==================== Sponsor Order 查询 ====================

    suspend fun getSponsorOrders(page: Int = 1, pageSize: Int = 20): List<SponsorOrder> = suspendTransaction {
        SponsorOrderEntity.all()
            .orderBy(SponsorOrders.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toSponsorOrder() }
    }

    suspend fun getSponsorOrderCount(): Int = suspendTransaction {
        SponsorOrderEntity.count().toInt()
    }

    suspend fun getSponsorOrderById(id: Int): SponsorOrder? = suspendTransaction {
        SponsorOrderEntity.findById(id)?.toSponsorOrder()
    }

    suspend fun getSponsorStats(): SponsorStats = suspendTransaction {
        val totalAmount = SponsorOrders
            .select(SponsorOrders.amount.sum())
            .where { SponsorOrders.status eq "paid" }
            .firstOrNull()
            ?.get(SponsorOrders.amount.sum())
            ?.toDouble() ?: 0.0

        val totalCount = SponsorOrderEntity.find { SponsorOrders.status eq "paid" }.count().toInt()

        val recentOrders = SponsorOrderEntity.find { SponsorOrders.status eq "paid" }
            .orderBy(SponsorOrders.paidAt to SortOrder.DESC)
            .limit(5)
            .map { it.toSponsorOrder() }

        SponsorStats(
            totalAmount = totalAmount,
            totalCount = totalCount,
            recentOrders = recentOrders
        )
    }

    // ==================== Sponsor Order 管理 ====================

    suspend fun createSponsorOrder(request: CreateSponsorOrderRequest): SponsorOrder = suspendTransaction {
        val pkg = ArtistSponsorPackageEntity.findById(request.packageId)
            ?: throw IllegalArgumentException("Sponsor package not found")

        if (!pkg.isActive) {
            throw IllegalArgumentException("Sponsor package is not active")
        }

        val now = Clock.System.now()
        SponsorOrderEntity.new {
            sponsorPackage = pkg
            artist = pkg.artist
            userId = request.userId
            amount = pkg.price
            currency = pkg.currency
            status = "pending"
            createdAt = now
        }.toSponsorOrder()
    }

    suspend fun confirmSponsorOrder(id: Int, transactionId: String, paymentMethod: String): Boolean = suspendTransaction {
        val entity = SponsorOrderEntity.findById(id) ?: return@suspendTransaction false
        if (entity.status != "pending") return@suspendTransaction false

        entity.status = "paid"
        entity.transactionId = transactionId
        entity.paymentMethod = paymentMethod
        entity.paidAt = Clock.System.now()

        // 更新艺术家累计收益
        val artist = entity.artist
        artist.totalEarnings = artist.totalEarnings + entity.amount
        artist.updatedAt = Clock.System.now()

        true
    }

    // ==================== 转换方法 ====================

    private fun SponsorOrderEntity.toSponsorOrder(): SponsorOrder {
        return SponsorOrder(
            id = id.value,
            packageId = sponsorPackage.id.value,
            packageName = sponsorPackage.name,
            artistId = artist.id.value,
            artistName = artist.name,
            userId = userId,
            amount = amount.toDouble(),
            currency = currency,
            status = status,
            createdAt = createdAt.toEpochMilliseconds(),
            paidAt = paidAt?.toEpochMilliseconds()
        )
    }
}
