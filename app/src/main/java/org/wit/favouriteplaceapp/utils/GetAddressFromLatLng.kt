package org.wit.favouriteplaceapp.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import java.lang.StringBuilder
import java.util.*

@Suppress("DEPRECATION")
class GetPlaceFromLatLng(context: Context,
                         private val latitude: Double,
                         private val longitude: Double)
    : AsyncTask<Void, String, String>() {

    private val geocoder : Geocoder = Geocoder(context, Locale.getDefault())

    private lateinit var mPlaceListener : PlaceListener

    override fun doInBackground(vararg p0: Void?): String {
    try {
        val placeList : List<Address>? = geocoder.getFromLocation(latitude,longitude, 1)

        if(placeList != null && placeList.isNotEmpty()) {
            val address: Address = placeList[0]
            val sb = StringBuilder()
            for (i in 0..address.maxAddressLineIndex) {
                sb.append(address.getAddressLine(i)).append(" ")
            }
            //deleting the last space at the end of the string...
            sb.deleteCharAt(sb.length - 1)
            return sb.toString()
        }
    }
    catch (e:Exception){
    e.printStackTrace()
        }
        return ""
    }

    override fun onPostExecute(result: String?) {
        if(result == null){
            mPlaceListener.onError()
        }
        else{
            mPlaceListener.onPlaceFound(result)
        }
        super.onPostExecute(result)
    }


    fun setPlaceListener(placeListener: PlaceListener){
        mPlaceListener = placeListener
    }

    fun getPlaceAddress(){
        execute()
    }


    interface PlaceListener {
        fun onPlaceFound(place: String?)

        fun onError()
    }

}