package com.refrigerador67.smncli

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.util.Log
import com.nixietab.smncliwork.SMNWeather.getWeatherInfo



class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews( // Set layout
                context.packageName,
                R.layout.widgetlayout
            )

            Thread {
                // Code to be executed in the new thread
                val jsonData = getWeatherInfo("")

                val desc = jsonData.getValue("description")
                val temp = jsonData.getValue("temp")
                val humidity = jsonData.getValue("humidity")
                val pressure = jsonData.getValue("pressure")
                val wind = jsonData.getValue("wind_speed")

                val widgetText = "$desc\n$temp °C\nHumidity: $humidity%\nPressure: $pressure hPa\nWind: $wind km/h"

                Log.i("lol",widgetText)
                views.setTextViewText(R.id.textView, widgetText)
                appWidgetManager.updateAppWidget(appWidgetId, views) // Tell the AppWidgetManager to perform an update on the current widget
            }.start()
        }
    }
}