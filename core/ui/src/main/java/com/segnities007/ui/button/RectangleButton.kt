package com.segnities007.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RectangleButton(
    modifier: Modifier = Modifier,
    text: String,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    onClick: () -> Unit
){
    ElevatedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){

            if(startIcon != null){
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = startIcon,
                    contentDescription = null
                )
            }

            Text(
                text = text,
                fontSize = 24.sp,
            )
            if(endIcon != null){
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = endIcon,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
@Preview
private fun RectangleButtonPreview(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        RectangleButton(
            startIcon = Icons.Default.Person,
            endIcon = Icons.Default.Person,
            text = "Rectangle Button",
            onClick = {},
        )
    }
}