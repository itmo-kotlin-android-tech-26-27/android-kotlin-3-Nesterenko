package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.test.*

class UgcApiTest {
    @Test
    fun `brief survives application restart and invalid input returns 400`() {
        val directory = kotlin.io.path.createTempDirectory().toFile()
        val url = "jdbc:h2:file:${directory.resolve("briefs").invariantSeparatorsPath}"
        val draft = BriefDraft("  Весенний запуск  ", "Бренд", "Снять обзор", 12000)
        try {
            var saved: Brief? = null
            testApplication {
                environment { config = MapApplicationConfig("ugc.jdbcUrl" to url) }
                application { configureSerialization(); configureRouting() }
                val invalid = client.post("/api/briefs") {
                    contentType(ContentType.Application.Json)
                    setBody("{}")
                }
                assertEquals(HttpStatusCode.BadRequest, invalid.status)
                val response = client.post("/api/briefs") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(draft))
                }
                assertEquals(HttpStatusCode.Created, response.status)
                val brief = Json.decodeFromString<Brief>(response.bodyAsText())
                assertEquals("Весенний запуск", brief.title)
                assertEquals("/api/briefs/${brief.id}", response.headers[HttpHeaders.Location])
                saved = brief
            }
            val brief = assertNotNull(saved)
            testApplication {
                environment { config = MapApplicationConfig("ugc.jdbcUrl" to url) }
                application { configureSerialization(); configureRouting() }
                val loaded = client.get("/api/briefs/${brief.id}")
                assertEquals(HttpStatusCode.OK, loaded.status)
                assertEquals(brief, Json.decodeFromString<Brief>(loaded.bodyAsText()))
                assertEquals(listOf(brief), Json.decodeFromString<List<Brief>>(client.get("/api/briefs").bodyAsText()))
                assertEquals(HttpStatusCode.NotFound, client.get("/api/briefs/999").status)
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun `catalog combines filters and opens the matching profile`() = testApplication {
        val url = "jdbc:h2:mem:${UUID.randomUUID()}"
        environment { config = MapApplicationConfig("ugc.jdbcUrl" to url) }
        application { configureSerialization(); configureRouting() }
        val response = client.get("/api/creators") {
            parameter("category", "красота")
            parameter("platform", "TikTok")
            parameter("maxPrice", 5000)
            parameter("q", "алина")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val creator = Json.decodeFromString<List<Creator>>(response.bodyAsText()).single()
        val profile = Json.decodeFromString<Creator>(client.get("/api/creators/${creator.id}").bodyAsText())
        assertEquals(creator, profile)
        assertTrue(profile.portfolio.isNotEmpty())
        assertEquals(HttpStatusCode.NotFound, client.get("/api/creators/999").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/api/creators?maxPrice=-1").status)
    }
}
