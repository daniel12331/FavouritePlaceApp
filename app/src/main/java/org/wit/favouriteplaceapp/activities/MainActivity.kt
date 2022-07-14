package org.wit.favouriteplaceapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.favouriteplaceapp.adapter.PlaceAdapter
import org.wit.favouriteplaceapp.database.Database
import org.wit.favouriteplaceapp.databinding.ActivityMainBinding
import org.wit.favouriteplaceapp.models.PlaceModel


class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainLayout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainLayout.root)

        mainLayout.addActivityBtn.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getFavPlaceDB()

    }
    private fun getFavPlaceDB() {
        val dbHandler = Database(this)
        //gets favourtie place list from database
        val getFavPlaceList: ArrayList<PlaceModel> = dbHandler.getFavouritePlaceList()

        if (getFavPlaceList.size > 0) {
            mainLayout.favPlaceList.visibility = View.VISIBLE
            mainLayout.NoFavPlaceText.visibility = View.GONE

            setRecyclerView(getFavPlaceList)
        }
        else  {
            mainLayout.favPlaceList.visibility = View.GONE
            mainLayout.NoFavPlaceText.visibility = View.VISIBLE
        }
    }
    private fun setRecyclerView(favPlaceList : ArrayList<PlaceModel>){

        mainLayout.favPlaceList.layoutManager = LinearLayoutManager(this)
        mainLayout.favPlaceList.setHasFixedSize(true)

        val placesAdapter = PlaceAdapter(this, favPlaceList)
        mainLayout.favPlaceList.adapter = placesAdapter


        //onclicklistener from placeadapter....
        placesAdapter.setOnClickListener(object: PlaceAdapter.OnClickListener{
            //overiding the onclick function that i have created...
            override fun onClick(position: Int, model: PlaceModel) {
                //moving over to the other activity with the intent...
              val intent = Intent(this@MainActivity,
                  DetailActivity::class.java)
                //we pass a whole object (.putExtra), we serialize so we can pass it through .putExtra

                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getFavPlaceDB()
            }
        }

    }

    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"

    }
}