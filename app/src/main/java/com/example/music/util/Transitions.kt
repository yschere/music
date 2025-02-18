package com.example.music.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.ui.theme.MusicTheme

fun scaleIntoContainer(
//    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = 1.1f//if (direction == ScaleTransitionDirection.OUTWARDS) 0.9f else 1.1f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(220, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
}

fun scaleOutOfContainer(
//    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = 0.9f// if (direction == ScaleTransitionDirection.INWARDS) 0.9f else 1.1f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 220,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}

@Composable
fun ScalingAnimatedVisibility(
    //visible: Boolean = true,
    enter: EnterTransition = scaleIntoContainer(),// aleIn,
//    =
//        slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
//            // Offsets the content by 1/3 of its width to the left, and slide towards right
//            // Overwrites the default animation with tween for this slide animation.
//            -fullWidth / 3
//        } +
//        fadeIn(
//        // Overwrites the default animation with tween
//        animationSpec = tween(durationMillis = 200)
//        ),
    exit: ExitTransition = scaleOutOfContainer(),//,
//    =
//        slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
//            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
//            200
//        } + fadeOut()
) {
    var visible by remember { mutableStateOf(true) }
    AnimatedVisibility(
        visible = visible,
        enter = enter,
        exit = exit,
    ) {
        Box(Modifier.fillMaxWidth().requiredHeight(200.dp)) {}
    }
    // Content that needs to appear/disappear goes here:

}

@Preview
@Composable
fun PreviewVis() {
    MusicTheme {
        ScalingAnimatedVisibility()
    }
}