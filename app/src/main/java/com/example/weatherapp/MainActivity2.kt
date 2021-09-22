package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.covidtracker.models.weatherRVModel
import com.example.weatherapp.databinding.ActivityMain2Binding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import org.json.JSONException

class MainActivity2 : AppCompatActivity(){

    private lateinit var binding:ActivityMain2Binding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager:LocationManager
    lateinit var weatherRVArrayList:ArrayList<weatherRVModel>
    lateinit var weatherRVAdapter: weatherRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)
        weatherRVArrayList = ArrayList<weatherRVModel>()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        checkLocationPermission()

        binding.IVSearch.setOnClickListener {
            val city:String = binding.edtText.text.toString()

            if(city.isNullOrBlank()){
                Toast.makeText(this,"Please Enter a valid city",Toast.LENGTH_LONG).show()
            }else{
                binding.idPBLoading.visibility = View.VISIBLE
                binding.connectionOnLayout.visibility = View.GONE
                getWeatherInfo(city)
            }
        }
    }

    private fun checkLocationPermission() {

        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION) , 101)
            return
        }

        task.addOnSuccessListener {
            if (it != null){

                val geocoder = Geocoder(this)
                val currentLocation = geocoder.getFromLocation(
                    it.latitude,it.longitude,1
                )

                getWeatherInfo(currentLocation.first().subAdminArea)

                Toast.makeText(applicationContext,"Current Location is "+currentLocation.first().subAdminArea,Toast.LENGTH_LONG).show()

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101){
            if (grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                checkLocationPermission()
            }else{
                Toast.makeText(this,"allow permission for use this app ",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getWeatherInfo(city: String) {

        val url = "http://api.weatherapi.com/v1/forecast.json?key=d6659e430562400995b34457213108&q="+city+"&day=1&aqi=no&alerts=no"


        val queue = Volley.newRequestQueue(this@MainActivity2)

        val request = JsonObjectRequest(Request.Method.GET,url,null, { response ->

            try{

                weatherRVArrayList.clear()
                val region = response.getJSONObject("location")
                val current = response.getJSONObject("current")
                val temp = current.getString("temp_c")
                val r = region.getString("name")
                val condition = current.getJSONObject("condition")
                val conditionText = condition.getString("text")
                val iconPath = condition.getString("icon")

                val windSpeed = current.getString("wind_kph")
                binding.idCurrentWindSpeed.setText(windSpeed.toString()+" Km/h")
                Picasso.get().load("http:"+iconPath).into(binding.idIVIcon)

                binding.idTVCondition.text = conditionText.toString()
                binding.idTVTemperature.text = "$temp °C"
                binding.idTVCityName.text = r.toString()

                val isDay = current.getInt("is_day")
                if (isDay==1){
                    //morning
                    Picasso.get().load("https://images.unsplash.com/photo-1575050312925-f0d7757591eb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1534&q=80").into(binding.idIVBack)
                }else{
                    Picasso.get().load("https://images.unsplash.com/photo-1507502707541-f369a3b18502?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80").into(binding.idIVBack)
                }

                val forecastObj = response.getJSONObject("forecast")
                val forecasto = forecastObj.getJSONArray("forecastday").getJSONObject(0)
                val hoursArray = forecasto.getJSONArray("hour")

                for (i in 0 until hoursArray.length()){
                    val hourObj = hoursArray.getJSONObject(i)
                    val time = hourObj.getString("time").toString()
                    val tempr = hourObj.getString("temp_c").toString()
                    val img = hourObj.getJSONObject("condition").getString("icon").toString()
                    val wind = hourObj.getString("wind_kph").toString()

                    val tweatherModel = weatherRVModel(time,tempr,img,wind)

                    weatherRVArrayList += tweatherModel
                }
                weatherRVAdapter = weatherRVAdapter(weatherRVArrayList)
                binding.idRVWeather.adapter = weatherRVAdapter



                binding.idPBLoading.visibility = View.GONE
                binding.connectionOnLayout.visibility = View.VISIBLE

            }catch (e: JSONException){
                e.printStackTrace()
            }


        }, { error ->
            Toast.makeText(this,"Please Enter Valid City Name", Toast.LENGTH_LONG).show()


        })

        queue.add(request)
    }



}