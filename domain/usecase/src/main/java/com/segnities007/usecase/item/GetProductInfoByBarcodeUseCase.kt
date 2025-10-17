package com.segnities007.usecase.item

import com.segnities007.model.item.BarcodeInfo
import com.segnities007.model.item.ProductInfo
import com.segnities007.repository.ItemRepository

/**
 * バーコードから商品情報を取得するUse Case
 */
class GetProductInfoByBarcodeUseCase(
    private val itemRepository: ItemRepository
) {
    suspend operator fun invoke(barcodeInfo: BarcodeInfo): Result<ProductInfo?> {
        return try {
            val productInfo = itemRepository.getProductInfoByBarcode(barcodeInfo)
            Result.success(productInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
