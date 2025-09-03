package com.segnities007.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.login.R

@Composable
fun LoginTitles(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(R.drawable.checkmate),
            contentDescription = "App Icon",
            modifier =
                Modifier
                    .size(256.dp)
                    .padding(32.dp),
        )
        Text(
            text = "CheckMate",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

