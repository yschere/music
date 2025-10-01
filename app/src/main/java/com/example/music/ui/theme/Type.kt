package com.example.music.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.music.designsys.theme.MusicTypography
import com.example.music.ui.tooling.CompLightPreview

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = MusicTypography.displayLarge,
    displayMedium = MusicTypography.displayMedium,
    displaySmall = MusicTypography.displaySmall,

    headlineLarge = MusicTypography.headlineLarge,
    headlineMedium = MusicTypography.headlineMedium,
    headlineSmall = MusicTypography.headlineSmall,

    titleLarge = MusicTypography.titleLarge, //og
    titleMedium = MusicTypography.titleMedium,
    titleSmall = MusicTypography.titleSmall,

    bodyLarge = MusicTypography.bodyLarge, //og
    bodyMedium = MusicTypography.bodyMedium, //og
    bodySmall = MusicTypography.bodySmall,

    labelLarge = MusicTypography.labelLarge,
    labelMedium = MusicTypography.labelMedium,
    labelSmall = MusicTypography.labelSmall, //og
)

@CompLightPreview
@Composable
fun TypographyPreview() {
    MusicTheme {
        Column {
            val text = "Library"

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "display large:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.weight(0.8f)
                )
            } // display large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "display medium:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(0.8f)
                )
            } // display medium
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "display small:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.weight(0.8f)
                )
            } // display small
            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "headline large:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(0.8f)
                )
            } // headline large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "headline medium:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(0.8f)
                )
            } // headline medium
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "headline small:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(0.8f)
                )
            } // headline small
            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "title large:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(0.8f)
                )
            } // title large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "title medium:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(0.8f)
                )
            } // title medium
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "title small:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(0.8f)
                )
            } // title small
            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "body large:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(0.8f)
                )
            } // body large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "body medium:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(0.8f)
                )
            } // body medium
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "body small:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.8f)
                )
            } // body small
            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "label large:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(0.8f)
                )
            } // label large
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "label medium:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(0.8f)
                )
            } // label medium
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "label small:",
                    modifier = Modifier.weight(0.2f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(0.8f)
                )
            } // label small
        }
    }
}