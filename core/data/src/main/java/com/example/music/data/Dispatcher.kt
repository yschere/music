package com.example.music.data

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val musicDispatcher: MusicDispatchers)

enum class MusicDispatchers {
    Main,
    IO,
}
