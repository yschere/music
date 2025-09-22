package com.example.music.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Composable for Loading Screen: full screen with circular progress indicator
 */
@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                Modifier.align(Alignment.Center)
            )
        }
    }
}
