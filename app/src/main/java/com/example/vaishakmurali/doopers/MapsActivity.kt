package com.example.vaishakmurali.doopers

/**
 * Created by vaishakmurali on 23/11/17.
 */

import android.annotation.SuppressLint
import android.content.Context
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
import kotlin.collections.ArrayList


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
                    // To check if the the location has been changed.
                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }
                    oldLocation=location
                    // Note that thread can't run on UI so we have to use the function runOnUiThread.
                    runOnUiThread(){
                        // Clearing the map and adding player's marker on the map.
                        mMap!!.clear()
                        val playerLocation = LatLng(location!!.latitude,location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(playerLocation)
                                .title("You")
                                .snippet("Here is your location & Your points : $player_points")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.player))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(playerLocation,15f))

                        // Show aliens
                        for(i in 0 until newAliens.size){
                        val alien = newAliens[i]
                        if(!alien.IsReached){

                            // Setting location of the alien.
                            val alienLocation = Location(Context.LOCATION_SERVICE)
                            // Setting latitude and longitude
                            alienLocation.latitude = alien.latitude
                            alienLocation.longitude = alien.longitude

                            // Setting alien marker on map.
                            mMap.addMarker(MarkerOptions()
                                    .position(LatLng(alienLocation.latitude,alienLocation.longitude))
                                    .title(alien.name!!)
                                    .snippet(alien.description!!)
                                    .icon(BitmapDescriptorFactory.fromResource(alien.image!!)))

                            // When player reaches the alien's location
                            if (location!!.distanceTo((alienLocation))<2){
                                alien.IsReached = true
                                newAliens[i] = alien
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
    // variable to Count the player's points
    var player_points: Int = 0

    val newAliens = ArrayList<Animals>()
    fun loadAnimals(){

        //TODO  Put random decimal input for longitude and latitude on alien's location
        // Create new aliens using the animal data class.

        newAliens.add(Animals ("Doggosaur", R.drawable.doggo,"This alien looks like a dog. Holds 50 points.",
                50,18.557346,73.809171,false))

        newAliens.add(Animals("Owlosaur", R.drawable.owl,"This alien looks like an owl. Holds 110 points",
                150,18.525457,73.831927,false))

        newAliens.add(Animals("Crocosaur", R.drawable.croco,"This alien looks like a crocodile. Holds 150 points",
                150,18.526271,73.829995,false))

        newAliens.add(Animals("Pandaosaur", R.drawable.pandu,"This alien looks like a panda. It is very difficult to find him. Holds 650 points.",
                650,18.0,73.5,false))
    }

}
