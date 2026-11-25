package ru.performanceugc.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private val apiUrl = "http://10.0.2.2:${BuildConfig.UGC_API_PORT}/api"

internal data class Brief(val id: Long, val title: String, val product: String, val goal: String, val budget: Int)

internal data class Creator(
    val id: Int, val name: String, val category: String, val platform: String, val price: Int,
    val followers: Int, val engagement: Double, val bio: String, val portfolio: List<String>,
)

internal object Api {
    suspend fun createBrief(title: String, product: String, goal: String, budget: Int): Brief {
        val body = JSONObject().put("title", title).put("product", product).put("goal", goal).put("budget", budget)
        return parseBrief(JSONObject(request("/briefs", body)))
    }

    suspend fun briefs(): List<Brief> = parseList(request("/briefs"), ::parseBrief)
    suspend fun creator(id: Int): Creator = parseCreator(JSONObject(request("/creators/$id")))

    suspend fun creators(search: String, category: String, platform: String, maxPrice: String): List<Creator> {
        val params = mapOf("q" to search, "category" to category, "platform" to platform, "maxPrice" to maxPrice)
        val query = params.filterValues { it.isNotBlank() }.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value.trim(), "UTF-8")}"
        }
        val path = if (query.isEmpty()) "/creators" else "/creators?$query"
        return parseList(request(path), ::parseCreator)
    }

    private fun <T> parseList(json: String, decode: (JSONObject) -> T): List<T> {
        val array = JSONArray(json)
        return List(array.length()) { decode(array.getJSONObject(it)) }
    }

    private fun parseBrief(json: JSONObject) = Brief(
        json.getLong("id"), json.getString("title"), json.getString("product"),
        json.getString("goal"), json.getInt("budget"),
    )

    private fun parseCreator(json: JSONObject): Creator {
        val portfolio = json.getJSONArray("portfolio")
        return Creator(
            json.getInt("id"), json.getString("name"), json.getString("category"), json.getString("platform"),
            json.getInt("price"), json.getInt("followers"), json.getDouble("engagement"), json.getString("bio"),
            List(portfolio.length()) { portfolio.getString(it) },
        )
    }

    private suspend fun request(path: String, body: JSONObject? = null): String = withContext(Dispatchers.IO) {
        val connection = URL(apiUrl + path).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 5000
            connection.readTimeout = 8000
            connection.setRequestProperty("Accept", "application/json")
            if (body != null) {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            if (status !in 200..299) {
                val response = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                val message = runCatching { JSONObject(response.orEmpty()).getString("message") }.getOrNull()
                error(message ?: "Сервер вернул ошибку $status")
            }
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
