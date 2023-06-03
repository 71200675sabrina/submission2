package com.example.storyapp.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.*
import com.example.storyapp.adapter.ListAdapter
import com.example.storyapp.adapter.LoadAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.maps.MapsActivity
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.upload.UploadActivity



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private lateinit var viewModel: MainViewModel
    private lateinit var userModel: UserModel
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val storyViewModel : StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        userPreference = UserPreference(this)
        if (userPreference.getDataLogin().token.isEmpty() && Token.token == null){
            finish()
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
        showExistingPreference()

        val manager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = manager
        setUserStory()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        val logoutBtn = menu?.findItem(R.id.logout)
        val cameraBtn = menu?.findItem(R.id.upStory)
        val mapsBtn = menu?.findItem(R.id.maps)

        logoutBtn?.setOnMenuItemClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Anda yakin ingin keluar?")
            builder.setPositiveButton("Yes") {_,_->
                userPreference.logout()
                Token.token = null
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            builder.setNegativeButton("Cancel", null)
            val dialog = builder.create()
            dialog.show()
            true
        }

        cameraBtn?.setOnMenuItemClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
            true
        }

        mapsBtn?.setOnMenuItemClickListener{
            val intent = Intent(this,MapsActivity::class.java)
            startActivity(intent)
            true
        }


        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }

    private fun showExistingPreference(){
        userModel = userPreference.getDataLogin()
        Token.token = userModel.token
        populateView(userModel)
    }

    private fun populateView(userModel: UserModel){
        supportActionBar?.title = userModel.name
        viewModel.fetchStory(userModel.token)
    }

    private fun setUserStory() {
        val adapter = ListAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadAdapter{
                adapter.retry()
            }
        )
        storyViewModel.story.observe(this){
            if (it != null){
                Log.d("Main Activity", "Data received")
                adapter.submitData(lifecycle,it)
                Log.d("MainActivity","Data submitted to adapter")
            }
        }

    }
}