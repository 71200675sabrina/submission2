package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.StoryRepository
import com.example.storyapp.remoteDao.StoryDatabase

object Injection {
    fun provideRepository(context: Context) : StoryRepository{
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database,apiService)
    }
}