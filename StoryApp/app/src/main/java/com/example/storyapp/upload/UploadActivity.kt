package com.example.storyapp.upload

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.model.UserModel
import com.example.storyapp.model.UserPreference
import com.example.storyapp.model.story.UploadStoryResponse
import com.example.storyapp.view.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var user : UserModel
    private lateinit var userPreference: UserPreference
    private lateinit var maps: GoogleMap
    private lateinit var marker: Marker

    private var nowLatitude: Double = -7.7956
    private var nowLongtitude: Double = 110.3695

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,"Tidak mendapatka permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        userPreference = UserPreference(this)
        showExistingPreference()
        user = userPreference.getDataLogin()
        Log.d("Token", user.token)


    }

    private fun startGallery(){
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also{
            val photoURI : Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted : Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun uploadImage(){
        if (getFile != null){
            val file = reduceFileImage(getFile as File)
            val description = binding.editdesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val token = "Bearer " + user.token
            val apiService = ApiConfig.getApiService()
            val lat = nowLatitude
            val long = nowLongtitude
            val uploadImageRequest = apiService.uploadImage(token,imageMultipart,description,lat,long)
            uploadImageRequest.enqueue(object : Callback<UploadStoryResponse> {
                override fun onResponse(
                    call: Call<UploadStoryResponse>,
                    response: Response<UploadStoryResponse>
                ) {
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error){
                            val builder = AlertDialog.Builder(this@UploadActivity)
                            builder.setTitle("Story berhasil di upload")
                            builder.setMessage("Story telah di update")
                            builder.setPositiveButton("Selesai"){_,_->
                                val intent = Intent (this@UploadActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            builder.show()
                        }else{
                            Toast.makeText(this@UploadActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UploadActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                    Toast.makeText(this@UploadActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })


        }

    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun showExistingPreference(){
        user = userPreference.getDataLogin()

    }

    override fun onMapReady(p0: GoogleMap) {
        maps = p0
        getMyLocation()
        maps.uiSettings.isZoomControlsEnabled = true

        val Locationstart = LatLng(-7.7956,110.3695)
        Toast.makeText(this@UploadActivity, "Lokasi saat ini Lat $nowLatitude & Lokasi $nowLongtitude", Toast.LENGTH_LONG).show()
        marker = maps.addMarker(
            MarkerOptions()
                .position(Locationstart)
                .title("Starting Point")
                .snippet("Please Determine Where is your Location!!")
                .draggable(true)
        ) as Marker
        maps.animateCamera(CameraUpdateFactory.newLatLngZoom(Locationstart, 5f))
        maps.setOnMapClickListener {
            maps.clear()
            if (this::marker.isInitialized){
                marker.remove()
            }
            marker = maps.addMarker(
                MarkerOptions()
                    .position(it)
                    .draggable(true)
                    .title(user.name)
            ) as Marker
            Toast.makeText(this@UploadActivity,"Lokasi saat ini Lat ${it.latitude} & Lokasi ${it.longitude}", Toast.LENGTH_LONG).show()
        }
        maps.setOnMyLocationButtonClickListener {
            updateCurrentLocation()
            Toast.makeText(this@UploadActivity,"Lokasi Lat $nowLatitude & Lokasi $nowLongtitude",Toast.LENGTH_LONG).show()
            true
        }
    }

    private fun getMyLocation(){
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
        ){
            maps.isMyLocationEnabled = true
            updateCurrentLocation()
        } else {
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).toString()
            )
        }
    }

    private fun updateCurrentLocation(){
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        ){
            maps.clear()
            if (this::marker.isInitialized){
                marker.remove()
            }

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    if (location != null){
                        val latLng = LatLng(location.latitude, location.longitude)
                        val cameraupdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                        maps.animateCamera(cameraupdate)
                        marker = maps.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .draggable(true)
                                .title(user.name)
                        ) as Marker

                        nowLongtitude = latLng.longitude
                        nowLatitude = latLng.latitude
                    }
                }
        }
    }
}