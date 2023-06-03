package com.example.storyapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.di.Injection
import com.example.storyapp.model.StoryRepository
import com.example.storyapp.remoteDao.StoryListItem

class StoryViewModel (storyRepository: StoryRepository): ViewModel(){

    val story : LiveData<PagingData<StoryListItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw java.lang.IllegalArgumentException("No ViewModel class")
    }
}