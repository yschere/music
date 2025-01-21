/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.designsys.theme

import android.annotation.SuppressLint
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.music.designsys.R
//want to describe the base color palettes in use across MusicApp

@SuppressLint("ResourceAsColor")
val lightDefaultSet = lightColorScheme(
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

@SuppressLint("ResourceAsColor")
val lightMediumContrastSet = lightColorScheme(
    primary = Color(R.color.primary_medium_contrast_light),
    onPrimary = Color(R.color.on_primary_medium_contrast_light),
    primaryContainer = Color(R.color.primary_container_medium_contrast_light),
    onPrimaryContainer = Color(R.color.primary_container_medium_contrast_light),
    inversePrimary = Color(R.color.inverse_primary_medium_contrast_light),
    secondary = Color(R.color.secondary_medium_contrast_light),
    onSecondary = Color(R.color.on_secondary_medium_contrast_light),
    secondaryContainer = Color(R.color.secondary_container_medium_contrast_light),
    onSecondaryContainer = Color(R.color.on_secondary_container_medium_contrast_light),
    tertiary = Color(R.color.tertiary_medium_contrast_light),
    onTertiary = Color(R.color.on_tertiary_medium_contrast_light),
    tertiaryContainer = Color(R.color.tertiary_container_medium_contrast_light),
    onTertiaryContainer = Color(R.color.on_tertiary_container_medium_contrast_light),
    background = Color(R.color.background_medium_contrast_light),
    onBackground = Color(R.color.on_background_medium_contrast_light),
    surface = Color(R.color.surface_medium_contrast_light),
    onSurface = Color(R.color.on_surface_medium_contrast_light),
    surfaceVariant = Color(R.color.surface_variant_medium_contrast_light),
    onSurfaceVariant = Color(R.color.on_surface_variant_medium_contrast_light),
    surfaceTint = Color(R.color.primary_medium_contrast_light), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_medium_contrast_light),
    inverseOnSurface = Color(R.color.inverse_on_surface_medium_contrast_light),
    error = Color(R.color.error_medium_contrast_light),
    onError = Color(R.color.on_error_medium_contrast_light),
    errorContainer = Color(R.color.error_container_medium_contrast_light),
    onErrorContainer = Color(R.color.on_error_container_medium_contrast_light),
    outline = Color(R.color.outline_medium_contrast_light),
    outlineVariant = Color(R.color.outline_variant_medium_contrast_light),
    scrim = Color(R.color.scrim_medium_contrast_light),
    surfaceBright = Color(R.color.surface_bright_medium_contrast_light),
    surfaceContainer = Color(R.color.surface_container_medium_contrast_light),
    surfaceContainerHigh = Color(R.color.surface_container_high_medium_contrast_light),
    surfaceContainerHighest = Color(R.color.surface_container_highest_medium_contrast_light),
    surfaceContainerLow = Color(R.color.surface_container_low_medium_contrast_light),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_medium_contrast_light),
    surfaceDim = Color(R.color.surface_dim_medium_contrast_light),
)

