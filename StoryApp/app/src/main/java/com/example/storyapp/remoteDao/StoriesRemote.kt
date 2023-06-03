package com.example.storyapp.remoteDao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StoriesRemote {
    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryListItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story : ArrayList<StoryListItem>)

    @Query("DELETE FROM story")
    suspend fun delteAll()


}