package com.example.music.domain.usecases

import com.example.music.domain.util.MediaRepo
import javax.inject.Inject
import com.example.music.domain.util.domainLogger

class GetTotalCountsV2 @Inject constructor(
    private val resolver: MediaRepo,
) {
    suspend operator fun invoke(): List<Int> {//List<Pair<String,Int>> {
        domainLogger.info { "Get Total Counts V2 - start" }
        // get count of songs
        // get count of artists
        // get count of albums
        // get count of genres
        // use this as excuse to explore media store tables

        return resolver.inspectMediaStore().toList()

        //return listOf(1,2,3,4)
    }
}