@SuppressLint("ResourceAsColor")
val lightHighContrastSet = lightColorScheme(
    primary = Color(R.color.primary_high_contrast_light),
    onPrimary = Color(R.color.on_primary_high_contrast_light),
    primaryContainer = Color(R.color.primary_container_high_contrast_light),
    onPrimaryContainer = Color(R.color.primary_container_high_contrast_light),
    inversePrimary = Color(R.color.inverse_primary_high_contrast_light),
    secondary = Color(R.color.secondary_high_contrast_light),
    onSecondary = Color(R.color.on_secondary_high_contrast_light),
    secondaryContainer = Color(R.color.secondary_container_high_contrast_light),
    onSecondaryContainer = Color(R.color.on_secondary_container_high_contrast_light),
    tertiary = Color(R.color.tertiary_high_contrast_light),
    onTertiary = Color(R.color.on_tertiary_high_contrast_light),
    tertiaryContainer = Color(R.color.tertiary_container_high_contrast_light),
    onTertiaryContainer = Color(R.color.on_tertiary_container_high_contrast_light),
    background = Color(R.color.background_high_contrast_light),
    onBackground = Color(R.color.on_background_high_contrast_light),
    surface = Color(R.color.surface_high_contrast_light),
    onSurface = Color(R.color.on_surface_high_contrast_light),
    surfaceVariant = Color(R.color.surface_variant_high_contrast_light),
    onSurfaceVariant = Color(R.color.on_surface_variant_high_contrast_light),
    surfaceTint = Color(R.color.primary_high_contrast_light), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_high_contrast_light),
    inverseOnSurface = Color(R.color.inverse_on_surface_high_contrast_light),
    error = Color(R.color.error_high_contrast_light),
    onError = Color(R.color.on_error_high_contrast_light),
    errorContainer = Color(R.color.error_container_high_contrast_light),
    onErrorContainer = Color(R.color.on_error_container_high_contrast_light),
    outline = Color(R.color.outline_high_contrast_light),
    outlineVariant = Color(R.color.outline_variant_high_contrast_light),
    scrim = Color(R.color.scrim_high_contrast_light),
    surfaceBright = Color(R.color.surface_bright_high_contrast_light),
    surfaceContainer = Color(R.color.surface_container_high_contrast_light),
    surfaceContainerHigh = Color(R.color.surface_container_high_high_contrast_light),
    surfaceContainerHighest = Color(R.color.surface_container_highest_high_contrast_light),
    surfaceContainerLow = Color(R.color.surface_container_low_high_contrast_light),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_high_contrast_light),
    surfaceDim = Color(R.color.surface_dim_high_contrast_light),
)

@SuppressLint("ResourceAsColor")
val darkDefaultSet = darkColorScheme(
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

@SuppressLint("ResourceAsColor")
val darkMediumContrastSet = darkColorScheme(
    primary = Color(R.color.primary_medium_contrast_dark),
    onPrimary = Color(R.color.on_primary_medium_contrast_dark),
    primaryContainer = Color(R.color.primary_container_medium_contrast_dark),
    onPrimaryContainer = Color(R.color.primary_container_medium_contrast_dark),
    inversePrimary = Color(R.color.inverse_primary_medium_contrast_dark),
    secondary = Color(R.color.secondary_medium_contrast_dark),
    onSecondary = Color(R.color.on_secondary_medium_contrast_dark),
    secondaryContainer = Color(R.color.secondary_container_medium_contrast_dark),
    onSecondaryContainer = Color(R.color.on_secondary_container_medium_contrast_dark),
    tertiary = Color(R.color.tertiary_medium_contrast_dark),
    onTertiary = Color(R.color.on_tertiary_medium_contrast_dark),
    tertiaryContainer = Color(R.color.tertiary_container_medium_contrast_dark),
    onTertiaryContainer = Color(R.color.on_tertiary_container_medium_contrast_dark),
    background = Color(R.color.background_medium_contrast_dark),
    onBackground = Color(R.color.on_background_medium_contrast_dark),
    surface = Color(R.color.surface_medium_contrast_dark),
    onSurface = Color(R.color.on_surface_medium_contrast_dark),
    surfaceVariant = Color(R.color.surface_variant_medium_contrast_dark),
    onSurfaceVariant = Color(R.color.on_surface_variant_medium_contrast_dark),
    surfaceTint = Color(R.color.primary_medium_contrast_dark), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_medium_contrast_dark),
    inverseOnSurface = Color(R.color.inverse_on_surface_medium_contrast_dark),
    error = Color(R.color.error_medium_contrast_dark),
    onError = Color(R.color.on_error_medium_contrast_dark),
    errorContainer = Color(R.color.error_container_medium_contrast_dark),
    onErrorContainer = Color(R.color.on_error_container_medium_contrast_dark),
    outline = Color(R.color.outline_medium_contrast_dark),
    outlineVariant = Color(R.color.outline_variant_medium_contrast_dark),
    scrim = Color(R.color.scrim_medium_contrast_dark),
    surfaceBright = Color(R.color.surface_bright_medium_contrast_dark),
    surfaceContainer = Color(R.color.surface_container_medium_contrast_dark),
    surfaceContainerHigh = Color(R.color.surface_container_high_medium_contrast_dark),
    surfaceContainerHighest = Color(R.color.surface_container_highest_medium_contrast_dark),
    surfaceContainerLow = Color(R.color.surface_container_low_medium_contrast_dark),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_medium_contrast_dark),
    surfaceDim = Color(R.color.surface_dim_medium_contrast_dark),
)

