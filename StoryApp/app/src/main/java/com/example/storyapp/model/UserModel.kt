package com.example.storyapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (
    val name : String,
    val userId : String,
    val token : String
    ): Parcelable

@Parcelize
data class Story(
    val id : String,
    val name : String,
    val description : String,
    val photoUrl : String,
    val createdAt : String,
    val lat: Double,
    val lon : Double
):Parcelable