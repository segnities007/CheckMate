package com.segnities007.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.login.component.LoginButton

@Composable
fun LoginScreen(){

    LoginUi()
}

@Composable
private fun LoginUi(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Spacer(modifier = Modifier.weight(1f))
        Titles()
        Spacer(modifier = Modifier.weight(1f))
        Buttons()
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun Titles(){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Text(
            text = "CheckMate",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun Buttons(){
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        LoginButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue With Google",
            res = R.drawable.icons8_google,
        ) {
            //TODO
        }
        LoginButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue with nothing",
        ) {
            //TODO
        }
    }
}

@Composable
@Preview
private fun LoginUiPreview(){
    LoginUi()
}