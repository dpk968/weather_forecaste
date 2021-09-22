package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covidtracker.models.weatherRVModel
import com.squareup.picasso.Picasso

class weatherRVAdapter(private val weatherRVArrayList: ArrayList<weatherRVModel>):
RecyclerView.Adapter<weatherRVAdapter.weatherViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): weatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_rv_item,parent,false)
        return weatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: weatherViewHolder, position: Int) {

        val model = weatherRVArrayList[position]
        Picasso.get().load("http:"+model.icon).into(holder.condition)
        holder.temp.setText(model.temprature+"°C")
        holder.wind.setText(model.windSpeed+"km/h")
        holder.timeTV.setText(model.time)

    }

    override fun getItemCount(): Int {
        
        return weatherRVArrayList.size
    }

    class weatherViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val wind = itemView.findViewById<TextView>(R.id.idTVWindSpeed)
        val temp = itemView.findViewById<TextView>(R.id.idTVTemperature)
        val timeTV = itemView.findViewById<TextView>(R.id.idTVTime)
        val condition = itemView.findViewById<ImageView>(R.id.idIVCondition)

        
        
    }


}