package com.example.storyapp.model

import android.content.Context


class UserPreference(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getDataLogin(): UserModel{
        return UserModel(
            preferences.getString(NAME_KEY, "").toString(),
            preferences.getString(USERID_KEY, "").toString(),
            preferences.getString(TOKEN_KEY, "").toString(),
        )
    }

    fun setLogin(userModel : UserModel){
        val editor = preferences.edit()
        editor.putString(NAME_KEY, userModel.name)
        editor.putString(USERID_KEY, userModel.userId)
        editor.putString(TOKEN_KEY, userModel.token)
        editor.apply()
        }


    fun logout(){
        val editor = preferences.edit()
        editor.remove(NAME_KEY)
        editor.remove(USERID_KEY)
        editor.remove(TOKEN_KEY)
        editor.apply()

    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreference? = null

        const val PREFS_NAME = "login_pref"
        const val NAME_KEY = "NAME"
        const val TOKEN_KEY = "TOKEN"
        const val USERID_KEY = "USERID"


        fun getInstance(context: Context) : UserPreference {
            if (INSTANCE == null) {
                INSTANCE = UserPreference(context)
            }
            return INSTANCE as UserPreference
        }
    }

}
