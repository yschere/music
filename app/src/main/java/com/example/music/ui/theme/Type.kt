package com.example.music.ui.theme

import androidx.compose.material3.Typography
import com.example.music.designsys.theme.MusicTypography

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = MusicTypography.bodyLarge,
    titleLarge = MusicTypography.titleLarge,
    labelSmall = MusicTypography.labelSmall,
//    bodyMedium = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 12.sp, //fontSize = 14.sp,
//        lineHeight = 18.sp, //lineHeight = 20.sp,
//        letterSpacing = 0.5.sp //letterSpacing = 0.25sp
//    ),
    bodyMedium = MusicTypography.bodyMedium,
)