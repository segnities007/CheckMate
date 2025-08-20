package com.segnities007.items.mvi

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.segnities007.model.item.Item
import com.segnities007.ui.mvi.MviState

data class ItemsState(
    val items: List<Item> = emptyList(),
    val isShowBottomSheet: Boolean = false,
    val capturedImageUriForBottomSheet: Uri? = null,
    val capturedTempPathForViewModel: String = "",
) : MviState