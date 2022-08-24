package org.wit.favouriteplaceapp.firestore


import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.libraries.places.api.model.Place
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.wit.favouriteplaceapp.models.PlaceModel
import org.wit.favouriteplaceapp.ui.activities.LoginActivity
import org.wit.favouriteplaceapp.ui.activities.RegisterActivty
import org.wit.favouriteplaceapp.models.User
import org.wit.favouriteplaceapp.ui.activities.AddActivity
import org.wit.favouriteplaceapp.ui.activities.DetailActivity
import org.wit.favouriteplaceapp.ui.activities.fragments.ListFragment
import org.wit.favouriteplaceapp.utils.Constants


class FirestoreClass {
    //gets firestore instance..
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerTheUser(activity: RegisterActivty, userInfo: User) {

        //The "users" collection name...
        //also using the Constants Object, USERS Variable..
        mFireStore.collection(Constants.USERS)
            //The document ID for users fields
            .document(userInfo.id)
            //Then the userinfo is the field and the setoption is set to merge...
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userIsRegistered()

            }
            .addOnFailureListener{ e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user",
                    e
                )
            }
    }

    //Getting the current users ID..
    fun getCurrentUserID(): String {

        //instance of current user using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        //a variable to assign the currentUserID if it is not null or else it will be blank...
        var currentUserID = ""

        //if the current user is not empty/ not logged in, we store the current user ".uid"
        if(currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    //Function to get the details of the current users...
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we want the data.
        mFireStore.collection(Constants.USERS)
        //the document id to get the fields of the user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //here we have received the dcoument snapshot which is converted into the user data model object.
                val user = document.toObject(User::class.java)!!

                when(activity) {
                    //when the activity type is a of type LoginActivity then we use the activity.userLogg..
                    is LoginActivity -> {
                        activity.userLoggedInSuccessful(user)
                    }

                }
            }
            .addOnFailureListener{ e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while logging in the user",
                    e
                )
            }
    }

    fun uploadImage(activity: AddActivity, imageFileURI: Uri?) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                                activity.imageUploadSuccess(uri.toString())
                        }
                    }

            .addOnFailureListener { exception ->
                //print the error in log.
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadFavPlace(activity: AddActivity, placeDetails: PlaceModel) {
        mFireStore.collection(Constants.PLACES)
            .document()
            .set(placeDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.placeUploadSuccess()
            }
            .addOnFailureListener{e ->
                Log.e(
                    activity.javaClass.simpleName,
                "Error while uploading details..",
                    e
                )

            }

    }

    fun getFavPlace(fragment: ListFragment){
        //accessing places collection in db...
        mFireStore.collection(Constants.PLACES)
            //function to get the current logged in users collection..
            .whereEqualTo(Constants.USERS_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Fav Place list", document.documents.toString())
                val favPlaceList: ArrayList<PlaceModel> = ArrayList()


                for(i in document.documents) {

                    val places = i.toObject(PlaceModel::class.java)
                    places!!.place_id = i.id

                    favPlaceList.add(places)
                }
                fragment.successFavListFromFireStore(favPlaceList)
            }
            .addOnFailureListener{
                Log.e("Get Fav List", "Error while getting list.")
            }
    }

    fun updateFavDetails(activity: AddActivity, placeDetails : PlaceModel, placesId: String){
        mFireStore.collection(Constants.PLACES)
                //getting the specific document ID
            .document(placesId)
            //Here the user info are field and setoption is set to merge.
            .set(placeDetails, SetOptions.merge())
            .addOnSuccessListener {
                activity.updateDetailsSuccess()
            }
            .addOnFailureListener{e ->
                Log.e(
                    activity.javaClass.simpleName,
                "Error while updating the details",
                    e
                )

            }

    }

    fun deleteFavDetails(fragment: ListFragment, placesId: String){
        mFireStore.collection(Constants.PLACES)
            .document(placesId)
            .delete()
            .addOnSuccessListener {
                fragment.deleteListSuccess()
            }
            .addOnFailureListener{ e ->
                Log.e(
                    fragment.javaClass.simpleName,
                "Error while deleting the place.",
                    e
                )

            }
    }

    fun getFavDetails(activity: DetailActivity, placesId: String) {

        // The collection name
        mFireStore.collection(Constants.PLACES)
            .document(placesId)
            .get() // Will get the document snapshots
            .addOnSuccessListener { document ->
                // Here we get the product details in the form of document
                Log.e(activity.javaClass.simpleName, document.toString())
                // Convert the snapshot to the object of data model class
                val place = document.toObject(PlaceModel::class.java)!!

                activity.favDetailsSuccess(place)
            }
            .addOnFailureListener { e ->

                Log.e(activity.javaClass.simpleName, "Error while getting the place details", e)
            }
    }
}