package com.example.myservice

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.myservice.databinding.ActivityMainBinding
import java.util.Objects

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var boundStatus = false
    private lateinit var boudService: MyBoundService

    private val conection = object : ServiceConnection {

        override fun onServiceDisconnected(p0: ComponentName?) {
            boundStatus = false
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val myBinder = p1 as MyBoundService.MyBinder
            boudService = myBinder.getService
            boundStatus = true
            getNumberFromService()
        }

    }

    private fun getNumberFromService() {
        boudService.numberLiveData.observe(this) { number ->
            binding.tvBoundServiceNumber.text = number.toString()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var serviceIntent = Intent(this, MyBackgroundService::class.java)
        val foregroundServiceIntent = Intent(this, MyForegroundService::class.java)

        binding.btnStartBackgroundService.setOnClickListener {
            startService(serviceIntent)
        }

        binding.btnStopBackgroundService.setOnClickListener {
            stopService(serviceIntent)
        }

        binding.btnStartForegroundService.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(foregroundServiceIntent)
            } else {
                startService(foregroundServiceIntent)
            }
        }

        binding.btnStopForegroundService.setOnClickListener {
            stopService(foregroundServiceIntent)
        }

        val boundServiceIntent = Intent(this, MyBoundService::class.java)
        binding.btnStartBoundService.setOnClickListener {
            bindService(boundServiceIntent, conection, BIND_AUTO_CREATE)
        }

        binding.btnStopBoundService.setOnClickListener {
            unbindService(conection)
        }

    }

    override fun onStop() {
        super.onStop()
        if (boundStatus) {
            unbindService(conection)
            boundStatus = false
        }
    }


}