@SuppressLint("ResourceAsColor")
val darkHighContrastSet = darkColorScheme(
    primary = Color(R.color.primary_high_contrast_dark),
    onPrimary = Color(R.color.on_primary_high_contrast_dark),
    primaryContainer = Color(R.color.primary_container_high_contrast_dark),
    onPrimaryContainer = Color(R.color.primary_container_high_contrast_dark),
    inversePrimary = Color(R.color.inverse_primary_high_contrast_dark),
    secondary = Color(R.color.secondary_high_contrast_dark),
    onSecondary = Color(R.color.on_secondary_high_contrast_dark),
    secondaryContainer = Color(R.color.secondary_container_high_contrast_dark),
    onSecondaryContainer = Color(R.color.on_secondary_container_high_contrast_dark),
    tertiary = Color(R.color.tertiary_high_contrast_dark),
    onTertiary = Color(R.color.on_tertiary_high_contrast_dark),
    tertiaryContainer = Color(R.color.tertiary_container_high_contrast_dark),
    onTertiaryContainer = Color(R.color.on_tertiary_container_high_contrast_dark),
    background = Color(R.color.background_high_contrast_dark),
    onBackground = Color(R.color.on_background_high_contrast_dark),
    surface = Color(R.color.surface_high_contrast_dark),
    onSurface = Color(R.color.on_surface_high_contrast_dark),
    surfaceVariant = Color(R.color.surface_variant_high_contrast_dark),
    onSurfaceVariant = Color(R.color.on_surface_variant_high_contrast_dark),
    surfaceTint = Color(R.color.primary_high_contrast_dark), //not sure why but the documentation had this defaulting to primary
    inverseSurface = Color(R.color.inverse_surface_high_contrast_dark),
    inverseOnSurface = Color(R.color.inverse_on_surface_high_contrast_dark),
    error = Color(R.color.error_high_contrast_dark),
    onError = Color(R.color.on_error_high_contrast_dark),
    errorContainer = Color(R.color.error_container_high_contrast_dark),
    onErrorContainer = Color(R.color.on_error_container_high_contrast_dark),
    outline = Color(R.color.outline_high_contrast_dark),
    outlineVariant = Color(R.color.outline_variant_high_contrast_dark),
    scrim = Color(R.color.scrim_high_contrast_dark),
    surfaceBright = Color(R.color.surface_bright_high_contrast_dark),
    surfaceContainer = Color(R.color.surface_container_high_contrast_dark),
    surfaceContainerHigh = Color(R.color.surface_container_high_high_contrast_dark),
    surfaceContainerHighest = Color(R.color.surface_container_highest_high_contrast_dark),
    surfaceContainerLow = Color(R.color.surface_container_low_high_contrast_dark),
    surfaceContainerLowest = Color(R.color.surface_container_lowest_high_contrast_dark),
    surfaceDim = Color(R.color.surface_dim_high_contrast_dark),
)

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

