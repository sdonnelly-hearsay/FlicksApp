package com.codepath.steve.flicksapp

import android.os.Bundle
import android.util.Log
import android.view.View
import com.codepath.steve.flicksapp.glide.GlideApp
import com.codepath.steve.flicksapp.services.RestClient
import com.codepath.steve.flicksapp.services.models.Movie
import com.codepath.steve.flicksapp.services.models.Video
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.util.concurrent.TimeUnit

class MovieDetailsActivity : YouTubeBaseActivity() {

    private val movieService by lazy { RestClient().movieService }
    private val disposables : CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_DATA)

        GlideApp
            .with(this)
            .load(IMAGE_PREFIX + movie?.backdropPath)
            .placeholder(R.drawable.placeholder)
            .into(ivMovieDetailsBanner)

        ivMovieDetailsBanner
            .clicks()
            .throttleFirst(THROTTLE_DURATION, TimeUnit.MILLISECONDS)
            .subscribe{ _ ->
                disposables += movieService.getVideoInfo(movie.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val playableVideo: Video? = getPlayableVideo(it.results)
                        youtube.visibility = View.VISIBLE

                        youtube.initialize(MainActivity.YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
                            override fun onInitializationSuccess(
                                provider: YouTubePlayer.Provider?,
                                player: YouTubePlayer?,
                                b: Boolean
                            ) {
                                player?.loadVideo(playableVideo?.key)
                            }

                            override fun onInitializationFailure(
                                provider: YouTubePlayer.Provider?,
                                result: YouTubeInitializationResult?
                            ) {
                                Log.d(javaClass.name, result.toString())
                            }

                        })
                    }, { e ->
                        e.printStackTrace()
                    })
            }

        movie?.apply {
            ratingBar.rating = ((voteAverage / 2.0).toFloat())
            tvMovieSynopsis.text = overview
        }
    }

    private fun getPlayableVideo(videos: List<Video>): Video? {
        videos.forEach {
            if (it.site == "YouTube")
                return it
        }
        return null
    }

    override fun onBackPressed() {
        if (youtube.visibility == View.VISIBLE) {
            youtube.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val THROTTLE_DURATION = 500L
        const val IMAGE_PREFIX = "https://image.tmdb.org/t/p/w500"
        const val MOVIE_DATA = "MovieData"
    }
}
