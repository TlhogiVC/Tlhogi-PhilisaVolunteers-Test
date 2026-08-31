package com.philisa.volunteers.ui.auth.application

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.data.repository.ApplicationRepository
import com.philisa.volunteers.databinding.ActivityApplicationSuccessBinding
import com.philisa.volunteers.databinding.ItemTimelineStepBinding
import com.philisa.volunteers.navigation.AuthNavGraph
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch

class ApplicationSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicationSuccessBinding
    private val applicationRepository = ApplicationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplicationSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnDone.setOnClickListener { AuthNavGraph.goToLogin(this) }

        val applicationId = intent.getStringExtra(Constants.EXTRA_VOLUNTEER_APPLICATION_ID).orEmpty()
        lifecycleScope.launch {
            val application = applicationRepository.getApplication(applicationId)
            if (application != null) bindApplication(application)
        }
    }

    private fun bindApplication(application: VolunteerApplication) {
        binding.tvReferenceNumber.text = getString(R.string.reference_label, application.referenceNumber)

        val isApproved = application.status == VolunteerApplication.STATUS_APPROVED
        val isRejected = application.status == VolunteerApplication.STATUS_REJECTED
        binding.tvStatusPill.text = when {
            isApproved -> getString(R.string.status_approved)
            isRejected -> getString(R.string.status_rejected)
            else -> getString(R.string.status_under_review)
        }
        val (bgColor, textColor) = when {
            isApproved -> R.color.status_success_bg to R.color.status_success_text
            isRejected -> R.color.status_error_bg to R.color.status_error_text
            else -> R.color.status_pending_bg to R.color.status_pending_text
        }
        binding.tvStatusPill.background.setTint(getColor(bgColor))
        binding.tvStatusPill.setTextColor(getColor(textColor))

        addTimelineStep(getString(R.string.timeline_submitted), DateUtils.formatDate(application.appliedDate), done = true)
        addTimelineStep(getString(R.string.timeline_verification), "", done = isApproved || isRejected)
        addTimelineStep(getString(R.string.timeline_coordinator_review), "", done = isApproved || isRejected)
        addTimelineStep(
            getString(R.string.timeline_approved),
            if (isApproved) DateUtils.formatDate(application.reviewedDate) else "",
            done = isApproved
        )
    }

    private fun addTimelineStep(title: String, date: String, done: Boolean) {
        val stepBinding = ItemTimelineStepBinding.inflate(layoutInflater, binding.layoutTimeline, true)
        stepBinding.tvTimelineTitle.text = title
        stepBinding.tvTimelineDate.text = date
        stepBinding.ivTimelineIcon.setColorFilter(
            getColor(if (done) R.color.purple_700 else R.color.divider)
        )
        stepBinding.ivTimelineIcon.alpha = if (done) 1f else 0.5f
    }
}
