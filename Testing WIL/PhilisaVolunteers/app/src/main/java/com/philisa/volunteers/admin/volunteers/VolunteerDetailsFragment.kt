package com.philisa.volunteers.admin.volunteers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.databinding.FragmentVolunteerDetailsBinding
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils

class VolunteerDetailsFragment : Fragment() {

    private var _binding: FragmentVolunteerDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VolunteersViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVolunteerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowEmail.ivRowIcon.setImageResource(R.drawable.ic_email)
        binding.rowEmail.tvRowLabel.text = getString(R.string.label_email_caps)
        binding.rowPhone.ivRowIcon.setImageResource(R.drawable.ic_phone)
        binding.rowPhone.tvRowLabel.text = getString(R.string.label_phone_caps)
        binding.rowArea.ivRowIcon.setImageResource(R.drawable.ic_location)
        binding.rowArea.tvRowLabel.text = getString(R.string.label_area_row)
        binding.rowProgramme.ivRowIcon.setImageResource(R.drawable.ic_people)
        binding.rowProgramme.tvRowLabel.text = getString(R.string.label_programme_interest)
        binding.rowApplied.ivRowIcon.setImageResource(R.drawable.ic_calendar)
        binding.rowApplied.tvRowLabel.text = getString(R.string.label_applied)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnApprove.setOnClickListener { confirmAction(isApprove = true) }
        binding.btnReject.setOnClickListener { confirmAction(isApprove = false) }

        viewModel.selectedApplication.observe(viewLifecycleOwner) { application ->
            if (application != null) bindApplication(application)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnApprove.isEnabled = !isLoading
            binding.btnReject.isEnabled = !isLoading
        }
        viewModel.actionError.observe(viewLifecycleOwner) { error ->
            binding.tvActionError.isVisible = error != null
            binding.tvActionError.text = error ?: ""
        }
        viewModel.actionSuccess.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().popBackStack()
        }

        val applicationId = requireArguments().getString(Constants.EXTRA_VOLUNTEER_APPLICATION_ID).orEmpty()
        viewModel.selectApplication(applicationId)
    }

    private fun bindApplication(application: VolunteerApplication) {
        binding.tvInitial.text = application.firstName.take(1).uppercase()
        binding.tvName.text = application.fullName
        binding.rowEmail.tvRowValue.text = application.email
        binding.rowPhone.tvRowValue.text = application.phone
        binding.rowArea.tvRowValue.text = application.area
        binding.rowProgramme.tvRowValue.text = application.programmeInterest
        binding.rowApplied.tvRowValue.text = DateUtils.formatDate(application.appliedDate)
        binding.tvMotivation.text = application.motivation

        val (label, bg, text) = when (application.status) {
            VolunteerApplication.STATUS_APPROVED -> Triple(getString(R.string.status_approved), R.color.status_success_bg, R.color.status_success_text)
            VolunteerApplication.STATUS_REJECTED -> Triple(getString(R.string.status_rejected), R.color.status_error_bg, R.color.status_error_text)
            else -> Triple(getString(R.string.status_pending), R.color.status_pending_bg, R.color.status_pending_text)
        }
        binding.tvStatusPill.text = label
        binding.tvStatusPill.background.setTint(requireContext().getColor(bg))
        binding.tvStatusPill.setTextColor(requireContext().getColor(text))

        val isPending = application.status == VolunteerApplication.STATUS_PENDING
        binding.layoutActions.isVisible = isPending
    }

    private fun confirmAction(isApprove: Boolean) {
        val application = viewModel.selectedApplication.value ?: return
        val titleRes = if (isApprove) R.string.approve_confirm_title else R.string.reject_confirm_title
        val messageRes = if (isApprove) R.string.approve_confirm_message else R.string.reject_confirm_message

        AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setMessage(getString(messageRes, application.fullName))
            .setPositiveButton(if (isApprove) R.string.action_approve else R.string.action_reject) { _, _ ->
                if (isApprove) viewModel.approve(application) else viewModel.reject(application)
                val toastRes = if (isApprove) R.string.approval_success else R.string.rejection_success
                android.widget.Toast.makeText(requireContext(), toastRes, android.widget.Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
