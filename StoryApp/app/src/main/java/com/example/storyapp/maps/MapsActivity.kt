package com.example.storyapp.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.view.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var userPreference: UserPreference
    private lateinit var user : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        user = userPreference.getDataLogin()

        viewModel = ViewModelProvider(this@MapsActivity)[MainViewModel::class.java]
        viewModel.fetchStory(user.token)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        viewModel.listUser.observe(this@MapsActivity){
            val startPoint = LatLng(-7.7956, 110.3695)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 0f))
            mMap.apply {
                for (i in it){
                    val userLoc = LatLng(i.lat,i.lon)
                    addMarker(
                        MarkerOptions()
                            .position(userLoc)
                            .title(i.name)
                            .snippet(i.description)
                    )
                }
                setOnMarkerClickListener {
                    animateCamera(CameraUpdateFactory.newLatLngZoom(it.position,20f))
                    it.showInfoWindow()
                    true
                }
            }
        }


        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}