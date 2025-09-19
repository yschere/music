package com.example.music.designsys.theme

import android.annotation.SuppressLint
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.music.designsys.R

/**
 * App wide default colors
 */
val background = Color(0xFFFBFCFC)
val surface_bright = Color(0xFF313233)
val thumbnail_dark = Color(0xFF434343)
val thumbnail_light = Color(0xFFE9E9E9)

/**
 * Default Light Color Scheme from JetCaster
 */
@SuppressLint("ResourceAsColor")
val lightDefaultSetRsc = lightColorScheme(
    primary = Color(R.color.primary_light),
    onPrimary = Color(R.color.on_primary_light),
    primaryContainer = Color(R.color.primary_container_light),
    onPrimaryContainer = Color(R.color.primary_container_light),
    inversePrimary = Color(R.color.inverse_primary_light),
    secondary = Color(R.color.secondary_light),
    onSecondary = Color(R.color.on_secondary_light),
    secondaryContainer = Color(R.color.secondary_container_light),
    onSecondaryContainer = Color(R.color.on_secondary_container_light),
    tertiary = Color(R.color.tertiary_light),
    onTertiary = Color(R.color.on_tertiary_light),
    tertiaryContainer = Color(R.color.tertiary_container_light),
    onTertiaryContainer = Color(R.color.on_tertiary_container_light),
    background = Color(R.color.background_light),
    onBackground = Color(R.color.on_background_light),
    surface = Color(R.color.surface_light),
    onSurface = Color(R.color.on_surface_light),
    surfaceVariant = Color(R.color.surface_variant_light),
    onSurfaceVariant = Color(R.color.on_surface_variant_light),
    surfaceTint = Color(R.color.primary_light), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_light),
    inverseOnSurface = Color(R.color.inverse_on_surface_light),
    error = Color(R.color.error_light),
    onError = Color(R.color.on_error_light),
    errorContainer = Color(R.color.error_container_light),
    onErrorContainer = Color(R.color.on_error_container_light),
    outline = Color(R.color.outline_light),
    outlineVariant = Color(R.color.outline_variant_light),
    scrim = Color(R.color.scrim_light),
    surfaceBright = Color(R.color.surface_bright_light),
    surfaceContainer = Color(R.color.surface_container_light),
    surfaceContainerHigh = Color(R.color.surface_container_high_light),
    surfaceContainerHighest = Color(R.color.surface_container_highest_light),
    surfaceContainerLow = Color(R.color.surface_container_low_light),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_light),
    surfaceDim = Color(R.color.surface_dim_light),
)

/**
 * Default Dark Color Scheme from JetCaster
 */
@SuppressLint("ResourceAsColor")
val darkDefaultSetRsc = darkColorScheme(
    primary = Color(R.color.primary_dark),
    onPrimary = Color(R.color.on_primary_dark),
    primaryContainer = Color(R.color.primary_container_dark),
    onPrimaryContainer = Color(R.color.primary_container_dark),
    inversePrimary = Color(R.color.inverse_primary_dark),
    secondary = Color(R.color.secondary_dark),
    onSecondary = Color(R.color.on_secondary_dark),
    secondaryContainer = Color(R.color.secondary_container_dark),
    onSecondaryContainer = Color(R.color.on_secondary_container_dark),
    tertiary = Color(R.color.tertiary_dark),
    onTertiary = Color(R.color.on_tertiary_dark),
    tertiaryContainer = Color(R.color.tertiary_container_dark),
    onTertiaryContainer = Color(R.color.on_tertiary_container_dark),
    background = Color(R.color.background_dark),
    onBackground = Color(R.color.on_background_dark),
    surface = Color(R.color.surface_dark),
    onSurface = Color(R.color.on_surface_dark),
    surfaceVariant = Color(R.color.surface_variant_dark),
    onSurfaceVariant = Color(R.color.on_surface_variant_dark),
    surfaceTint = Color(R.color.primary_dark), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_dark),
    inverseOnSurface = Color(R.color.inverse_on_surface_dark),
    error = Color(R.color.error_dark),
    onError = Color(R.color.on_error_dark),
    errorContainer = Color(R.color.error_container_dark),
    onErrorContainer = Color(R.color.on_error_container_dark),
    outline = Color(R.color.outline_dark),
    outlineVariant = Color(R.color.outline_variant_dark),
    scrim = Color(R.color.scrim_dark),
    surfaceBright = Color(R.color.surface_bright_dark),
    surfaceContainer = Color(R.color.surface_container_dark),
    surfaceContainerHigh = Color(R.color.surface_container_high_dark),
    surfaceContainerHighest = Color(R.color.surface_container_highest_dark),
    surfaceContainerLow = Color(R.color.surface_container_low_dark),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_dark),
    surfaceDim = Color(R.color.surface_dim_dark),
)

