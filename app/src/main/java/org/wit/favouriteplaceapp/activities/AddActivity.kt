package org.wit.favouriteplaceapp.activities

import android.app.Activity
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.database.Database
import org.wit.favouriteplaceapp.databinding.ActivityAddBinding
import org.wit.favouriteplaceapp.models.PlaceModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.getInstance

class AddActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var addLayout : ActivityAddBinding

    private var cal = getInstance()
    private lateinit var dateSetListner: DatePickerDialog.OnDateSetListener
    private var Latitude : Double = 0.0
    private var Longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)

        // Binding add_activity xml file and setting content view
        addLayout = ActivityAddBinding.inflate(layoutInflater)
        setContentView(addLayout.root)

        setSupportActionBar(addLayout.toolbaraddplace)

        // Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Same functionality as a back press button
        addLayout.toolbaraddplace.setNavigationOnClickListener{
            onBackPressed()
        }

        // We open the date dialog and wait for a user to set a date
        dateSetListner = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            //Call the update view function after user picked date
            updateDateInView()

        }
        //We automatically fill the date using the current date thats why this method is outside
        updateDateInView()
        //We use this on SetOnClickListener because the AddActivity class is also our
        //on click listener
        addLayout.date.setOnClickListener(this)
        //addLayout.AddImageBtn.setOnClickListener(this)
        addLayout.SaveBtn.setOnClickListener(this)
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
                    } else -> {
                    val favModel = PlaceModel(
                        0,
                        addLayout.title.text.toString(),
                        "no",
                        addLayout.description.text.toString(),
                        addLayout.date.text.toString(),
                        addLayout.location.text.toString(),
                        Latitude,
                        Longitude
                    )
                    val dbHandler =  Database(this)
                    //the add method returns a long value which we store below
                    val addFavPlace = dbHandler.addFavouritePlace(favModel)
                    if(addFavPlace > 0){
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    //Finishes add activity and goes back to main
                }

                }
            }
            /*
            R.id.AddImageBtn ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo from gallery",
                "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(
                        this@AddActivity,
                        "Camera Selection ",
                        Toast.LENGTH_SHORT).show()

                    }

                }

            } */
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

/*
    //https://github.com/Karumi/Dexter Multiple Permissions
    // https://www.geeksforgeeks.org/easy-runtime-permissions-in-android-with-dexter/
    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        //Refer to mulitple permissions on Dexter github,
        //Chnaged the original code from java to kotlin,
        // we pass in the object - MultiplePermissionsListner, which then d
        ).withListener(object: MultiplePermissionsListener{
            //This will be called when the permissions are checked and then the if statement
            //Checks if all the permissions are granted and if they are the toast message is displayed
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {if(report.areAllPermissionsGranted()){
                Toast.makeText(this@AddActivity,
                "Permissions are granted",
                Toast.LENGTH_SHORT).show()
            }}

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken )
            {
                AlertDialog.Builder(this@AddActivity).setMessage(
                    "Permissions need to enabled in settings")
            }
        }).check();
    }
 */

}