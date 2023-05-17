package com.example.instagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.instagramclone.databinding.RecyclerRowBinding
import com.example.instagramclone.model.Posts
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter( private val postList : ArrayList<Posts> ) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        println(postList)
        holder.binding.recyclerUsernameText.text = postList.get(position).username
        holder.binding.recyclerCommentText.text = "${postList.get(position).username}: ${postList.get(position).comment}"
        Picasso.get().load(postList.get(position).profileImage).into(holder.binding.recyclerUserImage)
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}