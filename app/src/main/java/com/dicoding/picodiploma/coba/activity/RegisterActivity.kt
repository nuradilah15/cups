package com.dicoding.picodiploma.coba.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.dicoding.picodiploma.coba.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val logoImageView = findViewById<ImageView>(R.id.logo)
        logoImageView.setImageResource(R.drawable.logo5)

        btn_register.setOnClickListener {
            createAccount()
        }

        tv_to_sign.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }


    private fun createAccount() {
        val fullName : String = fullname_register.text.toString()
        val userName : String = username_register.text.toString()
        val email : String = email_register.text.toString()
        val password : String = password_register.text.toString()

        if (TextUtils.isEmpty(fullName) ||
            TextUtils.isEmpty(userName) ||
            TextUtils.isEmpty(email) ||
            TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tidak boleh ada yang kosong", Toast.LENGTH_LONG).show()
        } else {
            val progressDialog = ProgressDialog(this@RegisterActivity)
            progressDialog.setTitle("Login")
            progressDialog.setMessage("Please Wait....")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    saveUserInfo(email, fullName, userName, progressDialog)
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                    finish()
                } else {
                    progressDialog.dismiss()
                    mAuth.signOut()
                    Toast.makeText(this, "Register failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveUserInfo(email : String, fullname : String, username : String, progressDialog : ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname.toLowerCase(Locale.ROOT)
        userMap["username"] = username.toLowerCase(Locale.ROOT)
        userMap["email"] = email
        userMap["bio"] = "Hey Iam student at IDN Boarding School"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-app-256b6.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=ecebab92-ce4f-463c-a16a-a81fc34b0772"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful ){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account sudah dibuat", Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserID)
                        .child("Following").child(currentUserID)
                        .setValue(true)

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}