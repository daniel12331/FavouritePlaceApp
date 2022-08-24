package org.wit.favouriteplaceapp.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize
data class PlaceModel(
    val id: String? = null,
    val title: String? = null,
    val image: String? = null,
    val description: String? = null,
    val date: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    var place_id: String? = null
) : Parcelable


//I came across someone using parcelable instead of serizable,
//and apparently its almost ten times faster then serizable,
//it was very easy to implement so I did it... some references below
//https://proandroiddev.com/serializable-or-parcelable-why-and-which-one-17b274f3d3bb
//https://stackoverflow.com/questions/3323074/android-difference-between-parcelable-and-serializable
