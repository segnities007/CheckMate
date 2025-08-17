package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.segnities007.model.UserStatus

@Composable
fun UserStatusCard(userStatus: UserStatus) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UserIcon(pictureUrl = userStatus.pictureUrl)
            InformationLabels(
                name = userStatus.name,
                email = userStatus.email,
            )
        }
    }
}

@Composable
private fun UserIcon(pictureUrl: String?) {
    if (pictureUrl != null) {
        AsyncImage(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp),
            model = pictureUrl,
            contentDescription = null,
        )
    } else {
        Icon(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp),
            imageVector = Icons.Default.Person,
            contentDescription = null,
        )
    }
}

@Composable
private fun InformationLabels(
    name: String?,
    email: String?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = name ?: "NoName",
            fontSize = 32.sp,
        )
        Text(
            text = email ?: "NoEmail",
            fontSize = 12.sp,
            color = Color.Gray,
        )
    }
}

@Composable
@Preview
private fun UserStatusCardPreview() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        UserStatusCard(UserStatus())
    }
}
