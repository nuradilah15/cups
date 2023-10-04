package com.dicoding.picodiploma.coba.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import com.dicoding.picodiploma.coba.fragment.NotifFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.adapters.NotifAdapter
import com.dicoding.picodiploma.coba.models.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class NotifFragment : Fragment() {
    private var notificationAdapter : NotifAdapter? = null
    private var notificationList : MutableList<Notification>? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notif,container,false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val recyclerView: RecyclerView = view.findViewById(R.id.notification_recyclerView)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager

        notificationList= ArrayList()
        notificationAdapter= context?.let { NotifAdapter(it,notificationList as ArrayList<Notification>) }
        recyclerView.adapter = notificationAdapter

        readNotification()
        return view
    }


    private fun readNotification() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Notification"). child(firebaseUser!!.uid)
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList?.clear()
                for (snapshot in snapshot.children)
                {
                    val notification : Notification? = snapshot.getValue(Notification::class.java)
                    notificationList!!.add(notification!!)
                }
                notificationList!!.reverse()
                notificationAdapter?.notifyDataSetChanged()
            }
        })
    }

}