package com.dicoding.picodiploma.coba.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.dicoding.picodiploma.coba.models.Comment
import com.dicoding.picodiploma.coba.models.User

import de.hdodenhof.circleimageview.CircleImageView

class CommentsAdapter(private val mContext : Context, private var mComment : MutableList<Comment>)
    : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = mComment. size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment[position]
        holder.commentTV.text = comment.comment

        getUserInfo(holder.imageProfileComment, holder.userNameCommentTV, comment.publisher)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var imageProfileComment: CircleImageView = itemView.findViewById(R.id.user_profile_image_comment)
        var userNameCommentTV: TextView = itemView.findViewById(R.id.user_name_comment)
        var commentTV: TextView = itemView.findViewById(R.id.comment_comment)
    }


    private fun getUserInfo(imageProfileComment: CircleImageView, userNameCommentTV: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val image = snapshot.getValue(User::class.java)
                    Picasso.get().load(image?.image).placeholder(R.drawable.profile).into(imageProfileComment)
                    userNameCommentTV.text = image!!.username
                }
            }
        })
    }
}