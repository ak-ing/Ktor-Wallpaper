package com.aking.routes

import com.aking.data.SponsorRepository
import com.aking.model.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Route.sponsorRoutes() {
    route("/sponsor") {
        // POST /api/sponsor/order - Create a sponsor order
        post("/order") {
            val request = call.receive<CreateSponsorOrderRequest>()

            try {
                val order = SponsorRepository.createSponsorOrder(request)
                call.created(order, "Sponsor order created")
            } catch (e: IllegalArgumentException) {
                badRequest(e.message ?: "Invalid request")
            }
        }

        // GET /api/sponsor/orders/{id} - Get sponsor order by ID
        get("/orders/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val order = SponsorRepository.getSponsorOrderById(id)
                ?: notFound("Order not found")

            call.success(order)
        }

        // POST /api/sponsor/orders/{id}/confirm - Confirm payment (callback)
        post("/orders/{id}/confirm") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: badRequest("Invalid ID")

            val request = call.receive<ConfirmPaymentRequest>()

            if (!SponsorRepository.confirmSponsorOrder(id, request.transactionId, request.paymentMethod)) {
                badRequest("Failed to confirm order")
            }

            call.success<Unit>(message = "Order confirmed")
        }
    }
}
