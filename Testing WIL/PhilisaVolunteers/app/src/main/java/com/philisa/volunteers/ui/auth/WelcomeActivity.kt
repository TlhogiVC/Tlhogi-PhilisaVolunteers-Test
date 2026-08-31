package com.philisa.volunteers.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.philisa.volunteers.databinding.ActivityWelcomeBinding
import com.philisa.volunteers.navigation.AuthNavGraph

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExplore.setOnClickListener {
            binding.root.smoothScrollTo(0, binding.btnBecomeVolunteer.top)
        }
        binding.btnBecomeVolunteer.setOnClickListener {
            AuthNavGraph.goToPersonalDetails(this)
        }
        binding.btnVolunteerLogin.setOnClickListener {
            AuthNavGraph.goToLogin(this)
        }
    }
}
