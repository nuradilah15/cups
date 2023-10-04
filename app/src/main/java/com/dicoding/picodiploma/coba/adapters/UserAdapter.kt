package com.dicoding.picodiploma.coba.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.ProfileFragment
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext: Context, private val mUser: List<User>,
                   private var isFragment: Boolean = false): RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //memanggil layout user_item_layout
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNametxtView.text = user.username
        holder.fullNametxtView.text = user.fullname
        Picasso.get().load(user.image).error(R.drawable.close).placeholder(R.drawable.profile).into(holder.userProfileImage)

        //method untuk mengetahui status user
        user.uid?.let { cekFollowingStatus(it,holder.followButton) }

        //Intent ke Fragment User
        holder.itemView.setOnClickListener {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow")
            {
                firebaseUser?.uid.let { it1 ->
                    user.uid?.let { it2 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(it2)
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(user.uid!!)
                                            .child("Followers").child(it1.toString())
                                            .setValue(true)
                                    }
                                    pushNotification(user.uid)
                                }
                            }
                    }
                }
            }
            else
            {
                firebaseUser?.uid.let { it1 ->
                    user.uid?.let { it2 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(it2)
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(user.uid!!)
                                            .child("Followers").child(it1.toString())
                                            .removeValue()
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private fun pushNotification(userid: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(userid)

        val notifyMap = HashMap<String, Any>()
        notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "started following you"
        notifyMap["postid"] = ""
        notifyMap["ispost"] = false

        ref.push().setValue(notifyMap)
    }

    class ViewHolder (@NonNull itemView: View) :
        RecyclerView.ViewHolder(itemView){
        //mengenalkan widget yang di la
        var userNametxtView: TextView = itemView.findViewById(R.id.user_name_search)
        var fullNametxtView: TextView = itemView.findViewById(R.id.user_fullname_search)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followButton: Button = itemView.findViewById(R.id.follow_btnsearh)

    }

    private fun cekFollowingStatus(uid: String, followButton: Button)
    {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(uid).exists())
                {
                    followButton.text = "Following"
                }
                else
                {
                    followButton.text = "Follow"
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}