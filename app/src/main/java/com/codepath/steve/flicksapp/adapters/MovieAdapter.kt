package com.codepath.steve.flicksapp.adapters

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codepath.steve.flicksapp.MovieDetailsActivity
import com.codepath.steve.flicksapp.R
import com.codepath.steve.flicksapp.YoutubeClickedListener
import com.codepath.steve.flicksapp.glide.GlideApp
import com.codepath.steve.flicksapp.services.models.Movie
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_movie_popular.view.*
import java.util.concurrent.TimeUnit

class MovieAdapter(private val context: Context, private var movies: ArrayList<Movie>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var youtubeClickedListener: YoutubeClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == REGULAR_MOVIE) {
            MovieViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false),
                parent.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            )
        } else {
            PopularMovieViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_movie_popular,
                    parent,
                    false
                )
            )
        }

    fun replaceMovieSet(newMovies: ArrayList<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = movies.size

    override fun getItemViewType(position: Int): Int {
        return if (movies[position].voteAverage >= POPULARITY_THRESHOLD) POPULAR_MOVIE else REGULAR_MOVIE
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == POPULAR_MOVIE) {
            (viewHolder as PopularMovieViewHolder).bind(movies[position])
        } else {
            (viewHolder as MovieViewHolder).bind(movies[position])
        }
    }

    inner class MovieViewHolder(
        private val movieView: View,
        private val isOrientationPortrait: Boolean
    ) : RecyclerView.ViewHolder(movieView) {
        fun bind(movie: Movie) {
            movieView.apply {
                tvMovieTitle.text = movie.title
                tvMovieDescription.text = movie.overview

                GlideApp
                    .with(context)
                    .load(if (isOrientationPortrait) IMAGE_PREFIX + movie.posterPath else IMAGE_PREFIX + movie.backdropPath)
                    .placeholder(R.drawable.placeholder)
                    .into(ivMovie)

                clMovieContainer
                    .clicks()
                    .throttleFirst(THROTTLE_DURATION, TimeUnit.MILLISECONDS)
                    .subscribe {
                        val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                            putExtra(MovieDetailsActivity.MOVIE_DATA, movie)
                        }
                        context.startActivity(intent)
                    }
            }
        }
    }

    inner class PopularMovieViewHolder(
        private val movieView: View
    ) : RecyclerView.ViewHolder(movieView) {
        fun bind(movie: Movie) {
            movieView.apply {
                GlideApp
                    .with(context)
                    .load(IMAGE_PREFIX + movie.backdropPath)
                    .placeholder(R.drawable.placeholder)
                    .into(ivPopularMovie)

                ivPopularMovie
                    .clicks()
                    .throttleFirst(THROTTLE_DURATION, TimeUnit.MILLISECONDS)
                    .subscribe {
                        youtubeClickedListener?.onYoutubeClicked(movie.id)
                    }
            }
        }
    }

    companion object {
        const val THROTTLE_DURATION = 500L
        const val IMAGE_PREFIX = "https://image.tmdb.org/t/p/w500"
        const val POPULARITY_THRESHOLD = 7.0
        const val REGULAR_MOVIE = 0
        const val POPULAR_MOVIE = 1
    }
}
