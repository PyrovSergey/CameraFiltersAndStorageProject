package ru.pyrovsergey.cameraandfiltersproject.mainscreen

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import ru.pyrovsergey.cameraandfiltersproject.R
import ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter.Presenter

class PictureAdapter(private var listPaths: MutableList<String>, private var presenter: Presenter) :
    RecyclerView.Adapter<PictureAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listPaths.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImageStringPath = listPaths.get(position)
        Log.d("PictureAdapter", currentImageStringPath)
        holder.imageViewItem.setImageBitmap(presenter.getBitmapFromPath(currentImageStringPath))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewItem: ImageView = view.findViewById(R.id.imageItem)
    }

    fun updateAdapter(list: List<String>) {
//        val sizeCurrentList = listPaths.size
//        val sizeNewList = list.size
        listPaths = list as MutableList<String>
//        notifyItemRangeChanged(sizeCurrentList, sizeCurrentList + sizeNewList)
    }
}