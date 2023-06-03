package com.example.storyapp.model.story

import android.os.Parcelable
import com.example.storyapp.remoteDao.StoryListItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryPageResponse(
    @field:SerializedName("error")
    val error : Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("listStory")
    val listStory:ArrayList<StoryListItem>
) : Parcelable