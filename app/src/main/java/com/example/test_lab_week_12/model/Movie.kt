package com.example.test_lab_week_12.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Movie(
    val adult: Boolean = false,
    val backdrop_path: String? = null,
    val id: Int = 0,
    @field:Json(name = "original_language")
    val originalLanguage: String? = null,
    @field:Json(name = "original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    val popularity: Float = 0f,
    @field:Json(name = "poster_path")
    val posterPath: String? = null,
    @field:Json(name = "release_date")
    val releaseDate: String? = null,
    val title: String? = null,
    val video: Boolean = false,
    @field:Json(name = "vote_average")
    val voteAverage: Float = 0f,
    @field:Json(name = "vote_count")
    val voteCount: Int = 0
)

