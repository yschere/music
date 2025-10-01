package com.example.music.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp

/**
 * An item that spans the entire width of a lazy grid.
 * @param key The key for the grid item.
 * @param contentType The type of content for the grid item.
 * @param content The composable content for the grid item.
 */
fun LazyGridScope.fullWidthItem(
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyGridItemScope.() -> Unit
) = item(
    span = { GridItemSpan(this.maxLineSpan) },
    key = key,
    contentType = contentType,
    content = content
)

/**
 * Composable for drawing a sticky header for an item
 * @param item The item to display in the header
 */
@Composable
fun StickyHeader( item: String ) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = item,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        HorizontalDivider(
            modifier = Modifier.padding(1.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * A sticky header implementation that respects the top padding of the content.
 * This should be removed when an official solution is provided.
 * Currently, the only issue is that the sticky layout and the next item overlap before moving,
 * while the sticky header should start moving when the next item is about to become sticky.
 *
 * @param state The state of the LazyGrid.
 * @param key The key for the sticky header item.
 * @param contentType The type of content for the sticky header.
 * @param content The composable content for the sticky header.
 */
fun LazyGridScope.stickyHeader(
    state: LazyGridState,
    key: Any? = null,
    contentType: Any? = null,
    content: @Composable LazyGridItemScope.() -> Unit
) {
//    stickyHeader(
//        key = key,
//        contentType = contentType,
//        content = {
    fullWidthItem {
        Layout(content = { content() }) { measurables, constraints ->
            val placeable = measurables[0].measure(constraints)
            val width = constraints.constrainWidth(placeable.width)
            val height = constraints.constrainHeight(placeable.height)
            layout(width, height) {
                val posY = coordinates?.positionInParent()?.y?.toInt() ?: 0
                val paddingTop = state.layoutInfo.beforeContentPadding
                var top = (paddingTop - posY).coerceIn(0, paddingTop)
                placeable.placeRelativeWithLayer(0, top)
            }
        }
    }
//        }
//    )
}


class BottomBorderLineShape(size: Size): Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val (w,h) = size
        //val radiusPx = with(density) { radius.toPx()}
        val path = Path().apply {
            moveTo(0f,h)
            lineTo(w,h)
        }

        return Outline.Generic(path)
    }
}