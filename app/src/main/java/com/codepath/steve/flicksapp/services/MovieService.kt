package com.codepath.steve.flicksapp.services

import com.codepath.steve.flicksapp.services.models.NowPlayingResponse
import com.codepath.steve.flicksapp.services.models.VideoInfoResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {
    @GET("/$API_VERSION/movie/now_playing?api_key=$API_KEY")
    fun getMovies() : Observable<NowPlayingResponse>

    @GET("/$API_VERSION/movie/{movieId}/videos?api_key=$API_KEY")
    fun getVideoInfo(@Path(value="movieId") movieId : Int) : Observable<VideoInfoResponse>

    companion object {
        const val API_VERSION = 3
        const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
    }
}
