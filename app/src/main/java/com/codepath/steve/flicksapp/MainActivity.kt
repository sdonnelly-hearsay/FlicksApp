package com.codepath.steve.flicksapp

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.codepath.steve.flicksapp.adapters.MovieAdapter
import com.codepath.steve.flicksapp.services.RestClient
import com.codepath.steve.flicksapp.services.models.Video
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : YouTubeBaseActivity(), YoutubeClickedListener {

    private val movieService by lazy { RestClient().movieService }
    private val movieAdapter by lazy { MovieAdapter(this@MainActivity, ArrayList()) }

    private val disposables: CompositeDisposable by lazy { CompositeDisposable() }
    private var youtubePlayer: YouTubePlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvMovieList.layoutManager = LinearLayoutManager(this)
        rvMovieList.adapter = movieAdapter

        movieAdapter.youtubeClickedListener = this

        youtube.initialize(YOUTUBE_API_KEY, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                b: Boolean
            ) {
                youtubePlayer = player
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider?,
                result: YouTubeInitializationResult?
            ) {
                Log.d(javaClass.name, result.toString())
            }

        })
    }

    override fun onResume() {
        super.onResume()
        disposables += movieService.getMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                movieAdapter.replaceMovieSet(it.results)
            }, { e ->
                e.printStackTrace()
            }
            )
    }

    override fun onYoutubeClicked(id: Int) {
        disposables += movieService.getVideoInfo(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val playableVideo: Video? = getPlayableVideo(it.results)
                if (playableVideo != null)
                    youtubePlayer?.loadVideo(playableVideo.key)
                youtube.visibility = View.VISIBLE
                rvMovieList.visibility = View.GONE


            }, { e ->
                e.printStackTrace()
            })
    }

    override fun onBackPressed() {
        if (youtube.visibility == View.VISIBLE) {
            youtube.visibility = View.GONE
            youtubePlayer?.pause()
            rvMovieList.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    private fun getPlayableVideo(videos: List<Video>): Video? {
        videos.forEach {
            if (it.site == "YouTube")
                return it
        }
        return null
    }

    override fun onPause() {
        super.onPause()
        disposables.dispose()
    }

    companion object {
        const val YOUTUBE_API_KEY = "AIzaSyAEeGb1IqwtBX69JcmruYu00nzU-6PGB7w"
    }
}

interface YoutubeClickedListener {
    fun onYoutubeClicked(id: Int)
}