//val primaryLight = Color(0xFF885200)
//val onPrimaryLight = Color(0xFFFFFFFF)
//val primaryContainerLight = Color(0xFFFFAC46)
//val onPrimaryContainerLight = Color(0xFF482900)
//val secondaryLight = Color(0xFF7A5817)
//val onSecondaryLight = Color(0xFFFFFFFF)
//val secondaryContainerLight = Color(0xFFFFD798)
//val onSecondaryContainerLight = Color(0xFF5C3F00)
//val tertiaryLight = Color(0xFF994700)
//val onTertiaryLight = Color(0xFFFFFFFF)
//val tertiaryContainerLight = Color(0xFFFF801F)
//val onTertiaryContainerLight = Color(0xFF2D1000)
//val errorLight = Color(0xFFA4384A)
//val onErrorLight = Color(0xFFFFFFFF)
//val errorContainerLight = Color(0xFFF87889)
//val onErrorContainerLight = Color(0xFF32000A)
//val backgroundLight = Color(0xFFFFF8F4)
//val onBackgroundLight = Color(0xFF221A11)
//val surfaceLight = Color(0xFFFFF8F4)
//val onSurfaceLight = Color(0xFF221A11)
//val surfaceVariantLight = Color(0xFFF7DEC8)
//val onSurfaceVariantLight = Color(0xFF544434)
//val outlineLight = Color(0xFF877461)
//val outlineVariantLight = Color(0xFFDAC3AD)
//val scrimLight = Color(0xFF000000)
//val inverseSurfaceLight = Color(0xFF382F25)
//val inverseOnSurfaceLight = Color(0xFFFFEEDF)
//val inversePrimaryLight = Color(0xFFFFB868)
//val surfaceDimLight = Color(0xFFE8D7C9)
//val surfaceBrightLight = Color(0xFFFFF8F4)
//val surfaceContainerLowestLight = Color(0xFFFFFFFF)
//val surfaceContainerLowLight = Color(0xFFFFF1E6)
//val surfaceContainerLight = Color(0xFFFCEBDC)
//val surfaceContainerHighLight = Color(0xFFF6E5D7)
//val surfaceContainerHighestLight = Color(0xFFF1E0D1)

