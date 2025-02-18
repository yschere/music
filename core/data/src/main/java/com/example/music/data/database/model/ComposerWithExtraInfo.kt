package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object ComposerWithExtraInfo contains composer object, and the count of songs.
 * @property composer [Composer] data class that represents a Composer
 * @property songCount [Int] the amount of songs by the composer. Used for sorting in the frontend.
 */
class ComposerWithExtraInfo {
    @Embedded
    lateinit var composer: Composer

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = composer
    operator fun component2() = songCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ComposerWithExtraInfo -> {
            composer == other.composer &&
                    songCount == other.songCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(composer, songCount)

}