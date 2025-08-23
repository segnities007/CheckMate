package com.segnities007.setting.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AccountButtons(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 2.dp,
            focusedElevation = 1.dp,
            hoveredElevation = 1.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ボタンリスト
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccountButtonCard(
                    title = "プロフィール編集",
                    description = "ユーザー情報を変更",
                    icon = Icons.Default.Person,
                    onClick = onEditProfile
                )
                
                AccountButtonCard(
                    title = "パスワード変更",
                    description = "セキュリティ設定を変更",
                    icon = Icons.Default.Lock,
                    onClick = onChangePassword
                )
                
                AccountButtonCard(
                    title = "ログアウト",
                    description = "アカウントからログアウト",
                    icon = Icons.Default.Logout,
                    onClick = onLogout,
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun AccountButtonCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 1.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // アイコン
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDestructive) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            // テキスト情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 矢印アイコン
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
