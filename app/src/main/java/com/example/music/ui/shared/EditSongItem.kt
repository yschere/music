package com.example.music.ui.shared

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.domain.model.SongInfo
import com.example.music.domain.testing.PreviewSongs


import com.example.music.ui.theme.MusicTheme

/** Changelog:
 *
 * 4/2/2025 - Removing PlayerSong as UI model supplement. SongInfo domain model
 * has been adjusted to support UI with the string values of the foreign key
 * ids and remaining extra info that was not in PlayerSong.
 */

@Composable
fun EditSongScreen(
    //windowSizeClass: WindowSizeClass,
    //displayFeatures: List<DisplayFeature>,
    song: SongInfo, // this should likely change to be SongInfo or Song
    navigateBack: () -> Unit,
    //viewModel: SettingsViewModel = hiltViewModel(),
) {
    //val coroutineScope = rememberCoroutineScope()

    ScreenBackground(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            containerColor = Color.Transparent,
            contentColor = contentColorFor(MaterialTheme.colorScheme.background) //selects the appropriate color to be the content color for the container using background color
            //contentColor = MaterialTheme.colorScheme.inverseSurface //or onPrimaryContainer
        ) { contentPadding ->
            Column {
                // top bar
                EditSongTopAppBar(
                    navigateBack = navigateBack,
                )

                // content
                EditSongContent(
                    //windowSizeClass = windowSizeClass,
                    //displayFeatures = displayFeatures,
                    song = song,
                    modifier = Modifier.padding(contentPadding),
                )
            }
        }

    }
}

@Composable
fun EditSongTopAppBar(
    navigateBack: () -> Unit,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        //back button
        IconButton(onClick = navigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.icon_back_nav),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        //right align objects after this space
        Spacer(Modifier.weight(1f))

        // would probably need a save btn and/or redo btn up here

        // more options button
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
fun EditSongContent(
    song: SongInfo,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = song.title,
        onValueChange = {},
        singleLine = true,
        shape = shapes.large,
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            //unfocusedIndicatorColor = Color.Green, //border line
            cursorColor = MaterialTheme.colorScheme.primary,
            //unfocusedTextColor = MaterialTheme.colorScheme.onBackground, //seems like default color
            //unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        label = { Text("Title") },
    )

    OutlinedTextField(
        value = song.artistName,
        onValueChange = {},
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        label = { Text("Artist") },
    )

    OutlinedTextField(
        value = song.albumTitle,
        onValueChange = {},
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        label = { Text("Album") },
    )

    OutlinedTextField(
        value = song.trackNumber.toString(),
        onValueChange = {},
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        label = { Text("Track Number") },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewEditSongScreen(){
    MusicTheme {
        EditSongScreen(
            song = PreviewSongs[0],
            navigateBack = {}
        )
    }
}