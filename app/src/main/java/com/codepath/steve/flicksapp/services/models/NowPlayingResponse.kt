
package com.codepath.steve.flicksapp.services.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NowPlayingResponse(
    var results : ArrayList<Movie> = ArrayList()
) : Parcelable
