package com.dicoding.picodiploma.coba.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.ProfileFragment
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.fragment.HomeFragment
import com.dicoding.picodiploma.coba.models.Notification
import com.dicoding.picodiploma.coba.models.Post
import com.dicoding.picodiploma.coba.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotifAdapter(
    private var mContext: Context,
    private var mNotification: List<Notification>): RecyclerView.Adapter<NotifAdapter.ViewHolder>() {

        class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
            var username : TextView = itemView.findViewById(R.id.notification_username)
            var notifyText : TextView = itemView.findViewById(R.id.notification_text)
            var profileImage: CircleImageView = itemView.findViewById(R.id.notification_image_profile)
            var postImg: ImageView = itemView.findViewById(R.id.posted_image)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotification[position]
        holder.notifyText.text = notification.getText()

    }




    override fun getItemCount(): Int {
       return mNotification.size
    }
}