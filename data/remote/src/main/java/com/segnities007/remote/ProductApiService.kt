package com.segnities007.remote

import com.segnities007.remote.model.GoogleBooksResponse
import com.segnities007.model.item.ItemCategory
import com.segnities007.model.item.ProductInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import android.util.Log

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
            Log.d("ProductApiService", "getProductInfo called with barcode: $barcode")

            // ISBNの場合（10桁または13桁）
            if (isIsbn(barcode)) {
                Log.d("ProductApiService", "Barcode is ISBN, calling getBookInfo")
                return@withContext getBookInfo(barcode)
            }

            // その他のバーコード形式
            Log.d("ProductApiService", "Barcode is not ISBN, calling getGenericProductInfo")
            return@withContext getGenericProductInfo(barcode)
        } catch (e: Exception) {
            Log.e("ProductApiService", "Error in getProductInfo: ${e.message}", e)
            null
        }
    }

    private fun isIsbn(barcode: String): Boolean {
        val cleanBarcode = barcode.replace("-", "").replace(" ", "")
        return cleanBarcode.length == 10 || cleanBarcode.length == 13
    }

    private suspend fun getBookInfo(isbn: String): ProductInfo? {
        Log.d("ProductApiService", "=== getBookInfo START for ISBN: $isbn ===")
        try {
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
            Log.e("ProductApiService", "Error in getBookInfo: ${e.message}", e)
            return null
        }
    }

    private suspend fun getGoogleBooksInfo(isbn: String): ProductInfo? {
        Log.d("ProductApiService", "=== getGoogleBooksInfo START for ISBN: $isbn ===")
        try {
            val response = httpClient.get("https://www.googleapis.com/books/v1/volumes") {
                parameter("q", "isbn:$isbn")
            }

            val googleBooksResponse = response.body<GoogleBooksResponse>()
            val book = googleBooksResponse.items?.firstOrNull()?.volumeInfo

            if (book != null) {
                // smallThumbnail を優先、なければ thumbnail
                val imageUrl = (book.imageLinks?.smallThumbnail ?: book.imageLinks?.thumbnail)
                    ?.replace("http://", "https://")

                Log.d("ProductApiService", "Google Books - Title: ${book.title}")
                Log.d("ProductApiService", "Google Books - ImageURL (final): $imageUrl")

                return ProductInfo(
                    barcode = isbn,
                    name = book.title ?: "書籍 (ISBN: $isbn)",
                    description = book.description ?: "ISBN: $isbn の書籍",
                    category = ItemCategory.STUDY_SUPPLIES,
                    imageUrl = imageUrl,
                    isbn = isbn,
                    publisher = book.publisher,
                    author = book.authors?.firstOrNull()
                )
            }
        } catch (e: Exception) {
            Log.e("ProductApiService", "Google Books API error: ${e.message}", e)
        }
        return null
    }

    private fun getGenericProductInfo(barcode: String): ProductInfo? {
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
