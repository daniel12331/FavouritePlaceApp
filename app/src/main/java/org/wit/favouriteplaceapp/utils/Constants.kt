package org.wit.favouriteplaceapp.utils

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap

object Constants {
    //we want to set variables that will be constant, for example the line below will always allow us to call our users collection in firestore...
    const val USERS: String = "users"
    const val PLACES: String = "places"

    const val USERS_ID: String = "id"

    //ADDACTIVITY
    const val GALLERY = 1
    const val CAMERA = 2
    const val PLACE_AUTO = 3

    const val IMAGE: String = "Fav_Image"

    const val EXTRA_PLACE_DETAILS = "extra_place_details"

    const val EXTRA_PLACE_ID = "extra_place_id"


    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}