package org.wit.favouriteplaceapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.wit.favouriteplaceapp.databinding.FavPlaceDetailViewBinding



class DetailActivity : AppCompatActivity() {
    //fav_place_detail_view layout content...
    private lateinit var detailLayout : FavPlaceDetailViewBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailLayout = FavPlaceDetailViewBinding.inflate(layoutInflater)
        setContentView(detailLayout.root)

    }

}