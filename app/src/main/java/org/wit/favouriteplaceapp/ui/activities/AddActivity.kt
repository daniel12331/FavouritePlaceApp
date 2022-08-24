package org.wit.favouriteplaceapp.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add.*
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.databinding.ActivityAddBinding
import org.wit.favouriteplaceapp.firestore.FirestoreClass
import org.wit.favouriteplaceapp.models.PlaceModel
import org.wit.favouriteplaceapp.utils.Constants
import org.wit.favouriteplaceapp.utils.GetPlaceFromLatLng
import org.wit.favouriteplaceapp.utils.GlideLoader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance

@Suppress("DEPRECATION")
open class AddActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var addLayout : ActivityAddBinding

    private var saveImageURI : Uri? = null
    private var mFavPlaceImage : String = ""

    private var cal = getInstance()
    private lateinit var dateSetListner: DatePickerDialog.OnDateSetListener

    private var Latitude : Double = 0.0
    private var Longitude : Double = 0.0

    private var mFavPlaceDetails : PlaceModel? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding add_activity xml file and setting content view
        addLayout = ActivityAddBinding.inflate(layoutInflater)
        setContentView(addLayout.root)
        setSupportActionBar(addLayout.toolbaraddplace)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //if intent has extra we populate the items..
        if(intent.hasExtra(Constants.EXTRA_PLACE_DETAILS)){
            //whatever is avaliable at that specific position we want to pass it...
            mFavPlaceDetails = intent.getParcelableExtra(Constants.EXTRA_PLACE_DETAILS)
        }
        if(mFavPlaceDetails !=null){
            if(mFavPlaceDetails!!.place_id!!.isNotEmpty()){
                supportActionBar?.title = "Edit Favourite Place"
                addLayout.SaveBtn.text = "Update"

                addLayout.title.setText(mFavPlaceDetails?.title)
                addLayout.description.setText(mFavPlaceDetails!!.description)
                addLayout.date.setText(mFavPlaceDetails!!.date)
                addLayout.location.setText(mFavPlaceDetails!!.location)
                Latitude = mFavPlaceDetails!!.latitude!!
                Longitude = mFavPlaceDetails!!.longitude!!
                mFavPlaceImage = mFavPlaceDetails!!.image!!

                GlideLoader(this).loadFavPlaceImage(mFavPlaceDetails!!.image!!, addLayout.Imageholder)

            }
        }

        // Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Same functionality as a back press button
        addLayout.toolbaraddplace.setNavigationOnClickListener{
            onBackPressed()
        }

        if(!Places.isInitialized()){
            Places.initialize(this@AddActivity, resources.getString(R.string.google_maps_key))
            }

        // We open the date dialog and wait for a user to set a date
        dateSetListner = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //Call the update view function after user picked date
            updateDateInView()

        }
        //We automatically fill the date using the current date thats why this method is outside
        updateDateInView()

        //if mFavPlaceDetails is null we know we are just creating a new entry,
        //but if it is not null we know we actually are editing something with the populated data

        //We use this on SetOnClickListener because the AddActivity class is also our
        //on click listener
        addLayout.date.setOnClickListener(this)
        addLayout.AddImageBtn.setOnClickListener(this)
        addLayout.SaveBtn.setOnClickListener(this)
        addLayout.location.setOnClickListener(this)
        addLayout.SelectCurrentLocation.setOnClickListener(this)
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    //requestiong location from selectcurrentlocation btn
    //We will suppress the .requestLocationUpdate, as we already check permissions before running this function,
    //hence the reason why we suppress the error
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1000
        mLocationRequest.numUpdates = 1

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallBack, Looper.myLooper())

    }

    private val mLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val mLastLocation: Location = p0.lastLocation!!
            Latitude = mLastLocation.latitude
            Longitude = mLastLocation.longitude

            val placeTask = GetPlaceFromLatLng(this@AddActivity, Latitude, Longitude)

            placeTask.setPlaceListener(object : GetPlaceFromLatLng.PlaceListener {
                override fun onPlaceFound(place: String?) {
                    addLayout.location.setText(place)
                }
                override fun onError() {
                    Log.e("Get Place Address::", "Something went wrong")
                }
            })
            placeTask.getPlaceAddress()
        }
    }

    // OnClick to handle all the onclick events in the add activity
    override fun onClick(v: View?) {
        // Whatever view that was clicked on is going to be passed through as v and
        // checks the id
        when(v!!.id) {
            // When someone clicks the date btn we use a lambda expression to execute code

            R.id.date -> {
                // DatePickerDialog we pass in the context, we pass i
                DatePickerDialog(
                    this@AddActivity,
                    dateSetListner,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            R.id.SaveBtn ->{
                when {
                    //Validation
                    addLayout.title.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                    }
                    addLayout.description.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                    }
                    addLayout.location.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                    }
                    (saveImageURI == null && mFavPlaceDetails!!.image!!.isEmpty()) -> {
                        Toast.makeText(this, "Please enter a Image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        uploadFavPlaceImage()
                 }
                }
            }

            /**
            --------------------Gallery and Camera picking functionality--------------------------
             */
            //When button is pressed we create a dialog, having two options one for
            //choosing photo libary or the other for using the camera...
            R.id.AddImageBtn ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo from gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    //never using the first variable...
                        _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        //we can now call our chooseImage... for camera selection
                        1 -> chooseImageFromCamera()

                    }
                }
                pictureDialog.show()

            }
            R.id.location ->{
                 try {
                     //GOOGLES STRUCTURE FOR PLACES API
                     //https://youtu.be/iEzF9G8gDy0
                    //this is the list of fields which has to be passed
                     val fields = listOf(
                         Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                         Place.Field.ADDRESS
                     )
                     // start the autocomplete intent with a unique request code.
                     val intent =
                         Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                             .build(this@AddActivity)
                     startActivityForResult(intent, Constants.PLACE_AUTO)



                 }
                 catch (e:Exception){
                     e.printStackTrace()
                 }
            }
            R.id.SelectCurrentLocation -> {
                if(!isLocationEnabled()){
                    Toast.makeText(this,"Your location provider is turned off, please turn it on!",Toast.LENGTH_SHORT).show()

                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }else{
                    Dexter.withActivity(this).withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                        .withListener(object : MultiplePermissionsListener {

                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                            if (report!!.areAllPermissionsGranted()) {

                                requestLocationData()
                            }
                        }
                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ){
                            AlertDialog.Builder(this@AddActivity).setMessage(
                                "Permissions need to enabled in settings").setPositiveButton("GO TO SETTINGS"
                            ) { _, _ ->
                                try {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", packageName, null)
                                    intent.data = uri
                                    startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    e.printStackTrace()
                                }
                            }.setNegativeButton("Cancel") {dialog, _ ->
                                dialog.dismiss()
                            }.show()
                        }
                        }).onSameThread().check()
                }
            }
        }

    }

    // Function to update date in the view
    private fun updateDateInView(){
        //dateFormat = how our date will be formatted
        val dateFormat = "dd.MM.yyyy"
        // Locale gets date from ur emulator/phone
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        //Here we set the text in date to our formatted calendar
        addLayout.date.setText(sdf.format(cal.time).toString())
    }




    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //We check if the resultCode is ok
        if(resultCode == RESULT_OK){
            //Then we check if the actvity result is from that call...
            if(requestCode == Constants.GALLERY){
                //then we check if the data we get is not null, then we run the code..
                if(data != null){
                    //we then get the content uri which is data.data
                     saveImageURI = data.data
                    //once we have the data we can try
                    try{
                        //we will try to get a image from the mediastore and from the contentURI
                        //cant find a alternative instead of using getBitmap
                        //we pass the data through getBitmap and we use the contentresolver to resolve the contentURI to the content provider..
                        val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, saveImageURI)
                        //finally we set the image
                        Imageholder.setImageBitmap(selectedImage)
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddActivity, "Failed to load image...", Toast.LENGTH_SHORT).show()
                    }
                }
                //Camera request code...

            } else if(requestCode == Constants.CAMERA){
                //https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
                //we take the "data" and we get extras from it so when we take the image the data will contain the image, we get that data by using .get("data") which will give us the object,
                // and then we convert it into a bitmap...
                val selectedImageFromCamera : Bitmap = data!!.extras!!.get("data") as Bitmap

                Imageholder.setImageBitmap(selectedImageFromCamera)

            }
            //new request code for places autocomplete
            else if(requestCode == Constants.PLACE_AUTO ){
                //this is all following googles structure...
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                addLayout.location.setText(place.address)
                Latitude = place.latLng!!.latitude
                Longitude = place.latLng!!.longitude
            }
        }
    }

    /**
    --------------------Permission handling using dexter--------------------------
     */
    //https://github.com/Karumi/Dexter Dexter -- Multiple Permissions -- Library
    // https://www.geeksforgeeks.org/easy-runtime-permissions-in-android-with-dexter/
    private fun choosePhotoFromGallery(){
        //using withContext, we can pass in the activity(this) then withPermissions
        //we write multiple permissions that we need as seen below.
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            //Refer to mulitple permissions on Dexter github,
            //Chnaged the original code from java to kotlin,
            // we pass in the object - MultiplePermissionsListner, which then d
        ).withListener(object: MultiplePermissionsListener{
            //This will be called when the permissions are checked and then the if statement
            //Checks if all the permissions are granted and if they are the toast message is displayed
            //Some reference videos that helped me understand dexter, take in mind he uses different permissions
            //https://youtu.be/jqWj_NEmcHA
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                //
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    //cant find any alternative so using startActivityForResult, result is checked after
                    //onActivityResult is finished...
                    startActivityForResult(galleryIntent, Constants.GALLERY)
                }}

            // in onPermissionRationaleShouldBeShown is where we need to tell the user why we need permissions...
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                            token: PermissionToken )
            {
                AlertDialog.Builder(this@AddActivity).setMessage(
                    "Permissions need to enabled in settings").setPositiveButton("GO TO SETTINGS"
                    //the following code sends the user to the settings, were not passing any variables so instead we use _
                    //we run an intent called Settings.ACTION......
                    //we are also passing the uri, which is the uri for packagename...
                    //so we send the user directly to application settings where we can change the user permissions
                    //we add the data to the intent, the uri...
                    //then we start the activity
                    //https://stackoverflow.com/questions/19517417/opening-android-settings-programmatically
                    //additonally I put a try and catch for error handling
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }

                    //If the user clicks the negative button, we dialog.dismiss
                }.setNegativeButton("Cancel") {dialog, _ ->
                    dialog.dismiss()
                }.show()
            }
        }).onSameThread().check()
    }

    //Similar code from above but different permissions used, also use a different intent
    private fun chooseImageFromCamera(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {

                if(report!!.areAllPermissionsGranted()){
                    //Intent will be directly to the mediastore
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, Constants.CAMERA)
                }}
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>,
                                                            token: PermissionToken )
            {
                AlertDialog.Builder(this@AddActivity).setMessage(
                    "Permissions need to enabled in settings").setPositiveButton("GO TO SETTINGS"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel") {dialog, _ ->
                    dialog.dismiss()
                }.show()
            }
        }).onSameThread().check()
    }


    private fun uploadFavPlaceImage() {
            if(saveImageURI != null){
                FirestoreClass().uploadImage(this, saveImageURI)
            }
        else {
                Toast.makeText(this, "Place was updated successfully", Toast.LENGTH_SHORT).show()
                uploadFavPlaceDetails()

            }
    }


    private fun uploadFavPlaceDetails() {

        val model = PlaceModel(
            FirestoreClass().getCurrentUserID(),
            addLayout.title.text.toString(),
            mFavPlaceImage,
            addLayout.description.text.toString(),
            addLayout.date.text.toString(),
            addLayout.location.text.toString(),
            Latitude,
            Longitude

        )
        //Check if the there is details in the mFavPlaceDetails, if so we we can update it.. if not we can add it...
        if(mFavPlaceDetails != null && mFavPlaceDetails!!.place_id!!.isNotEmpty() ){
            FirestoreClass().updateFavDetails(this, model, mFavPlaceDetails!!.place_id!!)
        }
        else{
            FirestoreClass().uploadFavPlace(this, model)
        }

    }

    fun imageUploadSuccess(imageURL: String){
        mFavPlaceImage = imageURL
        uploadFavPlaceDetails()
    }
    fun placeUploadSuccess(){
        Toast.makeText(this, "Place was uploaded successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
    fun updateDetailsSuccess(){

        //We do something similar to before, but instead of using functions we display toasts, we can either notify the user that he updated or added a place..
        if(mFavPlaceDetails != null && mFavPlaceDetails!!.id!!.isNotEmpty() ){
            Toast.makeText(this@AddActivity,"Favourtie Place was updated successfully", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this@AddActivity,"Favourtie Place was added successfully", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

}
