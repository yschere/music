package com.example.music.domain.model

import android.util.Log
import com.example.music.data.database.model.Composer
import com.example.music.data.database.model.ComposerWithExtraInfo
import com.example.music.data.util.FLAG

private const val TAG = "ComposerInfo"

/**
 * External data layer representation of a composer.
 *
 * Intent: to represent a Composer for the UI, with the ability to
 * order composers by song count.
 * @property id The composer's unique ID
 * @property name The name of the composer
 * @property songCount The amount of songs by the composer
 */
data class ComposerInfo(
    val id: Long = 0,
    val name: String = "",
    val songCount: Int = 0,
)

/**
 * Transform Composer table entry to ComposerInfo domain model
 */
fun Composer.asExternalModel(): ComposerInfo {
    if (FLAG) Log.i(TAG, "Composer to ComposerInfo external model constructor: \n ${this.id} + ${this.name}")
    return ComposerInfo(
        id = this.id,
        name = this.name,
    )
}

/**
 * Transform Composer table entry with Extra Info (songCount) to ComposerInfo domain model
 */
fun ComposerWithExtraInfo.asExternalModel(): ComposerInfo {
    if (FLAG) Log.i(TAG, "ComposerWithExtraInfo to ComposerInfo external model constructor: \n ${this.composer} + ${this.songCount}")
    return this.composer.asExternalModel().copy(
        songCount = songCount
    )
}