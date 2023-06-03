package com.example.storyapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ItemDetailBinding
import com.example.storyapp.model.Story
import com.example.storyapp.remoteDao.StoryListItem
import com.example.storyapp.view.StoryDetailActivity

class ListAdapter : PagingDataAdapter<StoryListItem, ListAdapter.ListViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ListViewHolder(private val binding: ItemDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data : StoryListItem) {
            val photo = data.photoUrl
            Glide.with(itemView.context).load(photo).placeholder(R.drawable.ex_image).into(binding.ivstory)
            binding.tvstory.text = data.name
            

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.STORY_KEY, data)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryListItem>(){
            override fun areItemsTheSame(oldItem: StoryListItem, newItem: StoryListItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryListItem,
                newItem: StoryListItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}