//val primaryLightMediumContrast = Color(0xFF623A00)
//val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
//val primaryContainerLightMediumContrast = Color(0xFFA76600)
//val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
//val secondaryLightMediumContrast = Color(0xFF5A3D00)
//val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
//val secondaryContainerLightMediumContrast = Color(0xFF936E2B)
//val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
//val tertiaryLightMediumContrast = Color(0xFF6F3100)
//val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
//val tertiaryContainerLightMediumContrast = Color(0xFFBC5800)
//val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
//val errorLightMediumContrast = Color(0xFF7F1B30)
//val onErrorLightMediumContrast = Color(0xFFFFFFFF)
//val errorContainerLightMediumContrast = Color(0xFFC14E5F)
//val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
//val backgroundLightMediumContrast = Color(0xFFFFF8F4)
//val onBackgroundLightMediumContrast = Color(0xFF221A11)
//val surfaceLightMediumContrast = Color(0xFFFFF8F4)
//val onSurfaceLightMediumContrast = Color(0xFF221A11)
//val surfaceVariantLightMediumContrast = Color(0xFFF7DEC8)
//val onSurfaceVariantLightMediumContrast = Color(0xFF504030)
//val outlineLightMediumContrast = Color(0xFF6E5C4A)
//val outlineVariantLightMediumContrast = Color(0xFF8B7765)
//val scrimLightMediumContrast = Color(0xFF000000)
//val inverseSurfaceLightMediumContrast = Color(0xFF382F25)
//val inverseOnSurfaceLightMediumContrast = Color(0xFFFFEEDF)
//val inversePrimaryLightMediumContrast = Color(0xFFFFB868)
//val surfaceDimLightMediumContrast = Color(0xFFE8D7C9)
//val surfaceBrightLightMediumContrast = Color(0xFFFFF8F4)
//val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
//val surfaceContainerLowLightMediumContrast = Color(0xFFFFF1E6)
//val surfaceContainerLightMediumContrast = Color(0xFFFCEBDC)
//val surfaceContainerHighLightMediumContrast = Color(0xFFF6E5D7)
//val surfaceContainerHighestLightMediumContrast = Color(0xFFF1E0D1)
//
//val primaryLightHighContrast = Color(0xFF351D00)
//val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
//val primaryContainerLightHighContrast = Color(0xFF623A00)
//val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
//val secondaryLightHighContrast = Color(0xFF301F00)
//val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
//val secondaryContainerLightHighContrast = Color(0xFF5A3D00)
//val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
//val tertiaryLightHighContrast = Color(0xFF3C1800)
//val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
//val tertiaryContainerLightHighContrast = Color(0xFF6F3100)
//val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
//val errorLightHighContrast = Color(0xFF4C0014)
//val onErrorLightHighContrast = Color(0xFFFFFFFF)
//val errorContainerLightHighContrast = Color(0xFF7F1B30)
//val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
//val backgroundLightHighContrast = Color(0xFFFFF8F4)
//val onBackgroundLightHighContrast = Color(0xFF221A11)
//val surfaceLightHighContrast = Color(0xFFFFF8F4)
//val onSurfaceLightHighContrast = Color(0xFF000000)
//val surfaceVariantLightHighContrast = Color(0xFFF7DEC8)
//val onSurfaceVariantLightHighContrast = Color(0xFF2E2113)
//val outlineLightHighContrast = Color(0xFF504030)
//val outlineVariantLightHighContrast = Color(0xFF504030)
//val scrimLightHighContrast = Color(0xFF000000)
//val inverseSurfaceLightHighContrast = Color(0xFF382F25)
//val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
//val inversePrimaryLightHighContrast = Color(0xFFFFE8D4)
//val surfaceDimLightHighContrast = Color(0xFFE8D7C9)
//val surfaceBrightLightHighContrast = Color(0xFFFFF8F4)
//val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
//val surfaceContainerLowLightHighContrast = Color(0xFFFFF1E6)
//val surfaceContainerLightHighContrast = Color(0xFFFCEBDC)
//val surfaceContainerHighLightHighContrast = Color(0xFFF6E5D7)
//val surfaceContainerHighestLightHighContrast = Color(0xFFF1E0D1)
//
//val primaryDark = Color(0xFFFFCF9E)
//val onPrimaryDark = Color(0xFF482900)
//val primaryContainerDark = Color(0xFFF79900)
//val onPrimaryContainerDark = Color(0xFF371E00)
//val secondaryDark = Color(0xFFFFFEFF)
//val onSecondaryDark = Color(0xFF422C00)
//val secondaryContainerDark = Color(0xFFFBCC80)
//val onSecondaryContainerDark = Color(0xFF553A00)
//val tertiaryDark = Color(0xFFFFB68B)
//val onTertiaryDark = Color(0xFF522300)
//val tertiaryContainerDark = Color(0xFFE76E00)
//val onTertiaryContainerDark = Color(0xFF000000)
//val errorDark = Color(0xFFFFB2B9)
//val onErrorDark = Color(0xFF65041F)
//val errorContainerDark = Color(0xFFC14E5F)
//val onErrorContainerDark = Color(0xFFFFFFFF)
//val backgroundDark = Color(0xFF1A120A)
//val onBackgroundDark = Color(0xFFF1E0D1)
//val surfaceDark = Color(0xFF1A120A)
//val onSurfaceDark = Color(0xFFF1E0D1)
//val surfaceVariantDark = Color(0xFF544434)
//val onSurfaceVariantDark = Color(0xFFDAC3AD)
//val outlineDark = Color(0xFFA28D7A)
//val outlineVariantDark = Color(0xFF544434)
//val scrimDark = Color(0xFF000000)
//val inverseSurfaceDark = Color(0xFFF1E0D1)
//val inverseOnSurfaceDark = Color(0xFF382F25)
//val inversePrimaryDark = Color(0xFF885200)
//val surfaceDimDark = Color(0xFF1A120A)
//val surfaceBrightDark = Color(0xFF42372D)
//val surfaceContainerLowestDark = Color(0xFF140D06)
//val surfaceContainerLowDark = Color(0xFF221A11)
//val surfaceContainerDark = Color(0xFF271E15)
//val surfaceContainerHighDark = Color(0xFF32281F)
//val surfaceContainerHighestDark = Color(0xFF3D3329)
//
//val primaryDarkMediumContrast = Color(0xFFFFCF9E)
//val onPrimaryDarkMediumContrast = Color(0xFF351D00)
//val primaryContainerDarkMediumContrast = Color(0xFFF79900)
//val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
//val secondaryDarkMediumContrast = Color(0xFFFFFEFF)
//val onSecondaryDarkMediumContrast = Color(0xFF422C00)
//val secondaryContainerDarkMediumContrast = Color(0xFFFBCC80)
//val onSecondaryContainerDarkMediumContrast = Color(0xFF2C1C00)
//val tertiaryDarkMediumContrast = Color(0xFFFFBC95)
//val onTertiaryDarkMediumContrast = Color(0xFF2A0E00)
//val tertiaryContainerDarkMediumContrast = Color(0xFFE76E00)
//val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
//val errorDarkMediumContrast = Color(0xFFFFB8BE)
//val onErrorDarkMediumContrast = Color(0xFF36000C)
//val errorContainerDarkMediumContrast = Color(0xFFE5697A)
//val onErrorContainerDarkMediumContrast = Color(0xFF000000)
//val backgroundDarkMediumContrast = Color(0xFF1A120A)
//val onBackgroundDarkMediumContrast = Color(0xFFF1E0D1)
//val surfaceDarkMediumContrast = Color(0xFF1A120A)
//val onSurfaceDarkMediumContrast = Color(0xFFFFFAF8)
//val surfaceVariantDarkMediumContrast = Color(0xFF544434)
//val onSurfaceVariantDarkMediumContrast = Color(0xFFDEC7B1)
//val outlineDarkMediumContrast = Color(0xFFB59F8B)
//val outlineVariantDarkMediumContrast = Color(0xFF93806D)
//val scrimDarkMediumContrast = Color(0xFF000000)
//val inverseSurfaceDarkMediumContrast = Color(0xFFF1E0D1)
//val inverseOnSurfaceDarkMediumContrast = Color(0xFF32281F)
//val inversePrimaryDarkMediumContrast = Color(0xFF693E00)
//val surfaceDimDarkMediumContrast = Color(0xFF1A120A)
//val surfaceBrightDarkMediumContrast = Color(0xFF42372D)
//val surfaceContainerLowestDarkMediumContrast = Color(0xFF140D06)
//val surfaceContainerLowDarkMediumContrast = Color(0xFF221A11)
//val surfaceContainerDarkMediumContrast = Color(0xFF271E15)
//val surfaceContainerHighDarkMediumContrast = Color(0xFF32281F)
//val surfaceContainerHighestDarkMediumContrast = Color(0xFF3D3329)
//
//val primaryDarkHighContrast = Color(0xFFFFFAF8)
//val onPrimaryDarkHighContrast = Color(0xFF000000)
//val primaryContainerDarkHighContrast = Color(0xFFFFBE76)
//val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
//val secondaryDarkHighContrast = Color(0xFFFFFEFF)
//val onSecondaryDarkHighContrast = Color(0xFF000000)
//val secondaryContainerDarkHighContrast = Color(0xFFFBCC80)
//val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
//val tertiaryDarkHighContrast = Color(0xFFFFFAF8)
//val onTertiaryDarkHighContrast = Color(0xFF000000)
//val tertiaryContainerDarkHighContrast = Color(0xFFFFBC95)
//val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
//val errorDarkHighContrast = Color(0xFFFFF9F9)
//val onErrorDarkHighContrast = Color(0xFF000000)
//val errorContainerDarkHighContrast = Color(0xFFFFB8BE)
//val onErrorContainerDarkHighContrast = Color(0xFF000000)
//val backgroundDarkHighContrast = Color(0xFF1A120A)
//val onBackgroundDarkHighContrast = Color(0xFFF1E0D1)
//val surfaceDarkHighContrast = Color(0xFF1A120A)
//val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
//val surfaceVariantDarkHighContrast = Color(0xFF544434)
//val onSurfaceVariantDarkHighContrast = Color(0xFFFFFAF8)
//val outlineDarkHighContrast = Color(0xFFDEC7B1)
//val outlineVariantDarkHighContrast = Color(0xFFDEC7B1)
//val scrimDarkHighContrast = Color(0xFF000000)
//val inverseSurfaceDarkHighContrast = Color(0xFFF1E0D1)
//val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
//val inversePrimaryDarkHighContrast = Color(0xFF3F2400)
//val surfaceDimDarkHighContrast = Color(0xFF1A120A)
//val surfaceBrightDarkHighContrast = Color(0xFF42372D)
//val surfaceContainerLowestDarkHighContrast = Color(0xFF140D06)
//val surfaceContainerLowDarkHighContrast = Color(0xFF221A11)
//val surfaceContainerDarkHighContrast = Color(0xFF271E15)
//val surfaceContainerHighDarkHighContrast = Color(0xFF32281F)
//val surfaceContainerHighestDarkHighContrast = Color(0xFF3D3329)

