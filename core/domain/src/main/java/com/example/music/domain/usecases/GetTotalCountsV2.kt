package com.example.music.domain.usecases

import android.util.Log
import com.example.music.domain.util.MediaRepo
import javax.inject.Inject
import com.example.music.domain.util.domainLogger

private const val TAG = "Get Total Counts V2"
class GetTotalCountsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    operator fun invoke(): List<Int> {
        Log.i(TAG,"Start")
        // get count of songs
        // get count of artists
        // get count of albums
        // get count of genres
        // use this as excuse to explore media store tables
        return resolver.inspectMediaStore().toList()
    }
}