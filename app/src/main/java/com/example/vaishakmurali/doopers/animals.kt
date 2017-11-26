package com.example.vaishakmurali.doopers

import android.location.Location

/**
 * Created by vaishakmurali on 23/11/17.
 */

class animals(){
    var name:String? = null
    var image:Int? = null
    var description:String? = null
    var points:Int? = null
    var location:Location? = null
    var IsReached:Boolean? = false

    constructor(name:String,image:Int,description:String,points:Int,latitude:Double,longitude:Double,IsReached:Boolean) : this() {
        this.name = name
        this.image = image
        this.description = description
        this.points = points
        this.location = Location(name)
        this.location!!.latitude = latitude
        this.location!!.longitude = longitude
        this.IsReached = false
    }
}
