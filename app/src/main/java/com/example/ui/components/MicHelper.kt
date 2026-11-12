package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MahadiViewModel

/**
 * Returns a click handler for mic buttons: asks for the microphone permission when needed,
 * starts listening, and stops listening if it is already active.
 */
@Composable
fun rememberMicToggle(viewModel: MahadiViewModel): () -> Unit {
    val context = LocalContext.current
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.startListening() else viewModel.onMicPermissionDenied()
    }
    return {
        if (isListening) {
            viewModel.stopListening()
        } else if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.startListening()
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
}
