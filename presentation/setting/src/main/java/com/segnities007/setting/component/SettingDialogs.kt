package com.segnities007.setting.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteAllDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("全データ削除") },
        text = { Text("すべてのデータを削除しますか？") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("削除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        },
    )
}

@Composable
fun ImportingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("作成中") },
        text = { Text("テンプレートを生成しています...") },
        confirmButton = { },
    )
}

