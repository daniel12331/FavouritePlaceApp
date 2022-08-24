package org.wit.favouriteplaceapp.ui.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.squareup.okhttp.internal.Internal.logger
import kotlinx.android.synthetic.main.fragment_list.*
import org.wit.favouriteplaceapp.R
import org.wit.favouriteplaceapp.adapter.PlaceAdapter
import org.wit.favouriteplaceapp.databinding.FragmentListBinding
import org.wit.favouriteplaceapp.firestore.FirestoreClass
import org.wit.favouriteplaceapp.models.PlaceModel
import org.wit.favouriteplaceapp.ui.activities.AddActivity
import org.wit.favouriteplaceapp.ui.activities.DetailActivity
import org.wit.favouriteplaceapp.utils.SwipeToDelete
import org.wit.favouriteplaceapp.utils.SwipeToEdit

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    fun successFavListFromFireStore(favPlaceList: ArrayList<PlaceModel> ){

        //If there is items, we make the recycler visible and remove the no fav place text..
        if(favPlaceList.size > 0){
            rv_favPlaceList.visibility = View.VISIBLE
            NoFavPlaceText.visibility = View.GONE

            rv_favPlaceList.layoutManager = LinearLayoutManager(activity)
            rv_favPlaceList.setHasFixedSize(true)

            val adapterPlaces = PlaceAdapter(requireActivity(), favPlaceList)
            rv_favPlaceList.adapter = adapterPlaces

            adapterPlaces.setOnClickListener(object : PlaceAdapter.OnClickListener{
                override fun onClick(position: Int, model: PlaceModel) {
                    val intent = Intent(activity, DetailActivity::class.java)
                    startActivity(intent)
                   }

            })

            val editSwipeHandler = object: SwipeToEdit(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = rv_favPlaceList.adapter as PlaceAdapter
                    adapter.editItem(this@ListFragment,
                    viewHolder.adapterPosition)
                }
            }

            //we assign the editswiper handler
            val editItemHelper = ItemTouchHelper(editSwipeHandler)
            //we then attach editItemHelper and attach it to the recycler view
            editItemHelper.attachToRecyclerView(rv_favPlaceList)

            val deleteSwipeHandler= object : SwipeToDelete(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    FirestoreClass().deleteFavDetails(this@ListFragment, favPlaceList[viewHolder.adapterPosition].place_id!!)
                }
            }
            val deleteItemHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemHelper.attachToRecyclerView(rv_favPlaceList)
        }
        else {
            rv_favPlaceList.visibility = View.GONE
            NoFavPlaceText.visibility = View.VISIBLE
        }

    }
    fun deleteListSuccess(){
        logger.info("Deletion Successful")
        getListFromFireStore()
    }


    private fun getListFromFireStore(){
        FirestoreClass().getFavPlace(this)
    }

    //calling the getList from firestore when we onresume this fragment...
    override fun onResume() {
        super.onResume()
        getListFromFireStore()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fav_place, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.add_fav_place) {
            startActivity(Intent(activity, AddActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}