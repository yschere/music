package com.example.music.model

import com.example.music.data.database.model.Genre

data class GenreInfo(
    var id: Long = 0,
    var name: String = "",
)

fun Genre.asExternalModel() =
    GenreInfo(
        id = id,
        name = name
    )
