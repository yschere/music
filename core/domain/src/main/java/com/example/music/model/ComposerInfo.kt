package com.example.music.model

import com.example.music.data.database.model.Composer
import com.example.music.data.database.model.ComposerWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of a composer.
 * Intent: to represent a Composer for the UI, with the ability to
 * order composers based on song's date last played and song count.
 */
data class ComposerInfo(
    val id: Long = 0,
    val name: String = "",
    val songCount: Int = 0,
)

fun Composer.asExternalModel(): ComposerInfo =
    ComposerInfo(
        id = this.id,
        name = this.name,
    )

fun ComposerWithExtraInfo.asExternalModel(): ComposerInfo =
    this.composer.asExternalModel().copy(
        songCount = songCount
    )
