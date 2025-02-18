/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.domain.player

import com.example.music.player.SongPlayerImpl
import com.example.music.player.model.PlayerSong
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class SongPlayerImplTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockSongPlayer = SongPlayerImpl(testDispatcher)
    private val testSongs = listOf(
        PlayerSong(
            id = 1023,
            title = "88",
            artistName = "LM.C",
            albumTitle = "88 / ...With Vampire - Single",
            duration = Duration.parse("PT4M"),//5S"),
        ),
        PlayerSong(
            id = 17,
            title = "Ignorance",
            artistName = "Paramore",
            albumTitle = "Brand New Eyes",
            duration = Duration.parse("PT4M"),//26S"),
        ),
        PlayerSong(
            id = 6535,
            title = "Musique pour la Tristesse de Xion",
            artistName = "Yoko Shimomura",
            albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
            duration = Duration.parse("PT6M"),//22S"),
        ),
    )

    @Test
    fun whenPlay_incrementsByPlaySpeed() = runTest(testDispatcher) {
        val playSpeed = Duration.ofSeconds(2)
        val currSong = PlayerSong(
            id = 6535,
            title = "Musique pour la Tristesse de Xion",
            artistName = "Yoko Shimomura",
            albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
            duration = Duration.parse("PT6M"),//22S"),
        )
        mockSongPlayer.currentSong = currSong
        mockSongPlayer.playerSpeed = playSpeed

        mockSongPlayer.play()
        advanceTimeBy(playSpeed.toMillis() + 300)

        assertEquals(playSpeed, mockSongPlayer.playerState.value.timeElapsed)
    }

    @Test
    fun whenPlayDone_playerAutoPlaysNextSong() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currSong = PlayerSong(
            id = 17,
            title = "Ignorance",
            artistName = "Paramore",
            albumTitle = "Brand New Eyes",
            duration = duration
        )
        mockSongPlayer.currentSong = currSong
        testSongs.forEach { mockSongPlayer.addToQueue(it) }

        mockSongPlayer.play()
        advanceTimeBy(duration.toMillis() + 1)

        assertEquals(testSongs.first(), mockSongPlayer.currentSong)
    }

    @Test
    fun whenNext_queueIsNotEmpty_autoPlaysNextSong() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currSong = PlayerSong(
            id = 17,
            title = "Ignorance",
            artistName = "Paramore",
            albumTitle = "Brand New Eyes",
            duration = duration
        )

        mockSongPlayer.currentSong = currSong
        testSongs.forEach { mockSongPlayer.addToQueue(it) }

        mockSongPlayer.next()
        advanceTimeBy(100)

        assertTrue(mockSongPlayer.playerState.value.isPlaying)
    }
    @Test
    fun whenPlayListOfSongs_playerAutoPlaysNextSong() = runTest(testDispatcher) {
        val duration = Duration.ofSeconds(60)
        val currSong = PlayerSong(
            id = 6535,
            title = "Musique pour la Tristesse de Xion",
            artistName = "Yoko Shimomura",
            albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
            duration = Duration.parse("PT6M"),//22S"),
        )
        val firstSongFromList = PlayerSong(
            id = 17,
            title = "Ignorance",
            artistName = "Paramore",
            albumTitle = "Brand New Eyes",
            duration = Duration.parse("PT4M"),//26S"),
        )
        val secondSongFromList = PlayerSong(
            id = 1023,
            title = "88",
            artistName = "LM.C",
            albumTitle = "88 / ...With Vampire - Single",
            duration = Duration.parse("PT4M"),//5S"),
        )
        val songListToBeAddedToTheQueue: List<PlayerSong> = listOf(
            firstSongFromList, secondSongFromList
        )
        mockSongPlayer.currentSong = currSong

        mockSongPlayer.play(songListToBeAddedToTheQueue)
        assertEquals(firstSongFromList, mockSongPlayer.currentSong)

        advanceTimeBy(duration.toMillis() + 1)
        assertEquals(secondSongFromList, mockSongPlayer.currentSong)

        advanceTimeBy(duration.toMillis() + 1)
        assertEquals(currSong, mockSongPlayer.currentSong)
    }

    @Test
    fun whenNext_queueIsEmpty_doesNothing() {
        val song = testSongs[0]
        mockSongPlayer.currentSong = song
        mockSongPlayer.play()

        mockSongPlayer.next()

        assertEquals(song, mockSongPlayer.currentSong)
    }

    @Test
    fun whenAddToQueue_queueIsNotEmpty() = runTest(testDispatcher) {
        testSongs.forEach { mockSongPlayer.addToQueue(it) }

        advanceUntilIdle()

        val queue = mockSongPlayer.playerState.value.queue
        assertEquals(testSongs.size, queue.size)
        testSongs.forEachIndexed { index, playerSong ->
            assertEquals(playerSong, queue[index])
        }
    }

    @Test
    fun whenNext_queueIsNotEmpty_removeFromQueue() = runTest(testDispatcher) {
        mockSongPlayer.currentSong = PlayerSong(
            id = 6535,
            title = "Musique pour la Tristesse de Xion",
            artistName = "Yoko Shimomura",
            albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
            duration = Duration.parse("PT6M"),//22S")
        )
        testSongs.forEach { mockSongPlayer.addToQueue(it) }

        mockSongPlayer.play()
        advanceTimeBy(100)

        mockSongPlayer.next()
        advanceTimeBy(100)

        assertEquals(testSongs.first(), mockSongPlayer.currentSong)

        val queue = mockSongPlayer.playerState.value.queue
        assertEquals(testSongs.size - 1, queue.size)
    }

    @Test
    fun whenNext_queueIsNotEmpty_notRemovedFromQueue() = runTest(testDispatcher) {
        mockSongPlayer.currentSong = PlayerSong(
            id = 6535,
            title = "Musique pour la Tristesse de Xion",
            artistName = "Yoko Shimomura",
            albumTitle = "Kingdom Hearts Piano Collections - Field & Battle",
            duration = Duration.parse("PT6M"),//22S")
        )
        testSongs.forEach { mockSongPlayer.addToQueue(it) }

        mockSongPlayer.play()
        advanceTimeBy(100)

        mockSongPlayer.next()
        advanceTimeBy(100)

        assertEquals(testSongs.first(), mockSongPlayer.currentSong)

        val queue = mockSongPlayer.playerState.value.queue
        assertEquals(testSongs.size - 1, queue.size)
    }

    @Test
    fun whenPrevious_queueIsEmpty_resetSameSong() = runTest(testDispatcher) {
        mockSongPlayer.currentSong = testSongs[0]
        mockSongPlayer.play()
        advanceTimeBy(1000L)

        mockSongPlayer.previous()
        assertEquals(0, mockSongPlayer.playerState.value.timeElapsed.toMillis())
        assertEquals(testSongs[0], mockSongPlayer.currentSong)
    }
}
