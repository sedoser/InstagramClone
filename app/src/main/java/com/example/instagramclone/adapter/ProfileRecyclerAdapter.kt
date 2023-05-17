package com.example.instagramclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.databinding.RecyclerRowProfileBinding
import com.example.instagramclone.model.Posts
import com.squareup.picasso.Picasso

class ProfileRecyclerAdapter( private val postList : ArrayList<Posts>) : RecyclerView.Adapter<ProfileRecyclerAdapter.ProfilePostHolder>() {

    class ProfilePostHolder(val binding : RecyclerRowProfileBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostHolder {
        val binding = RecyclerRowProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfilePostHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfilePostHolder, position: Int) {
        //Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.photoImageView)
        holder.binding.recyclerUsernameText.text = postList.get(position).username
        holder.binding.recyclerCommentText.text = "${postList.get(position).username}: ${postList.get(position).comment}"
        Picasso.get().load(postList.get(position).profileImage).into(holder.binding.recyclerUserImage)
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}