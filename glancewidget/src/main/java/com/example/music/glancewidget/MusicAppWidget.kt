package com.example.music.glancewidget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.music.designsys.theme.blueDarkSet
import com.example.music.designsys.theme.blueLightSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal const val TAG = "MusicAppWidget"

/**
 * Implementation of App Widget functionality.
 */
class MusicAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MusicAppWidget()
}

data class MusicAppWidgetViewState(
    val songTitle: String,
    val artistName: String,
    val isPlaying: Boolean,
    val albumArtUri: String,
    val useDynamicColor: Boolean
)

private object Sizes {
    val minWidth = 140.dp
    val smallBucketCutoffWidth = 250.dp // anything from minWidth to this will have no title

    val imageNormal = 80.dp
    val imageCondensed = 60.dp
}

/**
 * Represents the different sizes for a widget.
 *
 * This enum is used to categorize the size of a widget based on its width and height.
 * It provides three distinct categories:
 *   - INVALID: A small widget whose width is less than midWidth (140.dp).
 *   - NARROW: A widget whose width is greater or equal to minWidth (140.dp), and less than or equal to smallBucketCutoffWidth (250.dp).
 *   - NORMAL: A larger widget whose width is greater than smallBucketCutoffWidth (250.dp).
 */
private enum class SizeBucket { Invalid, Narrow, Normal }

@Composable
private fun calculateSizeBucket(): SizeBucket {
    val size: DpSize = LocalSize.current
    val width = size.width

    return when {
        width < Sizes.minWidth -> SizeBucket.Invalid
        width <= Sizes.smallBucketCutoffWidth -> SizeBucket.Narrow
        else -> SizeBucket.Normal
    }
}

class MusicAppWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val testState = MusicAppWidgetViewState(
            songTitle = "ALONES",
            artistName = "Aqua Timez",
            isPlaying = false,
            albumArtUri = "https://static.libsyn.com/p/assets/9/f/f/3/9ff3cb5dc6cfb3e2e5bbc093207a2619/NIA000_PodcastThumbnail.png",
            useDynamicColor = false
        )

        provideContent {
            //val state by remember { PlaybackController.observe(context) }.collectAsState(NowPlaying.EMPTY)
            val sizeBucket = calculateSizeBucket()
            val playPauseIcon = if (testState.isPlaying) PlayPauseIcon.Pause else PlayPauseIcon.Play
            val artUri = Uri.parse(testState.albumArtUri)
            val modifier = GlanceModifier
                .background(
                    ImageProvider(R.drawable.bpicon_w),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.surface)
                )
                .padding(8.dp)
                .appWidgetBackground()
                .launchApp()
                .fillMaxSize()

            GlanceTheme(
                colors = ColorProviders(
                    light = lightMusicColors, //updated to use light scheme from designsys
                    dark = darkMusicColors //updated to use dark scheme from designsys
                )
            ) {
                when (sizeBucket) {
                    SizeBucket.Invalid -> WidgetUiInvalidSize()
                    SizeBucket.Narrow -> WidgetUiNarrow(
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                        modifier = modifier,
                    )

                    SizeBucket.Normal -> WidgetUiNormal(
                        song = testState.songTitle,
                        artist = testState.artistName,
                        imageUri = artUri,
                        playPauseIcon = playPauseIcon,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetUiNormal(
    song: String,
    artist: String,
    imageUri: Uri,
    playPauseIcon: PlayPauseIcon,
    modifier: GlanceModifier,
) {
    Scaffold(titleBar = {}) { //title bar will be optional starting in glance 1.1.0-beta3
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            AlbumArt(imageUri, GlanceModifier.size(Sizes.imageNormal))
            SongText(song, artist, modifier = GlanceModifier.padding(16.dp).defaultWeight())
            PlayPauseButton(playPauseIcon, {})
        }
    }
}

@Composable
private fun WidgetUiNarrow(
    imageUri: Uri,
    playPauseIcon: PlayPauseIcon,
    modifier: GlanceModifier,
) {
    Scaffold(titleBar = {}) { //title bar will be optional starting in glance 1.1.0-beta3
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            AlbumArt(imageUri, GlanceModifier.size(Sizes.imageCondensed))
            Spacer(GlanceModifier.defaultWeight())
            PlayPauseButton(playPauseIcon, {})
        }
    }
}

@Composable
private fun WidgetUiInvalidSize() {
    Box(modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.background)) {
        Text("invalid size")
    }
}