val PrimaryFR = Color(0xFF25ACE8)
val OnPrimaryFR = Color(0xFFFFFFFF)
val PrimaryContainerFR = Color(0xFFA9D3E6)
val OnPrimaryContainerFR = Color(0xFF082633)

val SecondaryFR = Color(0xFF6173D5)
val OnSecondaryFR = Color(0xFFFFFFFF)
val SecondaryContainerFR = Color(0xFFBEC4E6)
val OnSecondaryContainerFR = Color(0xFF171C33)

val TertiaryFR = Color(0xFF7D5260)
val OnTertiaryFR = Color(0xFFFFFFFF)
val TertiaryContainerFR = Color(0xFFE6CDD5)
val OnTertiaryContainerFR = Color(0xFF332227)

val ErrorFR = Color(0xFFB3261E)
val OnErrorFR = Color(0xFFFFFFFF)
val ErrorContainerFR = Color(0xFFE6ACA9)
val OnErrorContainerFR = Color(0xFF330B09)

val BackgroundFR = Color(0xFFfbfcfc)
val OnBackgroundFR = Color(0xFF313233)

val SurfaceFR = Color(0xFFfbfcfc)
val OnSurfaceFR = Color(0xFF313233)
val SurfaceVariantFR = Color(0xFFd9e2e6)
val OnSurfaceVariantFR = Color(0xFF556166)
val OutlineFR = Color(0xFF7f9199)

