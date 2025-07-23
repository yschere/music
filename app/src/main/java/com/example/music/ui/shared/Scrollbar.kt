package com.example.music.ui.shared

import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Composable Scrollbar for LazyVerticalGrid
 *
 * To call gridVerticalScrollbar, use Modifier.gridVerticalScrollbar in conjunction with a
 * variable storing rememberLazyGridState. Set the grid to scroll on to the state variable,
 * and set the gridVerticalScrollbar's state to the same state variable.
 *
 * Sample:
 *
 *     val gridState = rememberLazyGridState()
 *     LazyVerticalGrid(
 *         state = gridState,
 *         columns = GridCells.Adaptive(362.dp),
 *         modifier = modifier
 *             .fillMaxSize()
 *             .padding(horizontal = 12.dp)
 *             .gridVerticalScrollbar(
 *                 state = gridState,
 *                 backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
 *                 color = MaterialTheme.colorScheme.secondary,
 *      )
 *
 *  @param state [LazyGridState] lazy grid's state object
 *  @param width [Float] width of the scrollbar
 *  @param backgroundColor [Color] of the scrollbar background
 *  @param color [Color] of the scrollbar indicator
 */
@Composable
fun Modifier.gridVerticalScrollbar(
    state: LazyGridState,
    width: Float = 12f,
    backgroundColor: Color = Color.Black,
    color: Color = Color.LightGray,
): Modifier {
    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index

        if (firstVisibleElementIndex != null) {
            // get count of all the items in the grid
            val scrollableItems = state.layoutInfo.totalItemsCount - state.layoutInfo.visibleItemsInfo.size
            // get the height of the indicator from the
            val scrollBarHeight = this.size.height / scrollableItems
            val offsetY = ((this.size.height - scrollBarHeight) * firstVisibleElementIndex)

            // scrollbar background length
            drawRect(
                color = backgroundColor,
                topLeft = Offset(x = this.size.width, y = 0f),
                alpha = 1f,
            )

            // scrollbar indicator
            drawRect(
                color = color,
                topLeft = Offset(x = this.size.width, y = offsetY),
                size = Size(width, scrollBarHeight),
                alpha = 1f,
            )
        }
    }
}

/**
 * Composable Scrollbar for LazyList
 */
@Composable
fun Modifier.listVerticalScrollbar(
    state: LazyListState,
    width: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.tertiary,
): Modifier {

    val targetAlpha = if (state.isScrollInProgress) .7f else 0f
    val duration = if (state.isScrollInProgress) 150 else 1000

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    val firstIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow)
    )

    val lastIndex by animateFloatAsState(
        targetValue = state.layoutInfo.visibleItemsInfo.lastOrNull()?.index?.toFloat() ?: 0f,
        animationSpec = spring(stiffness = StiffnessMediumLow)
    )

    return drawWithContent {
        drawContent()

        val itemsCount = state.layoutInfo.totalItemsCount

        if (itemsCount > 0 && alpha > 0f) {
            val scrollbarTop = firstIndex / itemsCount * size.height
            val scrollBottom = (lastIndex + 1f) / itemsCount * size.height
            val scrollbarHeight = scrollBottom - scrollbarTop
            drawRect(
                color = color,
                topLeft = Offset(size.width - width.toPx(), scrollbarTop),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}
