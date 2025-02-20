package com.example.music.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.music.util.radialGradientScrimBottomRight

/**
 * Composable for a screen's Background.
 */
@Composable
fun ScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrimBottomRight(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimary))//.copy(alpha = 0.9f))
        )
        content()
    }
}