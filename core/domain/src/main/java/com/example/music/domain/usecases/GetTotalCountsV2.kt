package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Total Counts V2"

class GetTotalCountsV2 @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(): List<Int> {
        Log.i(TAG,"Start")
        // get count of songs
        // get count of artists
        // get count of albums
        // get count of genres
        // use this as excuse to explore media store tables
        return mediaRepo.inspectMediaStore().toList()
    }
}