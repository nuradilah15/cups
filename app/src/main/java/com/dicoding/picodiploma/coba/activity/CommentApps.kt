package com.dicoding.picodiploma.coba.activity

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.adapters.CommentsAdapter
import com.dicoding.picodiploma.coba.adapters.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.dicoding.picodiploma.coba.models.Comment
import com.dicoding.picodiploma.coba.models.Post
import com.dicoding.picodiploma.coba.models.User
import kotlinx.android.synthetic.main.activity_comment_apps.*

class CommentApps : AppCompatActivity() {
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_apps)


        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        commentList = ArrayList()
        commentAdapter = CommentsAdapter(this, commentList as ArrayList<Comment>)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerview_comments.layoutManager = linearLayoutManager
        recyclerview_comments.adapter = commentAdapter

        getPostComment()
        getUserInfo()
        txtpost_comments.setOnClickListener {
            when (add_comment.text.toString()){
                "" -> Toast.makeText(this, "Tidak boleh kosong", Toast.LENGTH_LONG).show()
                else -> postComment()
            }
        }
    }


    private fun getPostComment() {
        val refComment = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        refComment.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    commentList?.clear()
                    for (i in snapshot.children) {
                        val comments = i.getValue(Comment::class.java)
                        commentList?.add(comments!!)
                    }
                    commentAdapter?.notifyDataSetChanged()
                }
            }

        })
    }

    private fun getUserInfo() {
        val refImage = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refImage.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val refImage = snapshot.getValue(User::class.java)
                    Picasso.get().load(refImage?.image).placeholder(R.drawable.profile).into(profile_image_comment)
                }
            }
        })
    }

    private fun postComment() {
        val refComment = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid
        refComment.push().setValue(commentsMap)
        add_comment.text.clear()
    }
}
