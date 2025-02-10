package com.example.music.model

/**
 * Model holding a list of albums and a selected album in the collection
 */
data class FilterableAlbumsModel(
    val albums: List<AlbumInfo> = emptyList(),
    val selectedAlbum: AlbumInfo? = null
) {
    val isEmpty = albums.isEmpty() || selectedAlbum == null
}
