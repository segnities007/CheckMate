package com.segnities007.ui.card.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.ui.bar.SearchBar
import com.segnities007.ui.card.search.component.FilterDropdown
import com.segnities007.ui.card.search.component.SortDropdown
import com.segnities007.ui.card.search.model.FilterConfig
import com.segnities007.ui.card.search.model.SortConfig

/**
 * 汎用的な検索・フィルタ・ソート機能を提供するコンポーネント
 *
 * @param searchQuery 現在の検索クエリ
 * @param searchPlaceholder 検索バーのプレースホルダーテキスト
 * @param onSearchQueryChange 検索クエリ変更時のコールバック
 * @param filterConfig フィルタ設定（null の場合はフィルタボタンを非表示）
 * @param sortConfig ソート設定（null の場合はソートボタンを非表示）
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <FilterType, SortType> SearchCard(
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchPlaceholder: String,
    onSearchQueryChange: (String) -> Unit,
    filterConfig: FilterConfig<FilterType>? = null,
    sortConfig: SortConfig<SortType>? = null,
) {
    val backgroundAlpha = 0.6f

    _root_ide_package_.com.segnities007.ui.card.BaseCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SearchBar(
                searchQuery = searchQuery,
                searchPlaceholder = searchPlaceholder,
                onSearchQueryChange = onSearchQueryChange,
                backgroundAlpha = backgroundAlpha,
            )

            if (filterConfig != null || sortConfig != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    filterConfig?.let { config ->
                        FilterDropdown(
                            modifier = Modifier.weight(1f),
                            config = config,
                            backgroundAlpha = backgroundAlpha
                        )
                    }

                    sortConfig?.let { config ->
                        SortDropdown(
                            modifier = Modifier.weight(1f),
                            config = config,
                            backgroundAlpha = backgroundAlpha
                        )
                    }
                }
            }
        }
    }
}