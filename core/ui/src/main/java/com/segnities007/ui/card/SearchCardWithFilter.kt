package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import com.segnities007.ui.card.BaseCard
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.segnities007.ui.card.search.model.FilterConfig
import com.segnities007.ui.card.search.model.SortConfig


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <FilterType, SortType> SearchCardWithFilter(
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchPlaceholder: String,
    onSearchQueryChange: (String) -> Unit,
    filterConfig: FilterConfig<FilterType>? = null,
    sortConfig: SortConfig<SortType>? = null,
) {
    val backgroundAlpha = 0.6f

    BaseCard(
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
                        FilterDropdown(modifier = Modifier.weight(1f), config = config, backgroundAlpha = backgroundAlpha)
                    }

                    sortConfig?.let { config ->
                        SortDropdown(modifier = Modifier.weight(1f), config = config, backgroundAlpha = backgroundAlpha)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    searchPlaceholder: String,
    onSearchQueryChange: (String) -> Unit,
    backgroundAlpha: Float = 0.6f,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = searchPlaceholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "検索",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha),
        ),
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> FilterDropdown(
    modifier: Modifier = Modifier,
    config: FilterConfig<T>,
    backgroundAlpha: Float = 0.6f,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    ),
                ),
            ),
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = config.iconDescription,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = config.getDisplayName(config.selectedValue),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            config.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(config.getDisplayName(option.value)) },
                    onClick = {
                        config.onValueChange(option.value)
                        expanded = false
                    },
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SortDropdown(
    modifier: Modifier = Modifier,
    config: SortConfig<T>,
    backgroundAlpha: Float = 0.6f,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    ),
                ),
            ),
        ) {
            Icon(
                imageVector = config.icon,
                contentDescription = config.iconDescription,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = config.getDisplayName(config.selectedValue),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            config.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(config.getDisplayName(option.value)) },
                    onClick = {
                        config.onValueChange(option.value)
                        expanded = false
                    },
                )
            }
        }
    }
}