@Composable
private fun AlbumArt(
    imageUri: Uri,
    modifier: GlanceModifier = GlanceModifier
) {
    /*// commented out until able to understand glance with compose
    // support for this would be within designsys
    AlbumImage_Widget(
        0,
        null,
        modifier.cornerRadius(12.dp),
        ContentScale.FillBounds,
    )*/
    Image(
        provider = ImageProvider(R.drawable.bpicon_w),
        //painter = painterResource(R.drawable.bpicon_w),
        //painter = painterResource(albumImage), //trying to use drawable from res folder
        //painter = imageLoader, //uses coil imageLoader
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = modifier.cornerRadius(12.dp),
    )
    //WidgetAsyncImage(uri = imageUri, contentDescription = null, modifier = modifier)
}

@Composable
fun SongText(song: String, artist: String, modifier: GlanceModifier = GlanceModifier) {
    val fgColor = GlanceTheme.colors.onPrimaryContainer
    Column(modifier) {
        Text(
            text = song,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = fgColor),
            maxLines = 2,
        )
        Text(
            text = artist,
            style = TextStyle(fontSize = 14.sp, color = fgColor),
            maxLines = 2,
        )
    }
}

@Composable
private fun PlayPauseButton(state: PlayPauseIcon, onClick: () -> Unit) {
    val (iconRes: Int, description: Int) = when (state) {
        PlayPauseIcon.Play -> R.drawable.ic_round_play_filled to R.string.content_description_play
        PlayPauseIcon.Pause -> R.drawable.ic_pause_rounded_filled to R.string.content_description_pause
    }

    val provider = ImageProvider(iconRes)
    val contentDescription = LocalContext.current.getString(description)

    SquareIconButton(
        provider,
        contentDescription = contentDescription,
        onClick = onClick
    )
}

enum class PlayPauseIcon { Play, Pause }

/**
 * Uses Coil to load images.
 */
@Composable
private fun WidgetAsyncImage(
    uri: Uri,
    contentDescription: String?,
    modifier: GlanceModifier = GlanceModifier
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /*LaunchedEffect(key1 = uri) {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .size(200, 200)
            .target { data: Drawable ->
                bitmap = (data as BitmapDrawable).bitmap
            }
            .build()

        scope.launch(Dispatchers.IO) {
            val result = ImageLoader(context).execute(request)
            if (result is ErrorResult) {
                val t = result.throwable
                Log.e(TAG, "Image request error:", t)
            }
        }
    }*/

    /*bitmap?.let { bitmap ->
        Image(
            provider = ImageProvider(bitmap),
            contentDescription = contentDescription,
            contentScale = ContentScale.FillBounds,
            modifier = modifier.cornerRadius(12.dp)
            // TODO: confirm radius with design
        )
    }*/
}

/**
 * Creates a new `ColorProvider` with a modified alpha value.
 *
 * @param context The context needed to resolve the color.
 * @param alpha The new alpha value (0f-1f); -1f means use the original alpha.
 * @return A new `ColorProvider` or the original if alpha is -1f.
 */
@SuppressLint("RestrictedApi")
fun ColorProvider.copy(context: Context, alpha: Float = -1f) = if (alpha == -1f) this else
    ColorProvider(getColor(context).copy(alpha))

@Composable
internal fun GlanceModifier.launchApp(): GlanceModifier {
    val context = LocalContext.current
    return clickable {
        Log.d("Util", "Universal: ${context.packageName}")
        context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName))
    }
}