package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

suspend fun Application.configureRouting() {
    val briefUrl = environment.config.propertyOrNull("ugc.jdbcUrl")?.getString()
        ?: "jdbc:h2:file:./performanceugc"
    val briefs = BriefRepository(briefUrl)
    briefs.initialize()

    routing {
        get("/") { call.respondText("Hello, World!") }
        get("/json/kotlinx-serialization") { call.respond(mapOf("hello" to "world")) }

        route("/api/briefs") {
            post {
                val input = try {
                    call.receive<BriefDraft>()
                } catch (_: BadRequestException) {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiError("Неверный формат брифа"))
                }
                val draft = input.copy(title = input.title.trim(), product = input.product.trim(), goal = input.goal.trim())
                if (draft.title.length !in 1..120 || draft.product.length !in 1..200 ||
                    draft.goal.length !in 1..2000 || draft.budget <= 0) {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiError("Проверьте поля брифа и бюджет"))
                }
                val brief = briefs.create(draft)
                call.response.header(HttpHeaders.Location, "/api/briefs/${brief.id}")
                call.respond(HttpStatusCode.Created, brief)
            }
            get { call.respond(briefs.read()) }
            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()?.takeIf { it > 0 }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiError("Неверный номер брифа"))
                val brief = briefs.read(id).firstOrNull()
                    ?: return@get call.respond(HttpStatusCode.NotFound, ApiError("Бриф не найден"))
                call.respond(brief)
            }
        }
        route("/api/creators") {
            get {
                val params = call.request.queryParameters
                val search = params["q"]?.trim().orEmpty()
                val category = params["category"]?.trim().orEmpty()
                val platform = params["platform"]?.trim().orEmpty()
                val maxPrice = params["maxPrice"]?.let {
                    it.toIntOrNull()?.takeIf { price -> price > 0 }
                        ?: return@get call.respond(HttpStatusCode.BadRequest, ApiError("Укажите положительную цену"))
                }
                call.respond(creators.filter {
                    (it.name.contains(search, ignoreCase = true) || it.bio.contains(search, ignoreCase = true) ||
                        it.category.contains(search, ignoreCase = true)) &&
                        (category.isEmpty() || it.category.equals(category, ignoreCase = true)) &&
                        (platform.isEmpty() || it.platform.equals(platform, ignoreCase = true)) &&
                        (maxPrice == null || it.price <= maxPrice)
                })
            }
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()?.takeIf { it > 0 }
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ApiError("Неверный номер креатора"))
                val creator = creators.firstOrNull { it.id == id }
                    ?: return@get call.respond(HttpStatusCode.NotFound, ApiError("Креатор не найден"))
                call.respond(creator)
            }
        }
    }
}
