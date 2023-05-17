package com.example.instagramclone.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.instagramclone.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    var selectedProfilePicture : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        val currentUser = auth.currentUser

        registerLauncher()


    }
    fun selectProfileImage (view: View){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Allow"){
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else{
                //request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else{
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
            //start activity for result
        }


    }


    fun registerClicked(view: View){

        val email = binding.emailTextRegister.text.toString()
        val password = binding.passwordTextRegister.text.toString()
        val username = binding.usernameTextRegister.text.toString()
        val profileImageName = "$email"
        val reference = storage.reference
        val userMap = hashMapOf<String, Any>()
        val profilePictureReference = reference.child("profileImages").child(profileImageName)

        if (selectedProfilePicture != null){
            profilePictureReference.putFile(selectedProfilePicture!!).addOnSuccessListener {
                val uploadProfilePictureReference = storage.reference.child("profileImages").child(profileImageName)
                uploadProfilePictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl =  it.toString()
                    userMap.put("profileImageUrl", downloadUrl)
                }
            }
        }

        if (email.equals("") || password.equals("") || username.equals("")){
            Toast.makeText(this, "Please enter email, username and password!", Toast.LENGTH_LONG).show()
        }
        else{

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

                userMap.put("username", username)
                userMap.put("email", email)
                userMap.put("password", password)
                firestore.collection("Users").document("$email").set(userMap)
                println("passed")
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@RegisterActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            } }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    selectedProfilePicture = intentFromResult.data
                    selectedProfilePicture?.let {
                        binding.profileImageView.setImageURI(it)
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else{
                //permission denied
                Toast.makeText(this@RegisterActivity, "Permission Needed", Toast.LENGTH_LONG).show()
            }
        }
    }
}
