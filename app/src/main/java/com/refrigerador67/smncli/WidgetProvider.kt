package com.refrigerador67.smncli

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.view.View
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.nixietab.smncliwork.SMNWeather.getWeatherInfoByLocationId
import java.util.Calendar


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val sharedPreferences by lazy {getDefaultSharedPreferences(context)}

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews( // Set layout
                context.packageName,
                R.layout.widgetlayout
            )

            Thread {
                var jsonData: MutableMap<String, String>? = mutableMapOf()
                when (sharedPreferences?.getString("provider", "openmeteo")) {
                    "openmeteo" -> {jsonData = getWeatherInfo(sharedPreferences.getString("lat", null), sharedPreferences.getString("lon", null))}
                    "smn" -> {jsonData = getWeatherInfoByLocationId(sharedPreferences.getString("locationid", null), sharedPreferences.getString("APIauth", null), sharedPreferences.getString("apiurl", null))}
                }
                // Code to be executed in the new thread

                if (jsonData != null){

                    val calendar = Calendar.getInstance()
                    val time = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE).toString()
                    val temp = jsonData.getValue("temp")
                    val humidity = jsonData.getValue("humidity")
                    val pressure = jsonData.getValue("pressure")
                    val wind = jsonData.getValue("wind_speed")
                    val widgetText = "$time\n$temp °C\nHumidity: $humidity%\nPressure: $pressure hPa\nWind: $wind km/h"

                    views.setViewVisibility(R.id.widgetBar, View.GONE)
                    views.setTextViewText(R.id.textView, widgetText)
                    sharedPreferences.edit {
                        putString("lastText", widgetText)
                    }
                }
                Thread.sleep(10000)
                appWidgetManager.updateAppWidget(appWidgetId, views) // Tell the AppWidgetManager to perform an update on the current widget
            }.start()
        }
    }

}