package com.example.test_lab_week_12.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _selectedYear = MutableStateFlow("2024")
    val selectedYear: StateFlow<String> = _selectedYear

    // Combine movies + selected year → filteredMovies
    val filteredMovies: StateFlow<List<Movie>> =
        combine(_movies, _selectedYear) { movies, year ->
            movies.filter { it.releaseDate?.startsWith(year) == true }
                .sortedByDescending { it.popularity }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    init {
        fetchMovies()
    }

    fun updateSelectedYear(year: String) {
        _selectedYear.value = year
    }

    private fun fetchMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { e -> _error.value = "Error: ${e.message}" }
                .collect { list -> _movies.value = list }
        }
    }
}
