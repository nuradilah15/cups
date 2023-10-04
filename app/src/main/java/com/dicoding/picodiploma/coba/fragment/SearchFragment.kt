package com.dicoding.picodiploma.coba.fragment

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.dicoding.picodiploma.coba.fragment.SearchFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.adapters.UserAdapter
import com.dicoding.picodiploma.coba.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var myUser : MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        myUser = ArrayList()
        userAdapter = context?.let {
            UserAdapter(it, myUser as ArrayList<User>, true)
        }
        recyclerView?.adapter = userAdapter

        view.search_editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getUsers()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                getUsers()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.search_editText.toString() != "") {
                    recyclerView?.visibility = View.VISIBLE
                    getUsers()
                    searchUser(s.toString().toLowerCase(Locale.ROOT))
                }
            }
        })
        return view
    }


    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullname")
            .startAt(input).endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                myUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        myUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun getUsers() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (view?.search_editText?.toString() == "") {
                    myUser?.clear()
                    for (snapshot in snapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            myUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }
        })
    }
}