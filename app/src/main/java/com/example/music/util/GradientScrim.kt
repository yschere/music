package com.example.music.util

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Applies a monochromatic radial gradient scrim in the foreground emanating from any offset.
 */
fun Modifier.radialGradientScrimAnyOffset(color: Color, xOffset: Int, yOffset: Int): Modifier {
    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val largerDimension = max(size.height, size.width)
            val smallerDimension = min(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(x = size.width / xOffset, y = size.height / yOffset),
                colors = listOf(color, Color.Transparent),
                radius = smallerDimension * 1.5f,
                colorStops = getColorStops(0) // forcing else branch
            )
        }
    }
    return this.background(radialGradient)
}

/**
 * Applies a multicolor radial gradient scrim in the foreground emanating from any offset.
 */
fun Modifier.radialGradientScrimAnyColorsOffset(colors: List<Color>, xOffset: Int, yOffset: Int): Modifier {
    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val largerDimension = max(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(x = size.width / xOffset, y = size.height / yOffset),
                colors = colors + Color.Transparent,
                radius = (largerDimension / (colors.size + 1)) * colors.size, //largerDimension / 2
                colorStops = getColorStops(colors.size + 1) // adding Transparent color means +1 to og size
            )
        }
    }
    return this.background(radialGradient)
}

/**
 * Applies a monochromatic radial gradient scrim in the foreground emanating from the top
 * center quarter of the element.
 */
fun Modifier.radialGradientScrimCentered(color: Color): Modifier {
    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val largerDimension = max(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(y = size.height / 4),
                colors = listOf(color, Color.Transparent),
                radius = largerDimension / 3,
                colorStops = getColorStops(0) // forcing else branch
            )
        }
    }
    return this.background(radialGradient)
}

/**
 * Applies a multicolor radial gradient scrim in the foreground emanating from the bottom
 * right of the element.
 */
fun Modifier.radialGradientScrimBottomRight(colors: List<Color>,): Modifier {
    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val largerDimension = max(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(x = size.width, y = size.height),
                colors = colors + Color.Transparent,
                radius = largerDimension * 1.75f,
                colorStops = getColorStops(colors.size + 1)
            )
        }
    }
    return this.background(radialGradient)
}

/**
 * Draws a vertical gradient scrim in the foreground.
 *
 * @param color The color of the gradient scrim.
 * @param startYPercentage The start y value, in percentage of the layout's height (0f to 1f)
 * @param endYPercentage The end y value, in percentage of the layout's height (0f to 1f). This
 * value can be smaller than [startYPercentage]. If that is the case, then the gradient direction
 * will reverse (decaying downwards, instead of decaying upwards).
 * @param decay The exponential decay to apply to the gradient. Defaults to `1.0f` which is
 * a linear gradient.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in
 * the higher visual quality at the cost of draw performance. Defaults to `16`.
 */
fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16
) = this then VerticalGradientElement(color, startYPercentage, endYPercentage, decay, numStops)

private data class VerticalGradientElement(
    var color: Color,
    var startYPercentage: Float = 0f,
    var endYPercentage: Float = 1f,
    var decay: Float = 1.0f,
    var numStops: Int = 16
) : ModifierNodeElement<VerticalGradientModifier>() {
    fun createOnDraw(): DrawScope.() -> Unit {
        val colors = if (decay != 1f) {
            // If we have a non-linear decay, we need to create the color gradient steps
            // manually
            val baseAlpha = color.alpha
            List(numStops) { i ->
                val x = i * 1f / (numStops - 1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // If we have a linear decay, we just create a simple list of start + end colors
            listOf(color.copy(alpha = 0f), color)
        }

        val brush =
            // Reverse the gradient if decaying downwards
            Brush.verticalGradient(
                colors = if (startYPercentage < endYPercentage) colors else colors.reversed(),
            )

        return {
            val topLeft = Offset(0f, size.height * min(startYPercentage, endYPercentage))
            val bottomRight =
                Offset(size.width, size.height * max(startYPercentage, endYPercentage))

            drawRect(
                topLeft = topLeft,
                size = Rect(topLeft, bottomRight).size,
                brush = brush
            )
        }
    }

    override fun create() = VerticalGradientModifier(createOnDraw())

    override fun update(node: VerticalGradientModifier) {
        node.onDraw = createOnDraw()
    }

    /**
     * Allow this custom modifier to be inspected in the layout inspector
     **/
    override fun InspectorInfo.inspectableProperties() {
        name = "verticalGradientScrim"
        properties["color"] = color
        properties["startYPercentage"] = startYPercentage
        properties["endYPercentage"] = endYPercentage
        properties["decay"] = decay
        properties["numStops"] = numStops
    }
}

private class VerticalGradientModifier(
    var onDraw: DrawScope.() -> Unit
) : Modifier.Node(), DrawModifierNode {

    override fun ContentDrawScope.draw() {
        onDraw()
        drawContent()
    }
}

/**
 * Defines the possible color stops for radial gradient scrims.
 */
private fun getColorStops(size: Int): List<Float> =
    when (size) {
        1 -> listOf(0.5f)
        2 -> listOf(0.3f, 0.7f)
        3 -> listOf(0.25f, 0.5f, 0.75f)
        4 -> listOf(0.2f, 0.4f, 0.6f, 0.8f)
        5 -> listOf(0.20f, 0.35f, 0.50f, 0.65f, 0.80f)
        else -> listOf(0f, 0.9f)
    }