val PrimaryDarkFR = Color(0xFF8FCBE6)
val OnPrimaryDarkFR = Color(0xFF0C394C)
val PrimaryContainerDarkFR = Color(0xFF104B66)
val OnPrimaryContainerDarkFR = Color(0xFFA9D3E6)

val SecondaryDarkFR = Color(0xFFAEB6E6)
val OnSecondaryDarkFR = Color(0xFF23294C)
val SecondaryContainerDarkFR = Color(0xFF2F3766)
val OnSecondaryContainerDarkFR = Color(0xFFBEC4E6)

val TertiaryDarkFR = Color(0xFFE6C3CE)
val OnTertiaryDarkFR = Color(0xFF4C323B)
val TertiaryContainerDarkFR = Color(0xFF66434F)
val OnTertiaryContainerDarkFR = Color(0xFFE6CDD5)

val ErrorDarkFR = Color(0xFFE69490)
val OnErrorDarkFR = Color(0xFF4C100D)
val ErrorContainerDarkFR = Color(0xFF661511)
val OnErrorContainerDarkFR = Color(0xFFE6ACA9)

val BackgroundDarkFR = Color(0xFF313233)
val OnBackgroundDarkFR = Color(0xFFe2e5e6)
val SurfaceDarkFR = Color(0xFF313233)
val OnSurfaceDarkFR = Color(0xFFe2e5e6)
val SurfaceVariantDarkFR = Color(0xFF556166)
val OnSurfaceVariantDarkFR = Color(0xFFd4e0e6)
val OutlineDarkFR = Color(0xFFa0adb3)