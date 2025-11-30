package com.example.test_lab_week_12

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.test_lab_week_12.model.Movie
import com.example.test_lab_week_12.model.MovieViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // Adapter sesuai interface MovieClickListener
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

        val recyclerView = findViewById<RecyclerView>(R.id.movie_list)
        recyclerView.adapter = movieAdapter

        // Ambil repository dari MovieApplication
        val movieRepository = (application as MovieApplication).movieRepository

        // ViewModel factory
        val movieViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MovieViewModel(movieRepository) as T
                }
            }
        )[MovieViewModel::class.java]

        // Collect StateFlow
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {

                // Collect daftar film
                launch {
                    movieViewModel.popularMovies.collect { movies ->
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

                        val filtered = movies
                            .filter { it.releaseDate?.startsWith(currentYear) == true }
                            .sortedByDescending { it.popularity }

                        movieAdapter.addMovies(filtered)
                    }
                }

                // Collect error kalau ada
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