/**
 * Blue toned Light Color Scheme
 */
@SuppressLint("ResourceAsColor")
val blueLightSet = lightColorScheme(
    primary = Color(R.color.primary_blue_light),
    onPrimary = Color(R.color.on_primary_blue_light),
    primaryContainer = Color(R.color.primary_container_blue_light),
    onPrimaryContainer = Color(R.color.primary_container_blue_light),
    secondary = Color(R.color.secondary_blue_light),
    onSecondary = Color(R.color.on_secondary_blue_light),
    secondaryContainer = Color(R.color.secondary_container_blue_light),
    onSecondaryContainer = Color(R.color.on_secondary_container_blue_light),
    tertiary = Color(R.color.tertiary_blue_light),
    onTertiary = Color(R.color.on_tertiary_blue_light),
    tertiaryContainer = Color(R.color.tertiary_container_blue_light),
    onTertiaryContainer = Color(R.color.on_tertiary_container_blue_light),
    background = Color(R.color.background_blue_light),
    onBackground = Color(R.color.on_background_blue_light),
    surface = Color(R.color.surface_blue_light),
    onSurface = Color(R.color.on_surface_blue_light),
    surfaceVariant = Color(R.color.surface_variant_blue_light),
    onSurfaceVariant = Color(R.color.on_surface_variant_blue_light),
    surfaceTint = Color(R.color.primary_blue_light), //not sure why but the documentation had this defaulting to primary
    error = Color(R.color.error_blue_light),
    onError = Color(R.color.on_error_blue_light),
    errorContainer = Color(R.color.error_container_blue_light),
    onErrorContainer = Color(R.color.on_error_container_blue_light),
    outline = Color(R.color.outline_blue_light),
    outlineVariant = Color(R.color.outline_variant_blue_light),
)

/**
 * Blue toned Dark Color Scheme
 */
@SuppressLint("ResourceAsColor")
val blueDarkSet = darkColorScheme(
    primary = Color(R.color.primary_blue_dark),
    onPrimary = Color(R.color.on_primary_blue_dark),
    primaryContainer = Color(R.color.primary_container_blue_dark),
    onPrimaryContainer = Color(R.color.primary_container_blue_dark),
    secondary = Color(R.color.secondary_blue_dark),
    onSecondary = Color(R.color.on_secondary_blue_dark),
    secondaryContainer = Color(R.color.secondary_container_blue_dark),
    onSecondaryContainer = Color(R.color.on_secondary_container_blue_dark),
    tertiary = Color(R.color.tertiary_blue_dark),
    onTertiary = Color(R.color.on_tertiary_blue_dark),
    tertiaryContainer = Color(R.color.tertiary_container_blue_dark),
    onTertiaryContainer = Color(R.color.on_tertiary_container_blue_dark),
    background = Color(R.color.background_blue_dark),
    onBackground = Color(R.color.on_background_blue_dark),
    surface = Color(R.color.surface_blue_dark),
    onSurface = Color(R.color.on_surface_blue_dark),
    surfaceVariant = Color(R.color.surface_variant_blue_dark),
    onSurfaceVariant = Color(R.color.on_surface_variant_blue_dark),
    surfaceTint = Color(R.color.primary_blue_dark), //not sure why but the documentation had this defaulting to primary
    error = Color(R.color.error_blue_dark),
    onError = Color(R.color.on_error_blue_dark),
    errorContainer = Color(R.color.error_container_blue_dark),
    onErrorContainer = Color(R.color.on_error_container_blue_dark),
    outline = Color(R.color.outline_blue_dark),
    outlineVariant = Color(R.color.outline_variant_blue_dark),
)

