package org.wit.favouriteplaceapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add.view.*
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.models.PlaceModel
import kotlinx.android.synthetic.main.fav_place_view.view.*
import org.wit.favouriteplaceapp.ui.activities.AddActivity
import org.wit.favouriteplaceapp.ui.activities.DetailActivity
import org.wit.favouriteplaceapp.ui.activities.fragments.ListFragment
import org.wit.favouriteplaceapp.utils.Constants
import org.wit.favouriteplaceapp.utils.GlideLoader

open class PlaceAdapter(
    private val context: Context,
    private var list: ArrayList<PlaceModel>

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    //Inflates the item views which is designed in xml layout fil
    // create a new
    // {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.fav_place_view,
                parent,
                false
            )
        )
    }
    //This is work around for a onclicklistener because we cannot add a onclicklistener to a adapter
    // we bind the onclicklistener to this.onclicklistener when passing through this function.
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    // Binds each item in the ArrayList to a view
    // called when RecyclerView needs a new ViewHolder of the given type to represent
    //an item
    //this new ViewHolder should be constructed with a new View that can represent the items
    //of the given type u can either create a new View manually or inflate it from an XML
    //layout file
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            //setting the image uri as the ImageFavDisplay, we use Glider for an easy way to load an image for our Image display
            //https://bumptech.github.io/glide/doc/getting-started.html#basic-usage
            //https://github.com/bumptech/glide
            GlideLoader(context).loadFavPlaceImage(model.image!!, holder.itemView.ImageFavDisplay)
            holder.itemView.titleDis.text = model.title
            holder.itemView.descriptionDis.text = model.description

            //Checking if onclicklistener is null, if not then we check which element/favourite place was
            //clicked (position of the recyclerview), then were passing the placemodel which is then populating the
            //relevant info....
            holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PLACE_ID, model.place_id)
                context.startActivity(intent)
                }
            }
        }


    //with this function I want to notify our adapter that the specific element I would like to make my change on
    fun editItem(activity: ListFragment, position: Int){
        //we send them to add activity
        val intent = Intent(context, AddActivity::class.java)
        //putting extra info into the intent with the list position of the specific element....
        intent.putExtra(Constants.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivity(intent)
        //we make sure the adapter is notified about the changes made to the element...
        notifyItemChanged(position)
    }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    //passing info into onclick listener
    interface OnClickListener {
        fun onClick(position: Int, model: PlaceModel)
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

