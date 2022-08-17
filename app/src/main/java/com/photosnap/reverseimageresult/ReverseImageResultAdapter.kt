package com.photosnap.reverseimageresult

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.photosnap.R
import com.photosnap.data.model.ServerResponse
import java.lang.Exception


class ReverseImagesRecyclerViewAdapter(
    val reverImageList: ArrayList<ServerResponse>,
    val viewModel: ReverseImageResultViewModel,
    context: Context
) :
    RecyclerView.Adapter<ReverseImagesRecyclerViewAdapter.MyViewHolder>() {

    var cxt: Context

    init {
        cxt = context
    }

    fun swapList(mreverImageList: ArrayList<ServerResponse>){
        reverImageList.clear()
        reverImageList.addAll(mreverImageList)
        this.notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_products, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reverImageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val currentItem = reverImageList.get(position)

            holder.floatingActionButton.setOnClickListener {
                viewModel.takeUrl(currentItem = currentItem)
            }

            holder.image.setOnClickListener {
                val imagePopup = ImagePopup(cxt)
                imagePopup.initiatePopupWithGlide(currentItem.image_link) // Load Image from Drawable
                imagePopup.viewPopup();
            }

            Glide.with(cxt)
                .load(currentItem.image_link.toUri())
                .centerCrop()
                .into(holder.image)
        }catch (e:Exception){

        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var floatingActionButton:ImageButton

        init {
            image = itemView.findViewById(R.id.image)
            floatingActionButton = itemView.findViewById(R.id.floatingActionButton)
        }
    }


}