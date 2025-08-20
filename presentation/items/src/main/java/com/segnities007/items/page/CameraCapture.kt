package com.segnities007.items.page

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
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
                Log.d("CameraX", "Zoom set to $clampedZoom (range: ${it.minZoomRatio} - ${it.maxZoomRatio})")
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
        AndroidView(
            factory = {
                previewView.apply {
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        val backgroundColor = MaterialTheme.colorScheme.primaryContainer
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxWidth = size.width * 0.8f
            val boxHeight = boxWidth
            val left = (size.width - boxWidth) / 2
            val top = (size.height - boxHeight) / 2

            val path =
                Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height))
                    addRect(Rect(left, top, left + boxWidth, top + boxHeight))
                    fillType = PathFillType.EvenOdd
                }
            drawPath(path, color = backgroundColor)
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
            ) { Text("✕", textAlign = TextAlign.Center) }

            Button(
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
                                Log.e("CameraCapture", "takePicture onError: ${exception.message}", exception)
                            }
                        },
                    )
                },
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
            ) { Text("●") }

            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}
