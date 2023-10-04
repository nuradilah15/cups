package com.dicoding.picodiploma.coba.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post (
    var postId : String = "",
    var postImage : String? = null,
    var publisher : String = "",
    var description : String = ""
): Parcelable