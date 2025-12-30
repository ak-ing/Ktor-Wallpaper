package com.aking.data

import com.aking.database.*
import com.aking.model.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.sum

object DonationRepository {

    // ==================== Donation 查询 ====================

    suspend fun getAllDonations(page: Int = 1, pageSize: Int = 20): List<Donation> = suspendTransaction {
        DonationEntity.all()
            .orderBy(Donations.createdAt to SortOrder.DESC)
            .limit(pageSize).offset(((page - 1) * pageSize).toLong())
            .map { it.toDonation() }
    }

    suspend fun getDonationCount(): Int = suspendTransaction {
        DonationEntity.count().toInt()
    }

    suspend fun getDonationById(id: Int): Donation? = suspendTransaction {
        DonationEntity.findById(id)?.toDonation()
    }

    suspend fun getRecentDonations(limit: Int = 10): List<Donation> = suspendTransaction {
        DonationEntity.find { Donations.status eq "paid" }
            .orderBy(Donations.paidAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toDonation() }
    }

    suspend fun getDonationStats(): DonationStats = suspendTransaction {
        val totalAmount = Donations
            .select(Donations.amount.sum())
            .where { Donations.status eq "paid" }
            .firstOrNull()
            ?.get(Donations.amount.sum())
            ?.toDouble() ?: 0.0

        val totalCount = DonationEntity.find { Donations.status eq "paid" }.count().toInt()

        val recentDonations = DonationEntity.find { Donations.status eq "paid" }
            .orderBy(Donations.paidAt to SortOrder.DESC)
            .limit(5)
            .map { it.toDonation() }

        DonationStats(
            totalAmount = totalAmount,
            totalCount = totalCount,
            recentDonations = recentDonations
        )
    }

    // ==================== Donation 管理 ====================

    suspend fun createDonation(request: CreateDonationRequest): Donation = suspendTransaction {
        val now = Clock.System.now()
        DonationEntity.new {
            userId = request.userId
            nickname = request.nickname
            amount = request.amount.toBigDecimal()
            message = request.message
            isAnonymous = request.isAnonymous
            status = "pending"
            createdAt = now
        }.toDonation()
    }

    suspend fun confirmDonation(id: Int, transactionId: String, paymentMethod: String): Boolean = suspendTransaction {
        val entity = DonationEntity.findById(id) ?: return@suspendTransaction false
        if (entity.status != "pending") return@suspendTransaction false

        entity.status = "paid"
        entity.transactionId = transactionId
        entity.paymentMethod = paymentMethod
        entity.paidAt = Clock.System.now()
        true
    }

    // ==================== 转换方法 ====================

    private fun DonationEntity.toDonation(): Donation {
        return Donation(
            id = id.value,
            nickname = if (isAnonymous) null else nickname,
            amount = amount.toDouble(),
            message = if (isAnonymous) null else message,
            isAnonymous = isAnonymous,
            createdAt = createdAt.toEpochMilliseconds()
        )
    }
}
