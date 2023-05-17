package com.example.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.adapter.ProfileRecyclerAdapter
import com.example.instagramclone.databinding.ActivityProfileBinding
import com.example.instagramclone.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


class ProfileActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var binding : ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var profilePostArraylist : ArrayList<Posts>
    private lateinit var profilePostAdapter: ProfileRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db = Firebase.firestore
        val auth = Firebase.auth
        val storage = Firebase.storage
        val reference = storage.reference

        //print profileImage
        db.collection("Users")
            .whereEqualTo("email", "${auth.currentUser!!.email!!}")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot!!) {
                    val username = document["username"] as String
                    binding.usernameText.text = username
                }
            }



        //print profile image
        val profilePictureReference = storage.reference.child("profileImages/${auth.currentUser!!.email!!}")
        profilePictureReference.downloadUrl.addOnSuccessListener {
            val profilePictureUrl = it.toString()
            Picasso.get().load(profilePictureUrl).into(binding.userImage)
        }

        //print post number text
        val collection = db.collection("Posts").whereEqualTo("userEmail", auth.currentUser!!.email!!)
        val countQuery = collection.count()
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener {
            val snapshot = it.result
            binding.postsNumber.text = snapshot.count.toString()
        }




        profilePostArraylist = ArrayList<Posts>()
        getProfileData()
        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(this)
        profilePostAdapter = ProfileRecyclerAdapter(profilePostArraylist)
        binding.recyclerViewProfile.adapter = profilePostAdapter



    }

    fun getProfileData(){
        val db = Firebase.firestore
        val auth = Firebase.auth
        println("valueload")
        db.collection("Posts").whereEqualTo("userEmail", auth.currentUser!!.email!!).orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (auth.currentUser != null){
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG)
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents

                            profilePostArraylist.clear()
                            for (document in documents) {
                                val comment = document.get("comment") as String?
                                val userEmail = document.get("userEmail") as String?
                                val downloadUrl = document.get("downloadUrl") as String?
                                val username = document.get("username") as String?
                                val profileImage = document.get("profilePictureUrl") as String?

                                val postProfile =
                                    Posts(userEmail, comment, downloadUrl, username, profileImage)
                                profilePostArraylist.add(postProfile)
                            }
                            profilePostAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        }
    }

    fun homeIconClicked (view: View){
        val intent = Intent(this@ProfileActivity, FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun cameraClicked (view : View){
        val intent = Intent(this@ProfileActivity, UploadActivity::class.java)
        startActivity(intent)
        finish()
    }


}