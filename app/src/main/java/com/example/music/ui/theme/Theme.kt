package com.example.music.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.example.music.designsys.theme.lightDefaultSet
import com.example.music.designsys.theme.darkDefaultSet

//public final val inversePrimary: Color
//
//Color to be used as a "primary" color in places where the inverse color scheme is needed, such as the button on a SnackBar
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
    surfaceTint = primaryBlueLight,
    error = errorBlueLight,
    onError = onErrorBlueLight,
    errorContainer = errorContainerBlueLight,
    onErrorContainer = onErrorContainerBlueLight,
    outline = outlineBlueLight,
    outlineVariant = outlineVariantBlueLight,
    scrim = scrimBlueLight,
    inverseSurface = inverseSurfaceBlueLight,
    inverseOnSurface = inverseOnSurfaceBlueLight,
    inversePrimary = inversePrimaryBlueLight,
    surfaceDim = surfaceDimBlueLight,
    surfaceBright = surfaceBrightBlueLight,
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
    surfaceTint = primaryBlueDark,
    error = errorBlueDark,
    onError = onErrorBlueDark,
    errorContainer = errorContainerBlueDark,
    onErrorContainer = onErrorContainerBlueDark,
    outline = outlineBlueDark,
    outlineVariant = outlineVariantBlueDark,
    scrim = scrimBlueDark,
    inverseSurface = inverseSurfaceBlueDark,
    inverseOnSurface = inverseOnSurfaceBlueDark,
    inversePrimary = inversePrimaryBlueDark,
    surfaceDim = surfaceDimBlueDark,
    surfaceBright = surfaceBrightBlueDark,
)

private val coolToneLightColorSet = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
)

private val coolToneDarkColorSet = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
)

@Composable
fun MusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        blueDarkColorSet
    } else {
        blueLightColorSet
    }

    val coolColors = if (darkTheme) {
        coolToneDarkColorSet
    } else {
        coolToneLightColorSet
    }

    val jetCasterColors = if (darkTheme) {
        darkDefaultSet
    } else {
        lightDefaultSet
    }

    MaterialTheme(
        colorScheme = coolColors,//colors,//jetCasterColors,//
        typography = Typography,
        content = content
    )
}