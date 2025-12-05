package com.segnities007.items.component

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.segnities007.designsystem.theme.Dimens
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.segnities007.model.item.BarcodeInfo
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScanner(
    onBarcodeDetected: (BarcodeInfo) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val scanner = remember { BarcodeScanning.getClient() }

    var isScanning by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        try {
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build()
            preview.surfaceProvider = previewView.surfaceProvider

            val imageAnalysis =
                ImageAnalysis
                    .Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (isScanning) {
                    @OptIn(ExperimentalGetImage::class)
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image =
                            InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees,
                            )

                        scanner
                            .process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    if (barcode.rawValue != null) {
                                        val barcodeInfo =
                                            BarcodeInfo(
                                                barcode = barcode.rawValue!!,
                                                format = getBarcodeFormatName(barcode.format),
                                                rawValue = barcode.rawValue!!,
                                                displayValue = barcode.displayValue ?: barcode.rawValue!!,
                                            )
                                        isScanning = false
                                        onBarcodeDetected(barcodeInfo)
                                        break
                                    }
                                }
                            }.addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            }

            val camera =
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                )

            // ズーム設定
            val zoomState = camera.cameraInfo.zoomState.value
            zoomState?.let {
                val targetZoom = 1.2f
                val clampedZoom = targetZoom.coerceIn(it.minZoomRatio, it.maxZoomRatio)
                camera.cameraControl.setZoomRatio(clampedZoom)
            }
        } catch (exc: Exception) {
            onCancel()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // カメラプレビュー
        AndroidView(
            factory = {
                previewView.apply {
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // オーバーレイ
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxWidth = size.width * 0.8f
            val boxHeight = boxWidth * 0.6f
            val left = (size.width - boxWidth) / 2
            val top = (size.height - boxHeight) / 2

            val path =
                Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height))
                    addRect(Rect(left, top, left + boxWidth, top + boxHeight))
                    fillType = PathFillType.EvenOdd
                }
            drawPath(path, color = Color.Black.copy(alpha = 0.5f))
        }

        // スキャンエリアの枠
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Card(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(width = 280.dp, height = 180.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                ) {
                    // 角のインジケーター
                    repeat(4) { corner ->
                        val cornerSize = 40.dp
                        val cornerColor = MaterialTheme.colorScheme.primary

                        when (corner) {
                            0 -> { // 左上
                                Box(
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopStart)
                                            .size(cornerSize),
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, cornerSize.toPx()),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, 0f),
                                            strokeWidth = 4f,
                                        )
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, 0f),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), 0f),
                                            strokeWidth = 4f,
                                        )
                                    }
                                }
                            }
                            1 -> { // 右上
                                Box(
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .size(cornerSize),
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), 0f),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), cornerSize.toPx()),
                                            strokeWidth = 4f,
                                        )
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, 0f),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), 0f),
                                            strokeWidth = 4f,
                                        )
                                    }
                                }
                            }
                            2 -> { // 左下
                                Box(
                                    modifier =
                                        Modifier
                                            .align(Alignment.BottomStart)
                                            .size(cornerSize),
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, 0f),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, cornerSize.toPx()),
                                            strokeWidth = 4f,
                                        )
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, cornerSize.toPx()),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), cornerSize.toPx()),
                                            strokeWidth = 4f,
                                        )
                                    }
                                }
                            }
                            3 -> { // 右下
                                Box(
                                    modifier =
                                        Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(cornerSize),
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(0f, cornerSize.toPx()),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), cornerSize.toPx()),
                                            strokeWidth = 4f,
                                        )
                                        drawLine(
                                            color = cornerColor,
                                            start =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), 0f),
                                            end =
                                                androidx.compose.ui.geometry
                                                    .Offset(cornerSize.toPx(), cornerSize.toPx()),
                                            strokeWidth = 4f,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ヘルプテキスト
        Text(
            text = "バーコードを枠内に合わせてください",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(Dimens.CornerSmall),
                    ).padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
        )

        // コントロールボタン
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = Dimens.PaddingExtraLarge),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // キャンセルボタン
            FloatingActionButton(
                onClick = onCancel,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "キャンセル",
                )
            }

            // スキャンアイコン
            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            RoundedCornerShape(36.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "スキャン中",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            // プレースホルダー
            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

private fun getBarcodeFormatName(format: Int): String =
    when (format) {
        Barcode.FORMAT_QR_CODE -> "QR_CODE"
        Barcode.FORMAT_AZTEC -> "AZTEC"
        Barcode.FORMAT_CODABAR -> "CODABAR"
        Barcode.FORMAT_CODE_39 -> "CODE_39"
        Barcode.FORMAT_CODE_93 -> "CODE_93"
        Barcode.FORMAT_CODE_128 -> "CODE_128"
        Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
        Barcode.FORMAT_EAN_8 -> "EAN_8"
        Barcode.FORMAT_EAN_13 -> "EAN_13"
        Barcode.FORMAT_ITF -> "ITF"
        Barcode.FORMAT_PDF417 -> "PDF417"
        Barcode.FORMAT_UPC_A -> "UPC_A"
        Barcode.FORMAT_UPC_E -> "UPC_E"
        else -> "UNKNOWN"
    }
