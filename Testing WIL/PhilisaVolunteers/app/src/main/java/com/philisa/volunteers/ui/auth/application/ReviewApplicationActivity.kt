package com.philisa.volunteers.ui.auth.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.databinding.ActivityReviewApplicationBinding
import com.philisa.volunteers.databinding.ItemSummaryRowBinding
import com.philisa.volunteers.navigation.AuthNavGraph
import com.philisa.volunteers.utils.Constants
import kotlinx.coroutines.launch

class ReviewApplicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewApplicationBinding
    private val applicationRepository = ApplicationRepository()

    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var area: String
    private lateinit var programmeInterest: String
    private lateinit var motivation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewApplicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstName = intent.getStringExtra(Constants.EXTRA_FIRST_NAME).orEmpty()
        lastName = intent.getStringExtra(Constants.EXTRA_LAST_NAME).orEmpty()
        email = intent.getStringExtra(Constants.EXTRA_EMAIL).orEmpty()
        phone = intent.getStringExtra(Constants.EXTRA_PHONE).orEmpty()
        area = intent.getStringExtra(Constants.EXTRA_AREA).orEmpty()
        programmeInterest = intent.getStringExtra(Constants.EXTRA_PROGRAMME_INTEREST).orEmpty()
        motivation = intent.getStringExtra(Constants.EXTRA_MOTIVATION).orEmpty()

        addSummaryRow(getString(R.string.summary_name), "$firstName $lastName")
        addSummaryRow(getString(R.string.summary_email), email)
        addSummaryRow(getString(R.string.summary_phone), phone)
        addSummaryRow(getString(R.string.summary_area), area)
        addSummaryRow(getString(R.string.summary_programme), programmeInterest)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnBackStep.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener { submitApplication() }
    }

    private fun addSummaryRow(label: String, value: String) {
        val rowBinding = ItemSummaryRowBinding.inflate(layoutInflater, binding.layoutSummaryRows, true)
        rowBinding.tvRowLabel.text = label
        rowBinding.tvRowValue.text = value
    }

    private fun submitApplication() {
        binding.tvConsentError.isVisible = false
        binding.tvSubmitError.isVisible = false

        if (!binding.cbConsent.isChecked) {
            binding.tvConsentError.isVisible = true
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            val application = VolunteerApplication(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                area = area,
                programmeInterest = programmeInterest,
                motivation = motivation
            )
            val result = applicationRepository.submitApplication(application)
            setLoading(false)
            result.onSuccess { saved ->
                AuthNavGraph.goToApplicationSuccess(this@ReviewApplicationActivity, saved.id)
            }.onFailure {
                binding.tvSubmitError.isVisible = true
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnSubmit.isEnabled = !isLoading
    }
}
