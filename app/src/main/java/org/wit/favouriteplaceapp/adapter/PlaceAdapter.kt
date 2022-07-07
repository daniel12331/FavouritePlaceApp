package org.wit.favouriteplaceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.models.PlaceModel
import kotlinx.android.synthetic.main.fav_place_view.view.*



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
            holder.itemView.titleDis.text = model.title
            holder.itemView.descriptionDis.text = model.description

            //Checking if onclicklistener is null, if not then we check which element/favourite place was
            //clicked (position of the recyclerview), then were passing the placemodel which is then populating the
            //relevant info....
            holder.itemView.setOnClickListener{
                if(onClickListener !=null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
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