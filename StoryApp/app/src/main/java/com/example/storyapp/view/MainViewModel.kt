package com.example.storyapp.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.Story
import com.example.storyapp.model.story.AllStoriesResponse
import retrofit2.Call
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _listStory = MutableLiveData<List<Story>>()
    val listUser: LiveData<List<Story>> = _listStory

    internal fun fetchStory(token: String?){
        val client = ApiConfig.getApiService().getStories("Bearer $token")
        client.enqueue(object : retrofit2.Callback<AllStoriesResponse>{
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null){
                        val arrayStory = ArrayList<Story>()
                        if (!responseBody.error){
                            for(i in responseBody.listStory.indices){
                                val story = Story(responseBody.listStory[i].id,
                                responseBody.listStory[i].name,responseBody.listStory[i].description,
                                responseBody.listStory[i].photoUrl,responseBody.listStory[i].createdAt,
                                    responseBody.listStory[i].lat as Double,
                                    responseBody.listStory[i].lon as Double
                                )

                                arrayStory.add(story)
                            }
                            _listStory.value = arrayStory
                        }
                    }
                }else{
                    Log.e("Response Failed","MainActivity On Failure: ${response.message()} and $token")
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                Log.e("On Failure", "On Failure Message : $t")
            }
        })
    }
}