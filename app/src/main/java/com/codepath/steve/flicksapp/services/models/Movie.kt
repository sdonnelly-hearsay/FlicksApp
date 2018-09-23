package com.codepath.steve.flicksapp.services.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    var id : Int = 0,
    @SerializedName("vote_count") var voteCount : Int = 0,
    var video : Boolean = false,
    @SerializedName("vote_average") var voteAverage : Double = 0.0,
    var title : String = "",
    var popularity : Double = 0.0,
    @SerializedName("poster_path") var posterPath : String = "",
    @SerializedName("original_language") var originalLanguage : String = "",
    @SerializedName("original_title") var originalTitle : String = "",
    @SerializedName("genre_ids") var genreIds : ArrayList<Int> = ArrayList(),
    @SerializedName("backdrop_path") var backdropPath : String = "",
    var adult : Boolean = false,
    var overview : String = "",
    @SerializedName("release_date") var releaseDate : String = ""
) : Parcelable
