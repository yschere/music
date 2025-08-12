package com.example.music.domain.model

data class FeaturedLibraryItemsFilterV2 (
    val recentAlbums: List<AlbumInfo> = emptyList(),
    val recentlyAddedSongs: List<SongInfo> = emptyList()
)
