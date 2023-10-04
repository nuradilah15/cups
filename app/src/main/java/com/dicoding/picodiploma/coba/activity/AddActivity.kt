package com.dicoding.picodiploma.coba.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.dicoding.picodiploma.coba.R
import com.dicoding.picodiploma.coba.fragment.HomeFragment

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add.*
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePictureRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        SetUpView()

        storageProfilePictureRef = FirebaseStorage.getInstance().reference.child("Post Picture")

        tv_upload.setOnClickListener {
            uploadContent()
        }
        CropImage.activity()
            .setAspectRatio(1,1)
            .start(this@AddActivity)

        tv_cancel.setOnClickListener {
            startActivity(Intent(this, HomeFragment::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            image_post.setImageURI(imageUri)
        }
    }

    private fun SetUpView() {
        @Suppress("Deprecation")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun uploadContent() {
        when (imageUri) {
            null -> Toast.makeText(this, "Gambar tidak boleh kosong", Toast.LENGTH_LONG)
            else -> {
                val fileRef =
                    storageProfilePictureRef?.child(System.currentTimeMillis().toString() + ".jpg")
                val uploadTask: StorageTask<*>
                val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                val postId = ref.push().key

                // Membuat dialog untuk menampilkan progress upload
                val progressDialog = AlertDialog.Builder(this)
                    .setTitle("Uploading")
                    .setMessage("Please wait...")
                    .setCancelable(false)
                    .create()
                progressDialog.show()

                uploadTask = fileRef!!.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()
                        val postMap = HashMap<String, Any>()
                        postMap ["postId"] = postId!!
                        postMap ["description"] = deskripsi_post.text.toString()
                        postMap ["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap ["postImage"] = myUrl
                        ref.child(postId).setValue(postMap)
                        Toast.makeText(this,"Content uploaded", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this,"Content failed to upload", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}