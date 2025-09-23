package com.segnities007.widget

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider

internal class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Widget()
        }
    }
}

@Composable
internal fun Widget(){

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(100.dp)
            .background(
            MaterialTheme.colorScheme.primaryContainer,
        )
    ){
        WidgetContent(5,2)
    }
}


@Composable
fun WidgetContent(selectedIndex: Int, count: Int) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Row(
            modifier = GlanceModifier.padding(8.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            repeat(count) { index ->
                Box(
                    modifier = GlanceModifier
                        .size(12.dp)
                        .background(
                            color = if (index == selectedIndex) 0xFFFFFFFF.toInt() else 0xFFAAAAAA.toInt()
                        )
                        .cornerRadius(6.dp)
                ){}
            }
        }

        // テキストも Glance の Text を使用
        Text("Hello")
    }
}