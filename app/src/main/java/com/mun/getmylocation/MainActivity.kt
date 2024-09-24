package com.mun.getmylocation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent


class MainActivity : AppCompatActivity() {
    private lateinit var locationUtil: LocationUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationUtil = LocationUtil(this)

        val button: Button = findViewById(R.id.btnLocation)
        button.setOnClickListener {
            locationUtil.checkLocationSettings()
        }
    }

    // Forward onActivityResult to LocationUtil
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationUtil.onActivityResult(requestCode, resultCode)
    }

    // Forward onRequestPermissionsResult to LocationUtil
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationUtil.onRequestPermissionsResult(requestCode, grantResults)
    }
}