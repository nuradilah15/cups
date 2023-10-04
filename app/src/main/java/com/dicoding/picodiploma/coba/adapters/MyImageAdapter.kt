package com.dicoding.picodiploma.coba.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.R
import com.squareup.picasso.Picasso
import com.dicoding.picodiploma.coba.models.Post

class MyImageAdapter(private val mContext: Context, mPost: List<Post>)
    : RecyclerView.Adapter<MyImageAdapter.ViewHolder?>(){

    private var mPost: List<Post>? = null

    init {
        this.mPost = mPost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mPost!!.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]

        Picasso.get().load(post.postImage).into(holder.postImage)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var postImage: ImageView = itemView.findViewById(R.id.post_image_grid)
    }
}