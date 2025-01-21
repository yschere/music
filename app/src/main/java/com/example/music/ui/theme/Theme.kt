package com.example.music.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.music.designsys.theme.blueLightSet
import com.example.music.designsys.theme.blueDarkSet

//shifted the scheme declarations into core/designsys/theme/color.kt
//shifted the color declarations into core/designsys/res/values xml files
//AND THEN shifted back because trying it in greetingcard project seemed like it works best when the color and theme declarations are within the module itself

@SuppressLint("ResourceAsColor")
val blueLightColorSet = lightColorScheme(
    primary = primaryBlueLight,
    onPrimary = onPrimaryBlueLight,
    primaryContainer = primaryContainerBlueLight,
    onPrimaryContainer = onPrimaryContainerBlueLight,
    secondary = secondaryBlueLight,
    onSecondary = onSecondaryBlueLight,
    secondaryContainer = secondaryContainerBlueLight,
    onSecondaryContainer = onSecondaryContainerBlueLight,
    tertiary = tertiaryBlueLight,
    onTertiary = onTertiaryBlueLight,
    tertiaryContainer = tertiaryContainerBlueLight,
    onTertiaryContainer = onTertiaryContainerBlueLight,
    background = backgroundBlueLight,
    onBackground = onBackgroundBlueLight,
    surface = surfaceBlueLight,
    onSurface = onSurfaceBlueLight,
    surfaceVariant = surfaceVariantBlueLight,
    onSurfaceVariant = onSurfaceVariantBlueLight,
    surfaceTint = primaryBlueLight, //not sure why but the documentation had this defaulting to primary
    error = errorBlueLight,
    onError = onErrorBlueLight,
    errorContainer = errorContainerBlueLight,
    onErrorContainer = onErrorContainerBlueLight,
    outline = outlineBlueLight,
    outlineVariant = outlineVariantBlueLight,
)

@SuppressLint("ResourceAsColor")
val blueDarkColorSet = darkColorScheme(
    primary = primaryBlueDark,
    onPrimary = onPrimaryBlueDark,
    primaryContainer = primaryContainerBlueDark,
    onPrimaryContainer = onPrimaryContainerBlueDark,
    secondary = secondaryBlueDark,
    onSecondary = onSecondaryBlueDark,
    secondaryContainer = secondaryContainerBlueDark,
    onSecondaryContainer = onSecondaryContainerBlueDark,
    tertiary = tertiaryBlueDark,
    onTertiary = onTertiaryBlueDark,
    tertiaryContainer = tertiaryContainerBlueDark,
    onTertiaryContainer = onTertiaryContainerBlueDark,
    background = backgroundBlueDark,
    onBackground = onBackgroundBlueDark,
    surface = surfaceBlueDark,
    onSurface = onSurfaceBlueDark,
    surfaceVariant = surfaceVariantBlueDark,
    onSurfaceVariant = onSurfaceVariantBlueDark,
    surfaceTint = primaryBlueDark, //not sure why but the documentation had this defaulting to primary
    error = errorBlueDark,
    onError = onErrorBlueDark,
    errorContainer = errorContainerBlueDark,
    onErrorContainer = onErrorContainerBlueDark,
    outline = outlineBlueDark,
    outlineVariant = outlineVariantBlueDark,
)

@Composable
fun MusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) {
        blueLightColorSet
    } else {
        blueDarkColorSet
    }
//    val colorScheme = when {
//        darkTheme -> blueDarkSet
//        else -> blueLightSet
//    }

    MaterialTheme(
        colorScheme = colors,
        //colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}