package com.segnities007.items.component

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.segnities007.designsystem.theme.Dimens
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private fun createImageFile(context: Context): File {
    val timeStamp =
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.cacheDir
    if (!storageDir.exists()) storageDir.mkdirs()
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}

@Composable
fun CameraCapture(
    onImageCaptured: (imageUri: Uri, imagePath: String) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor: Executor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        try {
            cameraProvider.unbindAll()
            preview.surfaceProvider = previewView.surfaceProvider
            val camera =
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                )

            val zoomState = camera.cameraInfo.zoomState.value
            zoomState?.let {
                val targetZoom = 0.8f
                val clampedZoom = targetZoom.coerceIn(it.minZoomRatio, it.maxZoomRatio)
                camera.cameraControl.setZoomRatio(clampedZoom)
            }
        } catch (exc: Exception) {
            Toast
                .makeText(
                    context,
                    "カメラの起動に失敗しました: ${exc.message}",
                    Toast.LENGTH_LONG,
                ).show()
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

        // オーバーレイUI
        Box(modifier = Modifier.fillMaxSize()) {
            // 上部のヘッダー
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = Dimens.PaddingXXL, start = Dimens.PaddingMedium, end = Dimens.PaddingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // キャンセルボタン
                IconButton(
                    onClick = onCancel,
                    modifier =
                        Modifier
                            .size(Dimens.IconExtraLarge)
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = CircleShape,
                            ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "キャンセル",
                        tint = Color.White,
                        modifier = Modifier.size(Dimens.IconMedium),
                    )
                }

                // タイトル
                Text(
                    text = "写真を撮影",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier =
                        Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(Dimens.CornerLarge),
                            ).padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
                )

                // 右側のスペーサー（バランス用）
                Spacer(modifier = Modifier.size(Dimens.IconExtraLarge))
            }

            // 下部のコントロールエリア
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Dimens.PaddingXXL),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 撮影ボタン
                FloatingActionButton(
                    onClick = {
                        val photoFile = createImageFile(context)
                        val authorityString = "${context.packageName}.provider"
                        val photoUri = FileProvider.getUriForFile(context, authorityString, photoFile)
                        val outputFileOptions =
                            ImageCapture.OutputFileOptions.Builder(photoFile).build()
                        imageCapture.takePicture(
                            outputFileOptions,
                            cameraExecutor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val savedUri = outputFileResults.savedUri ?: photoUri
                                    onImageCaptured(savedUri, photoFile.absolutePath)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast
                                        .makeText(
                                            context,
                                            "写真撮影に失敗しました: ${exception.message}",
                                            Toast.LENGTH_LONG,
                                        ).show()
                                }
                            },
                        )
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "撮影",
                        modifier = Modifier.size(32.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 撮影ガイドテキスト
                Text(
                    text = "アイテムを撮影してください",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier =
                        Modifier
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp),
                            ).padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}
