package com.example.music.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

/**
 * Class object ComposerWithExtraInfo contains composer object, the count of songs,
 * and the max value of date_last_played from songs by composer.
 */
class ComposerWithExtraInfo {
    @Embedded
    lateinit var composer: Composer

    //this is the MAX("date_last_played") value from songs by composer,
    // should default to null unless there is at least one song that
    // has this value set and is the MAX value if multiple are set
    @ColumnInfo(name = "date_last_played")
    var dateLastPlayed: OffsetDateTime? = null

    //this is the song count within library
    @ColumnInfo(name = "song_count")
    var songCount: Int = 0

    /**
     * Allow consumers to destruct this class
     */
    operator fun component1() = composer
    operator fun component2() = dateLastPlayed
    operator fun component3() = songCount

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ComposerWithExtraInfo -> {
            composer == other.composer &&
                    dateLastPlayed == other.dateLastPlayed &&
                    songCount == other.songCount
        }
        else -> false
    }
    override fun hashCode(): Int = Objects.hash(composer, dateLastPlayed, songCount)

}