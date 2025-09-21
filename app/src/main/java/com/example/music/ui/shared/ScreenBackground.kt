package com.example.music.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.music.util.radialMultiGradientScrimBottomRight

/**
 * Composable for a screen's Background.
 */
@Composable
fun ScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()
            .radialMultiGradientScrimBottomRight(
                listOf(
                    MaterialTheme.colorScheme.onPrimary,
                    MaterialTheme.colorScheme.onSecondary
                )
            )
            // version of ScreenBackground's radialGradiantScrim that needs first Box's modifier above to have background set to colorScheme.background
//            .radialGradientScrimBottomRight(
//                listOf(
//                    MaterialTheme.colorScheme.primaryContainer,
//                    MaterialTheme.colorScheme.secondaryContainer
//                )
//            )
        )
        content()
    }
}