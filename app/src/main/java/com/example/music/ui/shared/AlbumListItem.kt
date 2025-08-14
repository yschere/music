package com.example.music.ui.shared

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.music.R
import com.example.music.designsys.component.AlbumImage
import com.example.music.domain.testing.PreviewAlbums
import com.example.music.domain.testing.getArtistData
import com.example.music.domain.model.AlbumInfo
import com.example.music.ui.theme.MusicTheme

@Composable
fun AlbumListItem(
    album: AlbumInfo,
    onClick: (AlbumInfo) -> Unit,
    boxOrRow: Boolean = true,
    //TODO: will want to add navigateToAlbumDetails onClick action at some point as well as declaration and implementation
    //TODO: will want to add onQueueAlbum onClick action if I keep the FAB over each album in box version
    modifier: Modifier = Modifier,
    /*//not passing in artistInfo from ArtistDetailsScreen because want to use album's album artist id info instead
        // onQueueSong: (SongInfo) -> Unit, //don't think onQueueSong will be needed for list item, but maybe a navigate to album details screen btn instead
        // showAlbumImage: Boolean, //this should be true all the time, so is unnecessary for album list item
        // showAlbumTitle: Boolean, //this should be true all the time, so is unnecessary for album list item
        // maybe a song count property? if that can be shown or is wanted? wait no that's part of albumWithExtraInfo,
        // so would need to pass that instead of Album
    */
) {
    Box(modifier = modifier.padding(2.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,
            onClick = { onClick(album) },
            //modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (boxOrRow){
                AlbumListItemBox(
                    album = album,
                    modifier = modifier,
                )
            } else {
                AlbumListItemRow(
                    album = album,
                    modifier = modifier,
                )
            }
        }
    }
}

//TODO: see if it would be better to use Card?
@Composable
private fun AlbumListItemBox(
    album: AlbumInfo,
    modifier: Modifier,
) {
    /* //maybe want the box with constraints thing up here?
     and then within it have base column structure?
     maybe have header and footer with their own separate constraints? that adhere to parent constraint
    */
/*      //////// VERSION 1 /////////
   Column(
       modifier = Modifier.padding(10.dp).size(210.dp, 280.dp)
   ) {
       // Top Part
       AlbumListItemHeader(
           album = album, //might want this to be album with extra info, so that song count can be passed in here
           onClick = {},
           modifier = Modifier.padding(bottom = 8.dp)
           //this one should have the floating action button in it
       )

       // Bottom Part
       AlbumListItemFooter(
           album = album, //might want a way to have album artist name passed in here
           onClick = {},
           //onQueueSong = onQueueSong,
           modifier = Modifier.padding(bottom = 8.dp)
           //this one should have the more options button in it
       )
   }
*/

    //////// VERSION 2 //////// trying with box with constraints and actually setting constraints

    /* //want to make it so that
        min width and min height are passed-in, default values
        max width and max height are dependent on size of phone screen or grid rules
        use of grid rules would need to be made from the calling object
    */

    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background),
            //.size(maxHeaderWidth, maxHeaderHeight)
            //.size(200.dp, 280.dp) //for future context: width 200.dp by height 280.dp had the width of the box go passed half way on the pixel 7, the height was a 3rd of the screen. no padding to the outside of the box so its flat against the window sides
            //more future context: in landscape mode, width 200 by height 280 meant the height covered most of the screen when taking status bar and bottom screen bar into acct
            //.size(boxWithConstraintsScope.maxWidth/3, boxWithConstraintsScope.maxHeight/4)
    ) {
        // Top Part
        AlbumListItemHeader(
            album = album, //might want this to be album with extra info, so that song count can be passed in here
            //onClick = {},
            //modifier = Modifier.padding(bottom = 8.dp)
            //this one should have the floating action button in it
        )

        // Bottom Part
        AlbumListItemFooter(
            album = album, //might want a way to have album artist name passed in here
            //onClick = {},
            //onQueueSong = onQueueSong,
            //modifier = Modifier.padding(bottom = 8.dp)
            //this one should have the more options button in it
        )
    }
}

@Composable
private fun AlbumListItemRow(
    album: AlbumInfo,
    //onClick: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    //uses same structural concept as song list item row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.background),
    ) {
        AlbumListItemImage(
            album = album,
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.small)
        )

        Column(modifier.weight(1f)) {
            Text(
                text = album.title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )
            Row(
                modifier = modifier.padding(horizontal = 10.dp)
            ) {
                Text(
                    text = getArtistData(album.albumArtistId!!).name,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
                Text(
                    text = " • " + album.songCount.toString() + if(album.songCount == 1) " song" else " songs",
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }

        IconButton( //more options button
            //modifier = Modifier.padding(0.dp),
            onClick = {  }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun AlbumListItemFooter(
    album: AlbumInfo,
    //onClick: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    //want a main row that contains two parts, first part is a column of album + album artist, second part is more options btn
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column(modifier = modifier.weight(1f)) {
            Text(
                text = album.title,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)
            )
            Text(
                text = getArtistData(album.albumArtistId!!).name,
                maxLines = 1,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(4.dp)
            )
        }
        IconButton(//more options button
            modifier = Modifier.padding(vertical = 12.dp),
            onClick = {  }, //pretty sure I need this to be context dependent, might pass something within savedStateHandler? within viewModel??
        ) {
            Icon( //more options icon
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.icon_more),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

// Question: see if it would be better to use Card?
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun AlbumListItemHeader(
    album: AlbumInfo,
    //onClick: (AlbumInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    //need a way to set the album image as the "background" of the header, and have the
    // song count as the foreground
    // the FAB / floating action button will be on the bottom right of the box
    BoxWithConstraints (
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
        propagateMinConstraints = true,
    ) {
        //val boxWithConstraintsScope = this
        val maxHeaderWidth = max(150.dp, this.maxWidth/2)
        val maxHeaderHeight = 200.dp//maxHeaderWidth// + 70.dp
        AlbumListItemImage(
            album = album,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(100.dp, 100.dp, maxHeaderWidth, maxHeaderHeight)
                //.height(210.dp)
                //.size(maxHeaderWidth, maxHeaderHeight)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = album.songCount.toString() + if (album.songCount == 1) " song" else " songs",
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(10.dp)
        )
        //floating action button here
//        SmallFloatingActionButton(
//            content = { Icon( Icons.Filled.PlayArrow,"Play Album" )},
//            onClick = {},
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(6.dp),
//            shape = CircleShape,
//            containerColor = MaterialTheme.colorScheme.primaryContainer,
//            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//        )
    }
}

@Composable
private fun AlbumListItemImage(
    album: AlbumInfo,
    modifier: Modifier = Modifier
) {
    AlbumImage(
        //albumImage = album.artwork!!,
        //albumImage = R.drawable.bpicon,
        contentDescription = null,
        modifier = modifier,
    )
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
//@Preview( name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES )
@Composable
private fun AlbumListItem_BOXPreview() {
    MusicTheme {
        AlbumListItem(
            album = PreviewAlbums[0],
            onClick = {},
            modifier = Modifier,
            boxOrRow = true,
        )
    }
}

@Preview( name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO )
@Composable
private fun AlbumListItem_ROWPreview() {
    MusicTheme {
        AlbumListItem(
            album = PreviewAlbums[4],
            onClick = {},
            modifier = Modifier,
            boxOrRow = false,
        )
    }
}