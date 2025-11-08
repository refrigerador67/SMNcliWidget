package com.refrigerador67.smncli

import android.util.Log
import com.nixietab.smncliwork.SMNWeather
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun getWeatherInfo(lat: String?, lon: String?): MutableMap<String, String>? {
        val w = fetchWeatherData(lat, lon)
        if (w == null) return null

        Log.i("OpenMeteoFetcher", w.toString())
        val info: MutableMap<String, String> = HashMap()

        val weather = w.optJSONObject("current")
        info.put("description", "")
        Log.i("mutable", w.toString())
        if (weather != null) {
            info.put("time", weather.optDouble("time", Double.Companion.NaN).toString())
            info.put("temp", weather.optDouble("temperature_2m", Double.Companion.NaN).toString())
            info.put("humidity",weather.optDouble("relative_humidity_2m", Double.Companion.NaN).toString())
            info.put("pressure", weather.optDouble("surface_pressure", Double.Companion.NaN).toString())
            info.put("wind_speed", weather.optDouble("wind_speed_10m", Double.Companion.NaN).toString())
            info.put("wind_dir", weather.optString("wind_direction_10m", ""))
        }
        return info
    }

    fun fetchWeatherData(lat:String?, lon:String?): JSONObject? {
        SMNWeather.handleSSLHandshake()
        val url = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,is_day,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m")
        val conn = url.openConnection() as HttpsURLConnection
        conn.setConnectTimeout(10000)
        conn.setReadTimeout(10000)
        conn.setRequestMethod("GET")

        try {
            BufferedReader(InputStreamReader(conn.getInputStream())).use { `in` ->
                val response = StringBuilder()
                var line: String?
                while ((`in`.readLine().also { line = it }) != null) {
                    response.append(line)
                }

                return JSONObject(response.toString())
            }
        } catch (_: IOException) {
            return null
        }
    }