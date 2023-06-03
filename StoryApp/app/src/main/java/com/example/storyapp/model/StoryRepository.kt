package com.example.storyapp.model

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.storyapp.api.ApiService
import com.example.storyapp.remoteDao.StoryDatabase
import com.example.storyapp.remoteDao.StoryListItem
import com.example.storyapp.remoteDao.StoryRemoteMediator

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory() : LiveData<PagingData<StoryListItem>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(storyDatabase,apiService),
            pagingSourceFactory = {
                storyDatabase.storiesRemote().getAllStory()
            }
        ).liveData
    }
}