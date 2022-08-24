package org.wit.favouriteplaceapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fav_place_detail_view.*
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.databinding.FavPlaceDetailViewBinding
import org.wit.favouriteplaceapp.firestore.FirestoreClass
import org.wit.favouriteplaceapp.models.PlaceModel
import org.wit.favouriteplaceapp.ui.activities.fragments.ListFragment
import org.wit.favouriteplaceapp.utils.Constants
import org.wit.favouriteplaceapp.utils.Constants.EXTRA_PLACE_DETAILS
import org.wit.favouriteplaceapp.utils.GlideLoader


class DetailActivity : AppCompatActivity() {
    //fav_place_detail_view layout content...
    private lateinit var detailLayout : FavPlaceDetailViewBinding

    private var mPlaceId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailLayout = FavPlaceDetailViewBinding.inflate(layoutInflater)
        setContentView(detailLayout.root)

        //Check if intent has extra, we then assign the id to mPlaceID
        if(intent.hasExtra(Constants.EXTRA_PLACE_ID)){
            mPlaceId = intent.getStringExtra(Constants.EXTRA_PLACE_ID)!!
            Log.i("ID",mPlaceId)
        }


        setSupportActionBar(detailLayout.toolbarFavPlaceDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Same functionality as a back press button
        detailLayout.toolbarFavPlaceDetail.setNavigationOnClickListener{
            onBackPressed()
        }

        getFavDetails()
    }

    fun getFavDetails(){
        FirestoreClass().getFavDetails(this, mPlaceId)
    }

    fun favDetailsSuccess(places: PlaceModel){


        supportActionBar!!.title = places.title
        GlideLoader(this).loadFavPlaceImage(places.image!!, iv_place_image)
        tv_description.text = places.description
        tv_location.text = places.location

        btn_view_on_map.setOnClickListener{
            val intent = Intent(this@DetailActivity, MapActivity::class.java)
            intent.putExtra(EXTRA_PLACE_DETAILS, places)
            startActivity(intent)
        }

    }

}
