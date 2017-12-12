package com.example.vaishakmurali.doopers

import android.location.Location

/**
 * Created by vaishakmurali on 23/11/17.
 */
// Creating a data class for crearing animal like aliens 
data class Animals(val name:String, val image:Int,val description:String, val points:Int, var latitude:Double, var longitude:Double,var IsReached:Boolean)
