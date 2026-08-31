package com.philisa.volunteers.ui.auth.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.ActivityProgrammeInterestBinding
import com.philisa.volunteers.navigation.AuthNavGraph
import com.philisa.volunteers.utils.Constants

class ProgrammeInterestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgrammeInterestBinding
    private var selectedProgramme: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgrammeInterestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvStepLabel.text = getString(R.string.step_of_3, 2)

        val programmes = listOf(
            getString(R.string.programme_womens_empowerment),
            getString(R.string.programme_youth),
            getString(R.string.programme_after_school),
            getString(R.string.programme_community_feeding),
            getString(R.string.programme_safe_houses),
            getString(R.string.programme_social_work)
        )
        binding.rvInterests.layoutManager = LinearLayoutManager(this)
        binding.rvInterests.adapter = InterestOptionAdapter(programmes) { selected ->
            selectedProgramme = selected
            binding.tvInterestError.isVisible = false
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnBackStep.setOnClickListener { finish() }
        binding.btnContinue.setOnClickListener { validateAndContinue() }
    }

    private fun validateAndContinue() {
        if (selectedProgramme == null) {
            binding.tvInterestError.isVisible = true
            return
        }
        val motivation = binding.etMotivation.text?.toString().orEmpty().trim()

        val intent = intent
        AuthNavGraph.goToReviewApplication(
            activity = this,
            firstName = intent.getStringExtra(Constants.EXTRA_FIRST_NAME).orEmpty(),
            lastName = intent.getStringExtra(Constants.EXTRA_LAST_NAME).orEmpty(),
            email = intent.getStringExtra(Constants.EXTRA_EMAIL).orEmpty(),
            phone = intent.getStringExtra(Constants.EXTRA_PHONE).orEmpty(),
            area = intent.getStringExtra(Constants.EXTRA_AREA).orEmpty(),
            programmeInterest = selectedProgramme!!,
            motivation = motivation
        )
    }
}
