package com.example.music.domain.model

//Song Sort Model.kt
/**
 * A model holding library songs and total songs in library
 */
//data class SongSortModel(
//    val songs: List<SongInfo> = emptyList(),
//    val count: Int = 0
//) {
//    val isEmpty = songs.isEmpty() || count == 0
//}

//Filterable Genres Model.kt
/**
 * Model holding a list of genres and a selected genre in the collection
 */
//data class FilterableGenresModel(
//    val genres: List<GenreInfo> = emptyList(),
//    val selectedGenre: GenreInfo? = null
//) {
//    val isEmpty = genres.isEmpty() || selectedGenre == null
//}


//Album To Song Info.kt
import com.example.music.data.database.model.SongToAlbum

/**
 * Data class for an object that combines a song and its album data.
 */
//data class AlbumToSongInfo (
//    val song: SongInfo,
//    val album: AlbumInfo,
//)
//
//fun SongToAlbum.asAlbumToSongInfo(): AlbumToSongInfo =
//    AlbumToSongInfo(
//        song = song.asExternalModel(),
//        album = album.asExternalModel()
//    )