package com.example.storyapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityStoryDetailBinding
import com.example.storyapp.model.Story


class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding
    companion object{
        const val STORY_KEY = "story_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail Story"
        val detailStory = intent.getParcelableExtra<Story>(STORY_KEY)

        if (detailStory != null){
            val imageUrl = detailStory.photoUrl
            Glide.with(this).load(imageUrl).into(binding.ivstory)
            binding.tvusername.text = detailStory.name
            binding.tvdescription.text = detailStory.description
            binding.tvDate.text = detailStory.createdAt

        }
    }
}