package com.example.habitly.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.habitly.HabitlyApplication
import com.example.habitly.ui.evidence.EvidenceViewModel
import com.example.habitly.ui.evidence.EvidenceViewModelFactory
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun EvidenceCaptureScreen(
    sessionId: Long,
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as HabitlyApplication
    val viewModel: EvidenceViewModel = viewModel(
        factory = EvidenceViewModelFactory(application.studyEvidenceRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var pendingCapture by remember { mutableStateOf<File?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (pendingCapture != null) {
            CaptureConfirmation(
                file = pendingCapture!!,
                onRetake = {
                    pendingCapture?.delete()
                    pendingCapture = null
                },
                onUsePhoto = {
                    pendingCapture?.let { file ->
                        viewModel.saveCapture(sessionId, file, onSaved)
                    }
                },
                enabled = !uiState.isSaving
            )
        } else if (hasCameraPermission) {
            CameraPreview(
                onCapture = { imageCapture ->
                    val file = viewModel.createCaptureFile()
                    captureImage(
                        context = context,
                        imageCapture = imageCapture,
                        file = file,
                        onSuccess = { pendingCapture = file },
                        onFailure = { file.delete() }
                    )
                },
                captureEnabled = !uiState.isSaving
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    "Camera access is needed to capture your study notes.",
                    color = Color.White
                )
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Allow camera")
                }
            }
        }

        IconButton(
            onClick = {
                pendingCapture?.delete()
                onCancel()
            },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Surface(shape = MaterialTheme.shapes.medium, color = Color.Black.copy(alpha = 0.55f)) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Close camera",
                    tint = Color.White,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (uiState.isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
    }
}

@Composable
private fun CaptureConfirmation(
    file: File,
    onRetake: () -> Unit,
    onUsePhoto: () -> Unit,
    enabled: Boolean
) {
    val bitmap = remember(file.absolutePath) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Captured study snapshot",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onRetake, enabled = enabled) {
                Text("Retake")
            }
            Button(onClick = onUsePhoto, enabled = enabled) {
                Text("Use photo")
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onCapture: (ImageCapture) -> Unit,
    captureEnabled: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(previewView, lifecycleOwner) {
        val provider = awaitCameraProvider(context)
        val preview = Preview.Builder().build().also { useCase ->
            useCase.surfaceProvider = previewView.surfaceProvider
        }
        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture
        )
        cameraProvider = provider
        imageCapture = capture
    }

    DisposableEffect(Unit) {
        onDispose { cameraProvider?.unbindAll() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Button(
            onClick = { imageCapture?.let(onCapture) },
            enabled = captureEnabled && imageCapture != null,
            modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null)
            Text("Capture study snapshot", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    file: File,
    onSuccess: () -> Unit,
    onFailure: (ImageCaptureException) -> Unit
) {
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) = onSuccess()
            override fun onError(exception: ImageCaptureException) = onFailure(exception)
        }
    )
}

private suspend fun awaitCameraProvider(context: Context): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess { provider -> continuation.resume(provider) }
                    .onFailure { error -> continuation.resumeWithException(error) }
            },
            ContextCompat.getMainExecutor(context)
        )
        continuation.invokeOnCancellation { future.cancel(true) }
    }