// -- Default Light Color Section --
val primary_light = Color(0xFF885200)
val on_primary_light = Color(0xFFFFFFFF)
val primary_container_light = Color(0xFFFFAC46)
val on_primary_container_light = Color(0xFF482900)

val secondary_light = Color(0xFF7A5817)
val on_secondary_light = Color(0xFFFFFFFF)
val secondary_container_light = Color(0xFFFFD798)
val on_secondary_container_light = Color(0xFF5C3F00)

val tertiary_light = Color(0xFF994700)
val on_tertiary_light = Color(0xFFFFFFFF)
val tertiary_container_light = Color(0xFFFF801F)
val on_tertiary_container_light = Color(0xFF2D1000)

val error_light = Color(0xFFA4384A)
val on_error_light = Color(0xFFFFFFFF)
val error_container_light = Color(0xFFF87889)
val on_error_container_light = Color(0xFF32000A)

val background_light = Color(0xFFFFF8F4)
val on_background_light = Color(0xFF221A11)

val surface_light = Color(0xFFFFF8F4)
val on_surface_light = Color(0xFF221A11)

val surface_variant_light = Color(0xFFF7DEC8)
val on_surface_variant_light = Color(0xFF544434)

val outline_light = Color(0xFF877461)
val outline_variant_light = Color(0xFFDAC3AD)

val scrim_light = Color(0xFF000000)

val inverse_surface_light = Color(0xFF382F25)
val inverse_on_surface_light = Color(0xFFFFEEDF)
val inverse_primary_light = Color(0xFFFFB868)
val surface_dim_light = Color(0xFFE8D7C9)
val surface_bright_light = Color(0xFFFFF8F4)

val surface_container_lowest_light = Color(0xFFFFFFFF)
val surface_container_low_light = Color(0xFFFFF1E6)
val surface_container_light = Color(0xFFFCEBDC)
val surface_container_high_light = Color(0xFFF6E5D7)
val surface_container_highest_light = Color(0xFFF1E0D1)

// -- Default Dark Color Section --
val primary_dark = Color(0xFFFFCF9E)
val on_primary_dark = Color(0xFF482900)
val primary_container_dark = Color(0xFFF79900)
val on_primary_container_dark = Color(0xFF371E00)

val secondary_dark = Color(0xFFFFFEFF)
val on_secondary_dark = Color(0xFF422C00)
val secondary_container_dark = Color(0xFFFBCC80)
val on_secondary_container_dark = Color(0xFF553A00)

val tertiary_dark = Color(0xFFFFB68B)
val on_tertiary_dark = Color(0xFF522300)
val tertiary_container_dark = Color(0xFFE76E00)
val on_tertiary_container_dark = Color(0xFF000000)

val error_dark = Color(0xFFFFB68B)
val on_error_dark = Color(0xFF522300)
val error_container_dark = Color(0xFFE76E00)
val on_error_container_dark = Color(0xFF000000)

val background_dark = Color(0xFF1A120A)
val on_background_dark = Color(0xFFF1E0D1)

val surface_dark = Color(0xFF1A120A)
val on_surface_dark = Color(0xFFF1E0D1)

val surface_variant_dark = Color(0xFF544434)
val on_surface_variant_dark = Color(0xFFDAC3AD)

val outline_dark = Color(0xFFA28D7A)
val outline_variant_dark = Color(0xFF544434)

val scrim_dark = Color(0xFF000000)

