package com.aking.routes

import com.aking.data.DonationRepository
import com.aking.model.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.donationRoutes() {
    route("/donations") {
        // POST /api/donations - Create a donation order
        post {
            val request = call.receive<CreateDonationRequest>()

            if (request.amount <= 0) {
                badRequest("Amount must be positive")
            }

            val donation = DonationRepository.createDonation(request)
            call.created(donation, "Donation order created")
        }

        // GET /api/donations/stats - Get donation statistics
        get("/stats") {
            val stats = DonationRepository.getDonationStats()
            call.success(stats)
        }

        // GET /api/donations/recent - Get recent donations
        get("/recent") {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
            val donations = DonationRepository.getRecentDonations(limit)
            call.success(donations)
        }

        // GET /api/donations/{id} - Get donation by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val donation = DonationRepository.getDonationById(id)
                ?: notFound("Donation not found")

            call.success(donation)
        }

        // POST /api/donations/{id}/confirm - Confirm payment (callback)
        post("/{id}/confirm") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val request = call.receive<ConfirmPaymentRequest>()

            if (!DonationRepository.confirmDonation(id, request.transactionId, request.paymentMethod)) {
                badRequest("Failed to confirm donation")
            }

            call.success<Unit>(message = "Donation confirmed")
        }
    }
}
