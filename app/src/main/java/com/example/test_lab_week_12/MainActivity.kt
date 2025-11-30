package com.example.test_lab_week_12

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.example.test_lab_week_12.model.MovieViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
        override fun onMovieClick(movie: Movie) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Clicked: ${movie.title}",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerYear = findViewById<Spinner>(R.id.spinner_year)
        val recyclerView = findViewById<RecyclerView>(R.id.movie_list)

        // FIX #1 — WAJIB
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = movieAdapter

        val years = listOf("2023", "2024", "2025")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = adapter

        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MovieViewModel(movieRepository) as T
                }
            }
        )[MovieViewModel::class.java]

        // FIX #2 — Listener Spinner yang benar
        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                movieViewModel.updateSelectedYear(years[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    movieViewModel.filteredMovies.collect { movies ->
                        movieAdapter.setMovies(movies)
                    }
                }

                launch {
                    movieViewModel.error.collect { errorMsg ->
                        if (errorMsg.isNotEmpty()) {
                            Snackbar.make(recyclerView, errorMsg, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
