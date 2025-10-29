package com.example.music.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.music.R
import com.example.music.designsys.theme.SCREEN_PADDING

/**
 * Composable for Error Screen: full screen with central retry message
 */
@Composable
fun Error(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        modifier = modifier
    ) {
        ScreenBackground(modifier = modifier) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = stringResource(id = R.string.an_error_has_occurred),
                    modifier = Modifier.padding(SCREEN_PADDING)
                )
                Button(onClick = onRetry) {
                    Text(text = stringResource(id = R.string.retry_label))
                }
            }
        }
    }
}