val inverse_surface_dark = Color(0xFFF1E0D1)
val inverse_on_surface_dark = Color(0xFF382F25)
val inverse_primary_dark = Color(0xFF885200)
val surface_dim_dark = Color(0xFF1A120A)
val surface_bright_dark = Color(0xFF42372D)

val surface_container_lowest_dark = Color(0xFF140D06)
val surface_container_low_dark = Color(0xFF221A11)
val surface_container_dark = Color(0xFF271E15)
val surface_container_high_dark = Color(0xFF32281F)
val surface_container_highest_dark = Color(0xFF3D3329)

@SuppressLint("ResourceAsColor")
val lightDefaultSet = lightColorScheme(
    primary = primary_light,
    onPrimary = on_primary_light,
    primaryContainer = primary_container_light,
    onPrimaryContainer = on_primary_container_light,
    inversePrimary = inverse_primary_light,
    secondary = secondary_light,
    onSecondary = on_secondary_light,
    secondaryContainer = secondary_container_light,
    onSecondaryContainer = on_secondary_container_light,
    tertiary = tertiary_light,
    onTertiary = on_tertiary_light,
    tertiaryContainer = tertiary_container_light,
    onTertiaryContainer = on_tertiary_container_light,
    background = background_light,
    onBackground = on_background_light,
    surface = surface_light,
    onSurface = on_surface_light,
    surfaceVariant = surface_variant_light,
    onSurfaceVariant = on_surface_variant_light,
    surfaceTint = primary_light, //not sure why but the documentation had this defaulting to primary
    inverseSurface = inverse_surface_light,
    inverseOnSurface = inverse_on_surface_light,
    error = error_light,
    onError = on_error_light,
    errorContainer = error_container_light,
    onErrorContainer = on_error_container_light,
    outline = outline_light,
    outlineVariant = outline_variant_light,
    scrim = scrim_light,
    surfaceBright = surface_bright_light,
    surfaceContainer = surface_container_light,
    surfaceContainerHigh = surface_container_high_light,
    surfaceContainerHighest = surface_container_highest_light,
    surfaceContainerLow = surface_container_low_light,
    surfaceContainerLowest = surface_container_lowest_light,
    surfaceDim = surface_dim_light,
)

/**
 * Default Dark Color Scheme from JetCaster
 */
@SuppressLint("ResourceAsColor")
val darkDefaultSet = darkColorScheme(
    primary = primary_dark,
    onPrimary = on_primary_dark,
    primaryContainer = primary_container_dark,
    onPrimaryContainer = on_primary_container_dark,
    inversePrimary = inverse_primary_dark,
    secondary = secondary_dark,
    onSecondary = on_secondary_dark,
    secondaryContainer = secondary_container_dark,
    onSecondaryContainer = on_secondary_container_dark,
    tertiary = tertiary_dark,
    onTertiary = on_tertiary_dark,
    tertiaryContainer = tertiary_container_dark,
    onTertiaryContainer = on_tertiary_container_dark,
    background = background_dark,
    onBackground = on_background_dark,
    surface = surface_dark,
    onSurface = on_surface_dark,
    surfaceVariant = surface_variant_dark,
    onSurfaceVariant = on_surface_variant_dark,
    surfaceTint = primary_dark, //not sure why but the documentation had this defaulting to primary
    inverseSurface = inverse_surface_dark,
    inverseOnSurface = inverse_on_surface_dark,
    error = error_dark,
    onError = on_error_dark,
    errorContainer = error_container_dark,
    onErrorContainer = on_error_container_dark,
    outline = outline_dark,
    outlineVariant = outline_variant_dark,
    scrim = scrim_dark,
    surfaceBright = surface_bright_dark,
    surfaceContainer = surface_container_dark,
    surfaceContainerHigh = surface_container_high_dark,
    surfaceContainerHighest = surface_container_highest_dark,
    surfaceContainerLow = surface_container_low_dark,
    surfaceContainerLowest = surface_container_lowest_dark,
    surfaceDim = surface_dim_dark,
)
