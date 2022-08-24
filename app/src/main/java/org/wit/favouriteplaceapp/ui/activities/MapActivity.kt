package org.wit.favouriteplaceapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.models.PlaceModel
import org.wit.favouriteplaceapp.utils.Constants.EXTRA_PLACE_DETAILS


//https://www.raywenderlich.com/230-introduction-to-google-maps-api-for-android-with-kotlin

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mFavDetails: PlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if (intent.hasExtra(EXTRA_PLACE_DETAILS)) {
            mFavDetails =
                intent.getSerializableExtra(EXTRA_PLACE_DETAILS) as PlaceModel
        }

        if (mFavDetails != null) {

            setSupportActionBar(toolbar_map)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mFavDetails!!.title

            toolbar_map.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(
            mFavDetails!!.latitude!!,
            mFavDetails!!.longitude!!
        )
        googleMap.addMarker(MarkerOptions().position(position).title(mFavDetails!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)
    }
}