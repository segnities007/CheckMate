package com.segnities007.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.login.R

@Composable
internal fun LoginButton(
    modifier: Modifier = Modifier,
    res: Int? = null,
    text: String,
    onClick: () -> Unit,
){
    ElevatedButton(
        modifier = modifier.height(52.dp),
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            if (res!=null)
                Icon(
                    painter = painterResource(res),
                    contentDescription = "Google Login Button"
                )
            Text(
                text = text,
                fontSize = 22.sp,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@Preview
private fun GoogleLoginButtonPreview(){
    LoginButton(onClick = {}, text = "Google Login", res = R.drawable.icons8_google)
}