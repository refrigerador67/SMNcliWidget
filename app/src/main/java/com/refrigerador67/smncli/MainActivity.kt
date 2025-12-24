package com.refrigerador67.smncli

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences


class MainActivity : AppCompatActivity() {

    val sharedPreferences: SharedPreferences? by lazy {getDefaultSharedPreferences(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.weatherText).text = sharedPreferences?.getString("lastText", "")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val settingsIntent = Intent(
                this,
                SettingsActivity::class.java
            )
            startActivity(settingsIntent)
            true
        }
        R.id.refresh_location -> {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    val locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager?
                    sharedPreferences?.edit {
                        putString("lat", locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.latitude.toString())
                        putString("lon", locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.longitude.toString())
                    }
                }
                else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}