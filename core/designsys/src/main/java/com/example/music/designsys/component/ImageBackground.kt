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

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageBackgroundColorScrim(
    imageId: String?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

@Composable
fun ImageBackgroundRadialGradientScrim(
    imageId: String?, //FixMe: this needs to be either bitmap or uri
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset((0f), (size.height)),
                radius = size.width * 2.5f
            )
            drawRect(brush, blendMode = BlendMode.Multiply)
        }
    )
}

/**
 * Displays an image scaled 150% overlaid by [overlay]
 */
@Composable
fun ImageBackground(
    imageId: String?,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageId,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
    )
}


/***********************************************************************************************
 *
 * **********  IMAGE BACKGROUND USING BITMAP ***********
 *
 **********************************************************************************************/

@Composable
fun ImageBackgroundColorScrim_Bm(
    imageId: Bitmap?,
    imageDescription: String,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    ImageBackground_Bm(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

@Composable
fun ImageBackgroundRadialGradientScrim_Bm(
    imageId: Bitmap?,
    imageDescription: String,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground_Bm(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            // trying to change center away from bottom left corner
//            val brush = Brush.radialGradient(
//                colors = colors,
//                center = Offset((size.width * 0.25f), (size.height * 0.6f) ),
//                radius = size.height * 1.3f
//            )
            //original brush
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset((0F), (size.height)),
                radius = size.width * 3F
            )
            drawRect(brush, blendMode = BlendMode.Multiply)
            /** original is Multiply;
             *  Modulate returns same result as multiply;
             *  Xor returns inverse values;
             *  SrcOver returns brush behind 'destination' gradient scrim, showing ImageBgRadial over PlayerContentRegular column modifier scrims
             *  DstOver returns scrim without brush
             */
        }
    )
}

/**
 * Displays an image scaled 150% overlaid by [overlay]
 */
@Composable
fun ImageBackground_Bm(
    imageId: Bitmap?,
    imageDescription: String,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageId,
        contentDescription = imageDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
//            .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            },
//        alignment = Alignment.Center,
//        alpha = 0.6f,
//        clipToBounds = true,
    )
}
