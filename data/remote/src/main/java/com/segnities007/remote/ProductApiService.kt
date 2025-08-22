package com.segnities007.remote

import com.segnities007.remote.model.GoogleBooksResponse
import com.segnities007.remote.model.GoogleBookItem
import com.segnities007.remote.model.VolumeInfo
import com.segnities007.remote.model.ImageLinks
import com.segnities007.remote.model.IndustryIdentifier
import com.segnities007.remote.model.OpenLibraryResponse
import com.segnities007.remote.model.Author
import com.segnities007.remote.model.Publisher
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.item.ProductInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ProductApiService {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.INFO
        }
    }

    suspend fun getProductInfo(barcode: String): ProductInfo? = withContext(Dispatchers.IO) {
        try {
            // ISBNの場合（10桁または13桁）
            if (isIsbn(barcode)) {
                return@withContext getBookInfo(barcode)
            }
            
            // その他のバーコード形式の場合
            return@withContext getGenericProductInfo(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isIsbn(barcode: String): Boolean {
        val cleanBarcode = barcode.replace("-", "").replace(" ", "")
        return cleanBarcode.length == 10 || cleanBarcode.length == 13
    }

    private suspend fun getBookInfo(isbn: String): ProductInfo? {
        try {
            // OpenLibrary APIを試行
            val openLibraryInfo = getOpenLibraryInfo(isbn)
            if (openLibraryInfo != null) {
                return openLibraryInfo
            }

            // Google Books APIを試行
            val googleBooksInfo = getGoogleBooksInfo(isbn)
            if (googleBooksInfo != null) {
                return googleBooksInfo
            }

            // フォールバック: 基本的な情報のみ
            return ProductInfo(
                barcode = isbn,
                name = "書籍 (ISBN: $isbn)",
                description = "ISBN: $isbn の書籍",
                category = ItemCategory.STUDY_SUPPLIES,
                isbn = isbn
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun getOpenLibraryInfo(isbn: String): ProductInfo? {
        try {
            val response = httpClient.get("https://openlibrary.org/api/books") {
                parameter("bibkeys", "ISBN:$isbn")
                parameter("format", "json")
                parameter("jscmd", "data")
            }
            
            // OpenLibraryのレスポンスをパース
            val responseText = response.body<String>()
            val jsonObject = org.json.JSONObject(responseText)
            val bookKey = "ISBN:$isbn"
            
            if (jsonObject.has(bookKey)) {
                val bookData = jsonObject.getJSONObject(bookKey)
                val title = bookData.optString("title", "")
                val authors = bookData.optJSONArray("authors")
                val publishers = bookData.optJSONArray("publishers")
                val description = bookData.optString("description", "")
                val coverUrl = bookData.optJSONObject("cover")?.optString("large", null)

                val authorNames = if (authors != null) {
                    (0 until authors.length()).mapNotNull { i ->
                        val name = authors.getJSONObject(i).optString("name", null)
                        if (name != null && name != "null") name else null
                    }
                } else emptyList()

                val publisherNames = if (publishers != null) {
                    (0 until publishers.length()).mapNotNull { i ->
                        val name = publishers.getJSONObject(i).optString("name", null)
                        if (name != null && name != "null") name else null
                    }
                } else emptyList()

                return ProductInfo(
                    barcode = isbn,
                    name = title.ifEmpty { "書籍 (ISBN: $isbn)" },
                    description = description.ifEmpty { "ISBN: $isbn の書籍" },
                    category = ItemCategory.STUDY_SUPPLIES,
                    imageUrl = coverUrl,
                    isbn = isbn,
                    publisher = publisherNames.firstOrNull(),
                    author = authorNames.firstOrNull()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun getGoogleBooksInfo(isbn: String): ProductInfo? {
        try {
            val response = httpClient.get("https://www.googleapis.com/books/v1/volumes") {
                parameter("q", "isbn:$isbn")
            }
            
            val googleBooksResponse = response.body<GoogleBooksResponse>()
            val book = googleBooksResponse.items?.firstOrNull()?.volumeInfo
            
            if (book != null) {
                return ProductInfo(
                    barcode = isbn,
                    name = book.title ?: "書籍 (ISBN: $isbn)",
                    description = book.description ?: "ISBN: $isbn の書籍",
                    category = ItemCategory.STUDY_SUPPLIES,
                    imageUrl = book.imageLinks?.thumbnail,
                    isbn = isbn,
                    publisher = book.publisher,
                    author = book.authors?.firstOrNull()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun getGenericProductInfo(barcode: String): ProductInfo? {
        // 一般的な商品の場合、バーコードから基本的な情報のみ提供
        return ProductInfo(
            barcode = barcode,
            name = "商品 (バーコード: $barcode)",
            description = "バーコード: $barcode の商品",
            category = ItemCategory.OTHER_SUPPLIES
        )
    }
    
    fun close() {
        httpClient.close()
    }
}
