package com.example.music.ui.theme

import androidx.compose.material3.Typography
import com.example.music.designsys.theme.MusicTypography
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.sp

//adjusted so that the typography is pulling from core/designsys instead of being overwritten

// Set of Material typography styles to start with
val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    ),
    bodyLarge = MusicTypography.bodyLarge,
    /* Other default text styles to override*/
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
    titleLarge = MusicTypography.titleLarge,
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    ),
    labelSmall = MusicTypography.labelSmall,
//    bodyMedium = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 12.sp,
//        lineHeight = 18.sp,
//        letterSpacing = 0.5.sp
//    ),
    bodyMedium = MusicTypography.bodyMedium,
)