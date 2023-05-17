package com.example.instagramclone.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instagramclone.R
import com.example.instagramclone.adapter.FeedRecyclerAdapter
import com.example.instagramclone.databinding.ActivityFeedBinding
import com.example.instagramclone.model.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityFeedBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Posts>
    private lateinit var feedAdapter : FeedRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Posts>()
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun getData(){
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener{ value, error ->

            if (error != null){
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else {

                if (value != null){
                    if (!value.isEmpty){
                        val documents = value.documents

                        postArrayList.clear()

                        for (document in documents){
                            val comment = document.get("comment") as String?
                            val userEmail = document.get("userEmail") as String?
                            val downloadUrl = document.get("downloadUrl") as String?
                            val username = document.get("username") as String?
                            val profileImage = document.get("profilePictureUrl") as String?

                            val post = Posts(userEmail, comment, downloadUrl, username, profileImage)
                            postArrayList.add(post)

                        }

                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun cameraClicked(view: View){
        val intent = Intent(this, UploadActivity::class.java)
        startActivity(intent)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.sign_out){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    fun profileIconClicked(view : View){
        val intent = Intent(this@FeedActivity, ProfileActivity::class.java)
        startActivity(intent)
    }


}