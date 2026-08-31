package com.philisa.volunteers.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.philisa.volunteers.databinding.ActivitySplashBinding
import com.philisa.volunteers.navigation.AppNavGraph

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private val goToWelcome = Runnable { AppNavGraph.goToWelcome(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler.postDelayed(goToWelcome, 900L)
    }

    override fun onDestroy() {
        handler.removeCallbacks(goToWelcome)
        super.onDestroy()
    }
}
