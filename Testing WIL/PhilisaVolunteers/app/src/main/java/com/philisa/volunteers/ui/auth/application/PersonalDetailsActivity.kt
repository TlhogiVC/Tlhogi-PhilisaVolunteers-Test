package com.philisa.volunteers.ui.auth.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.ActivityPersonalDetailsBinding
import com.philisa.volunteers.navigation.AuthNavGraph
import com.philisa.volunteers.utils.ValidationUtils

class PersonalDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvStepLabel.text = getString(R.string.step_of_3, 1)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnContinue.setOnClickListener { validateAndContinue() }
    }

    private fun validateAndContinue() {
        val firstName = binding.etFirstName.text?.toString().orEmpty().trim()
        val lastName = binding.etLastName.text?.toString().orEmpty().trim()
        val email = binding.etEmail.text?.toString().orEmpty().trim()
        val phone = binding.etPhone.text?.toString().orEmpty().trim()
        val area = binding.etArea.text?.toString().orEmpty().trim()

        var isValid = true
        if (!ValidationUtils.isNotBlank(firstName)) {
            binding.etFirstName.error = getString(R.string.error_field_required)
            isValid = false
        }
        if (!ValidationUtils.isNotBlank(lastName)) {
            binding.etLastName.error = getString(R.string.error_field_required)
            isValid = false
        }
        if (!ValidationUtils.isValidEmail(email)) {
            binding.etEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            binding.etPhone.error = getString(R.string.error_invalid_phone)
            isValid = false
        }
        if (!ValidationUtils.isNotBlank(area)) {
            binding.etArea.error = getString(R.string.error_field_required)
            isValid = false
        }
        if (!isValid) return

        AuthNavGraph.goToProgrammeInterest(this, firstName, lastName, email, phone, area)
    }
}
