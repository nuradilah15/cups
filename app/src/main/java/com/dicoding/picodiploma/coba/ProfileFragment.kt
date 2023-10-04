package com.dicoding.picodiploma.coba

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import com.dicoding.picodiploma.coba.ProfileFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.activity.AccountSettingActivity
import com.dicoding.picodiploma.coba.activity.LoginActivity
import com.dicoding.picodiploma.coba.adapters.MyImageAdapter
import com.dicoding.picodiploma.coba.models.Post
import com.dicoding.picodiploma.coba.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting_app.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private var postListGrid: MutableList<Post>? = null
    private var myImagesAdapter: MyImageAdapter? = null
    private var followersCount: Int = 0
    private var followingCount: Int = 0
    private var postCount: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        val prefEdit = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        prefEdit?.putString("profileId", firebaseUser.uid)
        prefEdit?.apply()

        if (profileId == firebaseUser.uid) {
            view?.btn_edit_account?.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowerOrFollowingStatus()
        }

        val recyclerViewUploadImages: RecyclerView?
        recyclerViewUploadImages = view.findViewById(R.id.recyclerview_upload_picimage)
        recyclerViewUploadImages?.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUploadImages?.layoutManager = linearLayoutManager

        postListGrid = ArrayList()
        myImagesAdapter = context?.let { MyImageAdapter(it, postListGrid as ArrayList<Post>) }
        recyclerViewUploadImages?.adapter = myImagesAdapter

        getFollowers()
        getFollowings()
        userInfo()
        myPost()
        view.options_view.setOnClickListener {
            logOut()
        }
        view.btn_edit_account.setOnClickListener {
            val getButtonText = view?.btn_edit_account?.text.toString()
            when {
                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingActivity::class.java
                    )
                )

                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1)
                            .child("Following").child(profileId).setValue(true)

                        pushNotification()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1).setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1)
                            .child("Following").child(profileId).removeValue()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1).removeValue()
                    }
                }
            }

        }
        return view
    }


    private fun pushNotification() {
        val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(profileId)

        val notifyMap = HashMap<String, Any>()
        notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "Started following you"
        notifyMap["postid"] =""
        notifyMap["ispost"] = true

        ref.push().setValue(notifyMap)
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
    }

    private fun checkFollowerOrFollowingStatus() {

        firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1)
                .child("Following")
        }.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child(profileId).exists()) {
                    view?.btn_edit_account?.text = "Following"
                } else {
                    view?.btn_edit_account?.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                followersCount = p0.childrenCount.toInt()
                view?.txt_totalFollowers?.text = followersCount.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun getFollowings() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                followingCount = p0.childrenCount.toInt()
                view?.txt_totalFollowing?.text = followingCount.toString()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun myPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    postCount = 0
                    (postListGrid as ArrayList<Post>).clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        if (post?.publisher.equals(profileId)) {
                            (postListGrid as ArrayList<Post>).add(post!!)
                            postCount++
                        }
                    }
                    Collections.reverse(postListGrid)
                    myImagesAdapter?.notifyDataSetChanged()
                    view?.txt_totalPost?.text = postCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)
                    Picasso.get().load(user?.image).placeholder(R.drawable.profile)
                        .into(view?.profile_image_gbr_frag)
                    view?.profile_fragment_username?.text = user?.username
                    view?.txt_full_namaProfile?.text = user?.fullname
                    view?.txt_bio_profile?.text = user?.bio
                    view?.txt_totalFollowers?.text = followersCount.toString()
                    view?.txt_totalFollowing?.text = followingCount.toString()
                    view?.txt_totalPost?.text = postCount.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



}