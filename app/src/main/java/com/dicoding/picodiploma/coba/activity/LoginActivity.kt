package com.dicoding.picodiploma.coba.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val logoImageView = findViewById<ImageView>(R.id.logo)
        logoImageView.setImageResource(R.drawable.logo5)
        btn_login.setOnClickListener {
            loginUser()
        }

        tv_to_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    private fun loginUser() {
        val email : String = email_login.text.toString()
        val password : String = password_login.text.toString()
        when {
            TextUtils.isEmpty(email) ->
                Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please Wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        progressDialog.dismiss()
                        startActivity(Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    } else {
                        progressDialog.dismiss()
                        mAuth.signOut()
                        Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
        super.onStart()
    }

}