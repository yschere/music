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

package com.example.music.designsys.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import androidx.glance.GlanceModifier
//import androidx.glance.background
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.music.designsys.R
import com.example.music.designsys.theme.MusicShapes
import com.example.music.designsys.theme.MusicTypography
import com.example.music.designsys.theme.blueDarkSet
import com.example.music.designsys.theme.blueLightSet
//import android.media.ImageReader

/**
 * TODO: rework to use songPlayerData or album artwork
 * how do i reference image from song/mp3 file
 * how do i store that
 */
@Composable
fun AlbumImage(
    albumImage: Int = 0,
    //albumImage: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderBrush: Brush = thumbnailPlaceholderDefaultBrush(),
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.primary))
        return
    }

    var imagePainterState by remember {
        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
    }

    val imageLoader = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(albumImage)
            .crossfade(true)
            .build(),
        contentScale = contentScale,
        onState = { state -> imagePainterState = state }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when (imagePainterState) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(id = R.drawable.img_empty),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .background(placeholderBrush)
                        .fillMaxSize()
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.bpicon2),
            //painter = painterResource(albumImage), //trying to use drawable from res folder
            //painter = imageLoader, //uses coil imageLoader
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier.size(24.dp),
        )
    }
}

//keeping commented out until able to understand glance with compose better
//connected to glancewidget and core designsys
//@Composable
//fun AlbumImage_Widget(
//    albumImage: Int = 0,
//    //albumImage: String,
//    contentDescription: String?,
//    modifier: GlanceModifier = GlanceModifier,
//    contentScale: ContentScale = ContentScale.Crop,
//    placeholderBrush: Brush = thumbnailPlaceholderDefaultBrush(),
//) {
//    if (LocalInspectionMode.current) {
//        Box(modifier = modifier.background(MaterialTheme.colorScheme.primary))
//        return
//    }
//
//    var imagePainterState by remember {
//        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
//    }
//
//    val imageLoader = rememberAsyncImagePainter(
//        model = ImageRequest.Builder(LocalContext.current)
//            .data(albumImage)
//            .crossfade(true)
//            .build(),
//        contentScale = contentScale,
//        onState = { state -> imagePainterState = state }
//    )
//
//    Box(
//        modifier = modifier,
//        contentAlignment = Alignment.Center
//    ) {
//        when (imagePainterState) {
//            is AsyncImagePainter.State.Loading,
//            is AsyncImagePainter.State.Error -> {
//                Image(
//                    painter = painterResource(id = R.drawable.img_empty),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize()
//                )
//            }
//            else -> {
//                Box(
//                    modifier = Modifier
//                        .background(placeholderBrush)
//                        .fillMaxSize()
//
//                )
//            }
//        }
//
//        Image(
//            painter = painterResource(R.drawable.bpicon2),
//            //painter = painterResource(albumImage), //trying to use drawable from res folder
//            //painter = imageLoader, //uses coil imageLoader
//            contentDescription = contentDescription,
//            contentScale = contentScale,
//            modifier = modifier.size(24.dp),
//        )
//    }
//}

//@Composable
//fun LocalTheme(
//    darkMode: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
//) {
//    if (darkMode) {
//        MaterialTheme(
//            colorScheme = blueDarkColorSet,
//            typography = MusicTypography,
//            shapes = MusicShapes,
//            content = content
//        )
//    } else {
//        MaterialTheme(
//            colorScheme = blueLightColorSet,
//            typography = MusicTypography,
//            shapes = MusicShapes,
//            content = content
//        )
//    }
//}
//
//@Preview
//@Composable
//fun AlbumImagePreview() {
//    LocalTheme {
//        AlbumImage(
//            1,
//            "strings",
//        )
//    }
//
//}
///*
//    albumImage: Int = 0,
//    //albumImage: String,
//    contentDescription: String?,
//    modifier: Modifier = Modifier,
//    contentScale: ContentScale = ContentScale.Crop,
//    placeholderBrush: Brush = thumbnailPlaceholderDefaultBrush(),
// */