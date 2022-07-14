package org.wit.favouriteplaceapp.models

import java.io.Serializable

data class PlaceModel (
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
    //Serializing allows us to bring it into a format to bring from one class to another...
) : Serializable