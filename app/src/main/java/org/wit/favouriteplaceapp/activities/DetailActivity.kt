package org.wit.favouriteplaceapp.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fav_place_detail_view.*
import org.wit.favouriteplaceapp.databinding.FavPlaceDetailViewBinding
import org.wit.favouriteplaceapp.models.PlaceModel


class DetailActivity : AppCompatActivity() {
    //fav_place_detail_view layout content...
    private lateinit var detailLayout : FavPlaceDetailViewBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailLayout = FavPlaceDetailViewBinding.inflate(layoutInflater)
        setContentView(detailLayout.root)

        var DetailModel : PlaceModel? = null
        //We check if an extra which has this name and if it does then assign the intent which gets
        //the serizable extra from the main activity as the placemodel...
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS )){
            DetailModel = intent.getParcelableExtra(
                MainActivity.EXTRA_PLACE_DETAILS) as PlaceModel?
        }
        //then we check if our place model is not empty, we set the title of the selected element on the top
        // of the screen, if you click it you back press..
        if(DetailModel != null){
            setSupportActionBar(toolbar_fav_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = DetailModel.title

            toolbar_fav_place_detail.setNavigationOnClickListener{
                onBackPressed()
            }
            iv_place_image.setImageURI(Uri.parse(DetailModel.image))
            tv_description.text = DetailModel.description
            tv_location.text = DetailModel.location
        }
    }

}