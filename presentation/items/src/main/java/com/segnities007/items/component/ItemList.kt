package com.segnities007.items.component

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.segnities007.items.mvi.ItemsState
import com.segnities007.model.Item

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsList(
    state: ItemsState,
    onAddItem: () -> Unit,
    onDeleteItem: (Item) -> Unit,
) {
    val chunked = state.items.chunked(2)
    Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        chunked.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowItems.forEach { item ->
                    ItemCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onDeleteItem = onDeleteItem,
                    )
                }
                if (rowItems.size < 2) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        AddItemCard(onAddItem = onAddItem)
    }
}

@Composable
fun AddItemCard(onAddItem: () -> Unit) {
    val context = LocalContext.current

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                onAddItem()
            }
        }

    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA,
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            onAddItem()
                        }

                        else -> {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("+ Add Item", style = MaterialTheme.typography.titleMedium)
        }
    }
}
