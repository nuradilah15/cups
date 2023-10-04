package com.dicoding.picodiploma.coba.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.activity.RegisterActivity
import com.dicoding.picodiploma.coba.adapters.PostAdapter
import com.dicoding.picodiploma.coba.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.post_layout.*


class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val linearLayoutManager = LinearLayoutManager(context)
        view.home_recyclerView?.setHasFixedSize(true)
        view.home_recyclerView?.layoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        view.home_recyclerView.adapter = postAdapter

        checkFollowing()
        return view

    }



    private fun checkFollowing() {
        followingList = ArrayList()
        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                if (snapshot.exists()) {
                    (followingList as ArrayList<*>).clear()
                    for (i in snapshot.children) {
                        i.key.let {
                            (followingList as ArrayList<String>).add(it!!)
                        }
                        showData()
                    }
                }
            }
        })
    }

    private fun showData() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList?.clear()

                for (snapshot in dataSnapshot.children.reversed()) {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<*>)) {
                        if (post!!.publisher == id) {
                            postList!!.add(post)
                        }
                    }
                }
                postAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled
            }
        })
    }
}
