package com.example.ea

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// Phoenix 21 June -> 6 July 2022
class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var latitudeView: TextView
    private lateinit var longitudeView: TextView
    private lateinit var contactView: TextView

    private lateinit var lat: String
    private lateinit var long: String
    private lateinit var contactNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!checkPermissions()){
            requestPermission()
        }

        latitudeView = findViewById(R.id.tv_latitude)
        longitudeView = findViewById(R.id.tv_longtitude)

        contactView = findViewById(R.id.show_contact)

        val prefs = getSharedPreferences("MyPreference", MODE_PRIVATE)
        val num = prefs.getString("contact", "")
        contactNumber = prefs.getString("contact","").toString()
        contactView.text = num

        // val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
            val location:Location?=task.result
            if(location==null)
            {
                Toast.makeText(this,"Null Received",Toast.LENGTH_SHORT).show()
            }
            else
            {
                //Toast.makeText(this,"Get Success",Toast.LENGTH_SHORT).show()
                lat = location.latitude.toString()
                long = location.longitude.toString()
                latitudeView.text = "" + lat
                longitudeView.text = "" + long
            }
        }

        checkLocationPermission()


        val button = findViewById<Button>(R.id.button1)
        button.setOnClickListener{
            checkMissing(contactNumber)
            getLocationWhenClicked()
            postDataUsingVolley(lat, long, contactNumber);
        }

        val setting = findViewById<Button>(R.id.settings)
        setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
/*
        val button1 = findViewById<Button>(R.id.back)
        button1.setOnClickListener {
            //setContentView(R.layout.activity_main)
            Toast.makeText(this,"yo",Toast.LENGTH_SHORT).show()
        }
*/
    }

    override fun onStart() {
        super.onStart()
        // Change and update number
        val prefs = getSharedPreferences("MyPreference", MODE_PRIVATE)
        val num = prefs.getString("contact", "")
        contactNumber = prefs.getString("contact","").toString()
        contactView.text = num
    }

    // Check if contact number is missing or not
    private fun checkMissing(contact_number: String){
        if (contact_number == ""){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No Contact Number")
            builder.setMessage("Please set your emergency contact number in settings")
            //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(applicationContext,
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(applicationContext,
                    android.R.string.no, Toast.LENGTH_SHORT).show()
            }

            builder.setNeutralButton("Maybe") { dialog, which ->
                Toast.makeText(applicationContext,
                    "Maybe", Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }
    }

    // Post Json to REST Api
    private fun postDataUsingVolley(name: String, job: String, contact: String) {
        // url to post our data
        val url = "https://emergency-project.herokuapp.com/location/"

        // creating a new variable for our request queue
        val queue = Volley.newRequestQueue(this@MainActivity)

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            com.android.volley.Response.Listener { response ->
                // on below line we are displaying a success toast message.
                Toast.makeText(this@MainActivity, "Data added to API", Toast.LENGTH_SHORT).show()

            }, com.android.volley.Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(
                    this@MainActivity,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {

            override fun getParams(): Map<String, String> {
                // below line we are creating a map for
                // storing our values in key and value pair.
                val params: MutableMap<String, String> = HashMap()

                // on below line we are passing our key
                // and value pair to our parameters.
                params["latitude"] = name
                params["longitude"] = job
                params["contact_number"] = contact

                // at last we are
                // returning our params.
                return params
            }
        }
        // below line is to make
        // a json object request.
        queue.add(request)
    }


    // Check, request location permission
    // Get Location (latitude, longitude)

    private fun checkLocationPermission(){
        if(checkPermissions())
        {
            if(isLocationEnabled())
            {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermission()
                    return
                }
            }
            else
            {
                //setting open here
                Toast.makeText(this,"Turn on location",Toast.LENGTH_SHORT).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            //request permission here
            requestPermission()
        }
    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
            }
        }
    }

    private fun getLocationWhenClicked(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
            val location:Location?=task.result
            if(location==null)
            {
                Toast.makeText(this,"Null Received",Toast.LENGTH_SHORT).show()
            }
            else
            {
                lat = location.latitude.toString()
                long = location.longitude.toString()
                latitudeView.text=""+lat
                longitudeView.text=""+long
            }
        }
    }

    private fun isLocationEnabled():Boolean {
        val locationManager:LocationManager=getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
            )
    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }



    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext,"Granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }
            else
            {
                Toast.makeText(applicationContext,"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

}

/*
Phoenix 21 June -> 6 July 2022
@CREDIT,@SOURCE
https://www.geeksforgeeks.org/how-to-post-data-to-api-using-volley-in-android/

# Django REST api
https://medium.com/swlh/build-your-first-rest-api-with-django-rest-framework-e394e39a482c
*/




