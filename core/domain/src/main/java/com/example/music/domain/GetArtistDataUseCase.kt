package com.example.music.domain

import com.example.music.data.database.model.Album
import com.example.music.data.repository.AlbumRepo
import com.example.music.model.AlbumInfo
import com.example.music.model.SongInfo
import com.example.music.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetArtistDataUseCase @Inject constructor(
    private val albumRepo: AlbumRepo,
){
//    suspend operator fun invoke(song: SongInfo): AlbumInfo {
//        return albumRepo.getAlbumById(song.id).single().asExternalModel()
//    }//would like this to return AlbumInfo, but might very likely need to return Flow<AlbumInfo>

    operator fun invoke(song: SongInfo): Flow<AlbumInfo> {
        return albumRepo.getAlbumById(song.id).transform<Album,AlbumInfo> {  }
    }

}
