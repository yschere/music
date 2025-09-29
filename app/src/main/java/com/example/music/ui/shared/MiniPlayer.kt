package com.example.music.ui.shared

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.music.R
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.MINI_PLAYER_BUTTON_SIZE
import com.example.music.designsys.theme.MINI_PLAYER_HEIGHT
import com.example.music.designsys.theme.SMALL_PADDING
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.getSongData
import com.example.music.ui.theme.MusicTheme
import com.example.music.ui.tooling.CompDarkPreview
import com.example.music.ui.tooling.CompLightPreview
import com.example.music.util.horizontalGradientScrim
import com.example.music.util.radialMultiGradientScrimAnyOffset
import com.example.music.util.radialMultiGradientScrimBottomRight

private const val TAG = "Mini Player"

@Composable
fun MiniPlayer(
    song: SongInfo,
    isPlaying: Boolean = true,
    navigateToPlayer: () -> Unit,
    onPlay: () -> Unit = {},
    onPause: () -> Unit = {},
    playButtonSize: Dp = MINI_PLAYER_BUTTON_SIZE,
    modifier: Modifier = Modifier,
) {
    Log.i(TAG, "Song: ${song.title}\n" +
        "has Artist?: ${song.artistName}\n" +
        "has artwork?: ${song.artworkUri}")

    val playButtonModifier = Modifier
        .size(playButtonSize)
        .background(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        )
        .semantics { role = Role.Button }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.fillMaxWidth().height(MINI_PLAYER_HEIGHT),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            onClick = { navigateToPlayer() },
            modifier = modifier.fillMaxWidth()
                //.horizontalGradientScrim(MaterialTheme.colorScheme.primaryContainer)
                .radialMultiGradientScrimAnyOffset(
                    colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer),
                    xOffset = 1f,
                    yOffset = 0.5f,
                ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(CONTENT_PADDING)
            ) {
                HeaderImage(song.artworkUri, song.title)
                Column(Modifier.padding(CONTENT_PADDING).weight(1f)) {
                    Text(
                        text = song.title,
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = song.setSubtitle(),
                        maxLines = 1,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (isPlaying) {
                    //determined that the current state is playing (isPlaying is true)
                    Image(
                        imageVector = Icons.Filled.Pause,
                        contentDescription = stringResource(R.string.pb_pause),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                        modifier = playButtonModifier
                            .padding(SMALL_PADDING)
                            .clip(CircleShape)
                            .clickable { onPause() }
                    )
                }
                else {
                    //determined that the current state is paused (isPlaying is false)
                    Image(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = stringResource(R.string.pb_play),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                        modifier = playButtonModifier
                            .padding(SMALL_PADDING)
                            .clip(CircleShape)
                            .clickable { onPlay() }
                    )
                }
            }
        }
    }
}

/*@Composable
fun MiniPlayerExpanded(
    song: SongInfo,
    hasNext: Boolean = false,
    isPlaying: Boolean = true,
    isShuffled: Boolean,
    repeatState: RepeatType,
    navigateToPlayer: () -> Unit,
    //navigateToQueue: () -> Unit = {},
    miniPlayerExpandedControlActions: MiniPlayerExpandedControlActions,
    modifier: Modifier = Modifier,
    primaryButtonSize: Dp = 64.dp,
    sideButtonSize: Dp = 48.dp,
) {
    Log.i(TAG, "Song: ${song.title}\n" +
        "has Artist?: ${song.artistName}\n" +
        "has artwork?: ${song.artworkUri}")

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(topStartPercent = 20, topEndPercent = 20),
            color = MaterialTheme.colorScheme.inversePrimary,
            contentColor = MaterialTheme.colorScheme.onSurface,
            onClick = { navigateToPlayer() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(vertical = 12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    HeaderImage(song.artworkUri, song.title)
                    Column(Modifier.padding(start = 8.dp).weight(1f)) {
                        Text(
                            text = song.title,
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.basicMarquee()
                        )
                        Text(
                            text = song.setSubtitle(),
                            maxLines = 1,
                            minLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    /* // leaving out till queue UI components are decided
                    IconButton(onClick = navigateToQueue) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = stringResource(R.string.icon_queue),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(sideButtonSize)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .semantics { role = Role.Button }
                                .padding(4.dp)
                                .clickable { navigateToQueue() }
                        )
                    }*/
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    PlayerButtons(
                        hasNext = hasNext,
                        isPlaying = isPlaying,
                        isShuffled = isShuffled,
                        repeatState = repeatState,
                        onPlay = miniPlayerExpandedControlActions.onPlay,
                        onPause = miniPlayerExpandedControlActions.onPause,
                        onNext = miniPlayerExpandedControlActions.onNext,
                        onPrevious = miniPlayerExpandedControlActions.onPrevious,
                        onShuffle = miniPlayerExpandedControlActions.onShuffle,
                        onRepeat = miniPlayerExpandedControlActions.onRepeat,
                        modifier = Modifier,
                        primaryButtonSize = primaryButtonSize,
                        sideButtonSize = sideButtonSize,
                    )
                }
            }
        }
    }
}*/

@CompLightPreview
@CompDarkPreview
@Composable
fun PreviewMiniPlayer() {
    MusicTheme {
        MiniPlayer(
            song = getSongData(6535),
            isPlaying = true,
            navigateToPlayer = {},
            onPlay = {},
            onPause = {},
        )
    }
}

/*@CompLightPreview
@Composable
fun PreviewMiniPlayerExpanded() {
    MusicTheme {
        MiniPlayerExpanded(
            song = getSongData(6535),
            isPlaying = true,
            isShuffled = false,
            repeatState = RepeatType.ON,
            miniPlayerExpandedControlActions = MiniPlayerExpandedControlActions(
                onPlay = {},
                onPause = {},
                onShuffle = {},
                onRepeat = {},
                onNext = {},
                onPrevious = {},
            ),
            navigateToPlayer = {},
            //navigateToQueue = {},
            modifier = Modifier,
        )
    }
}*/
