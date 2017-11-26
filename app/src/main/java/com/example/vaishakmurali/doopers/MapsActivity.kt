package com.example.vaishakmurali.doopers

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadAnimals()
    }
    var my_request_Code = 1203
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if (ActivityCompat.checkSelfPermission(this,android.Manifest
                    .permission
                    .ACCESS_FINE_LOCATION)!=PackageManager
                    .PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),my_request_Code)
                return
            }
        }
        GetUserLocation()
    }



    @SuppressLint("MissingPermission")
    fun GetUserLocation(){
        Toast.makeText(this,"Please move from your location to start the game.",Toast.LENGTH_LONG).show()
        var myLocation = MyLocationListener()
        var locationManager=getSystemService(ContextWrapper.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,4,4f,myLocation)
        var initThread = myThread()
        initThread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            my_request_Code->{
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
//                    when user allows location permissions.
                    GetUserLocation()
                else
//                    displaying error message if user denied permission.
                    Toast.makeText(this,"We are unable to get your location",Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }
    var location:Location? = null
    inner class MyLocationListener:LocationListener{

        constructor(){
            location = Location("start")
            location?.longitude = 0.0
            location?.longitude = 0.0
        }

        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

    }

    var oldLocation:Location? = null
    inner class myThread:Thread{
        constructor():super(){
            oldLocation = Location("start")
            oldLocation?.longitude = 0.0
            oldLocation?.longitude = 0.0
        }

        override fun run() {
            super.run()
            while (true){
                try{
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    runOnUiThread(){
                        // Add a marker in Sydney and move the camera
                        mMap!!.clear()
                        val sydney = LatLng(location!!.latitude,location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("You")
                                .snippet("Here is your location & Your points : $player_points")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.player))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15f))

                        // Show aliens
                        for(i in 0..listOfAnimals.size-1){
                        var alien = listOfAnimals[i]
                        if(alien.IsReached == false){

                            var alienLocation = LatLng(alien.location!!.latitude!!,alien.location!!.longitude)
                            mMap.addMarker(MarkerOptions()
                                    .position(alienLocation)
                                    .title(alien.name!!)
                                    .snippet(alien.description!!)
                                    .icon(BitmapDescriptorFactory.fromResource(alien.image!!)))

                            if (location!!.distanceTo((alien.location))<2){
                                alien.IsReached = true
                                listOfAnimals[i] = alien
                                player_points += alien.points!!
                                Toast.makeText(applicationContext,"you have reached the location",Toast.LENGTH_LONG).show()
                            }

                            }
                        }
                    }

                    Thread.sleep(1000)
                }
                catch (ex:Exception){

                }
            }
        }
    }
    var player_points = 0
    var listOfAnimals = ArrayList<animals>()

    fun loadAnimals(){
        // loading all the aliens.
        listOfAnimals.add(animals("Doggosaur", R.drawable.doggo,"This alien looks like dog. Holds 50 points.",
                50,18.532964,73.834206,false))

        listOfAnimals.add(animals("Owlosaur", R.drawable.owl,"This alien looks like owl. Holds 110 points",
                150,18.525457,73.831927,false))

        listOfAnimals.add(animals("Crocosaur", R.drawable.croco,"This alien looks like crocodile. Holds 150 points",
                150,18.526271,73.829995,false))

        listOfAnimals.add(animals("Pandaosaur", R.drawable.pandu,"This alien looks like panda. It is very difficult to find him. Holds 650 points.",
                650,18.0,73.5,false))
    }

}
