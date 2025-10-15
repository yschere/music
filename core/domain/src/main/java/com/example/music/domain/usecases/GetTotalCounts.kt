package com.example.music.domain.usecases

import android.util.Log
import com.example.music.data.mediaresolver.MediaRepo
import javax.inject.Inject

private const val TAG = "Get Total Counts"

/**
 * Use case to retrieve total row counts for the tables in MediaStore
 * @property mediaRepo Content Resolver Repository for MediaStore
 */
class GetTotalCounts @Inject constructor(
    private val mediaRepo: MediaRepo,
) {
    operator fun invoke(): List<Int> {
        Log.i(TAG,"Get Media Totals from Media Store START")
        return mediaRepo.inspectMediaStore().toList()
    }
}