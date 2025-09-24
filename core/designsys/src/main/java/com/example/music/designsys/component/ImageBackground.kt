package com.example.music.designsys.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Sends an image request and returns the image scaled 150% overlaid by [overlay] with a blur modifier
 * @param imageId Nullable [String] : represents the image's unique identifier
 * @param imageDescription Nullable [String] : describes the image
 * @param overlay [DrawScope] -> [Unit] : defines the filter overlay to draw onto the image
 * @param modifier [Modifier] : defines the modifiers to apply to image
 */
@Composable
internal fun ImageBackground(
    imageId: Any?,
    imageDescription: String? = null,
    overlay: DrawScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageId,
        contentDescription = imageDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    overlay()
                }
            }
            .blur(
                radiusX = 5.dp,
                radiusY = 5.dp,
                edgeTreatment = BlurredEdgeTreatment.Rectangle
            ),
    )
}


/***********************************************************************************************
 *
 * **********  IMAGE BACKGROUND USING URL STRING ***********
 *
 **********************************************************************************************/

/**
 * Draws an image background with a color filter.
 * @param imageId represents the image's url
 * @param imageDescription describes the image
 * @param color defines the color to apply to filter
 * @param modifier defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundColorFilter(
    imageId: String?,
    imageDescription: String? = null,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

/**
 * Draws an image background with a multicolor radial gradient filter centered on the bottom left.
 * @param imageId Nullable [String] : represents the image's url
 * @param imageDescription Nullable [String] : describes the image
 * @param colors [List] of [Color] : defines the colors to apply to filter
 * @param modifier [Modifier] : defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundRadialGradientFilter(
    imageId: String?,
    imageDescription: String? = null,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset((0f), (size.height)),
                radius = size.width * 2.5f
            )
            drawRect(brush, blendMode = BlendMode.Modulate)
        }
    )
}


/***********************************************************************************************
 *
 * **********  IMAGE BACKGROUND USING BITMAP ***********
 *
 **********************************************************************************************/

/**
 * Draws an image background with a color filter.
 * @param imageId Nullable [String] : represents the image's bitmap
 * @param imageDescription Nullable [String] : describes the image
 * @param color [Color] : defines the color to apply to filter
 * @param modifier [Modifier] : defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundColorFilter_Bm(
    imageId: Bitmap?,
    imageDescription: String? = null,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

/**
 * Draws an image background with a multicolor radial gradient filter centered on the bottom left.
 * @param imageId Nullable [String] : represents the image's bitmap
 * @param imageDescription Nullable [String] : describes the image
 * @param colors [List] of [Color] : defines the colors to apply to filter
 * @param modifier [Modifier] : defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundRadialGradientFilter_Bm(
    imageId: Bitmap?,
    imageDescription: String? = null,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset((0F), (size.height)),
                radius = size.width * 3F
            )
            drawRect(brush, blendMode = BlendMode.Modulate)
        }
    )
}


/***********************************************************************************************
 *
 * **********  IMAGE BACKGROUND USING URI ***********
 *
 **********************************************************************************************/

/**
 * Draws an image background with a color filter.
 * @param imageId Nullable [String] : represents the image's Uri
 * @param imageDescription Nullable [String] : describes the image
 * @param color [Color] : defines the color to apply to filter
 * @param modifier [Modifier] : defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundColorFilter_Uri(
    imageId: Uri?,
    imageDescription: String? = null,
    color: Color = Color.Transparent,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            drawRect(color)
        }
    )
}

/**
 * Draws an image background with a multicolor radial gradient filter centered on the bottom left.
 * @param imageId Nullable [String] : represents the image's Uri
 * @param imageDescription Nullable [String] : describes the image
 * @param colors [List] of [Color] : defines the colors to apply to filter
 * @param modifier [Modifier] : defines any modifiers to apply to image
 */
@Composable
fun ImageBackgroundRadialGradientFilter_Uri(
    imageId: Uri?,
    imageDescription: String? = null,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    ImageBackground(
        imageId = imageId,
        imageDescription = imageDescription,
        modifier = modifier,
        overlay = {
            val brush = Brush.radialGradient(
                colors = colors,
                center = Offset((0F), (size.height)),
                radius = size.width * 3F
            )
            drawRect(brush, blendMode = BlendMode.Modulate)
        }
    )
}
