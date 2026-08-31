package com.philisa.volunteers.ui.volunteer.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.FragmentActivityDetailsBinding
import com.philisa.volunteers.utils.Constants

class ActivityDetailsFragment : Fragment() {

    private var _binding: FragmentActivityDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ActivitiesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActivityDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowDateTime.ivRowIcon.setImageResource(R.drawable.ic_calendar)
        binding.rowDateTime.tvRowLabel.text = getString(R.string.label_date_time)
        binding.rowLocation.ivRowIcon.setImageResource(R.drawable.ic_location)
        binding.rowLocation.tvRowLabel.text = getString(R.string.label_location)
        binding.rowRole.ivRowIcon.setImageResource(R.drawable.ic_profile)
        binding.rowRole.tvRowLabel.text = getString(R.string.label_volunteer_role)
        binding.rowSpots.ivRowIcon.setImageResource(R.drawable.ic_people)
        binding.rowSpots.tvRowLabel.text = getString(R.string.label_spots_remaining)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnApply.setOnClickListener { viewModel.applyForActivity() }
        binding.btnCancel.setOnClickListener { showCancelConfirmation() }

        viewModel.currentActivity.observe(viewLifecycleOwner) { activity -> activity?.let { bindActivity(it) } }
        viewModel.currentApplication.observe(viewLifecycleOwner) { application ->
            val alreadyApplied = application != null
            binding.btnApply.isVisible = !alreadyApplied
            binding.btnCancel.isVisible = alreadyApplied
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnApply.isEnabled = !isLoading
            binding.btnCancel.isEnabled = !isLoading
        }
        viewModel.actionError.observe(viewLifecycleOwner) { error ->
            binding.tvActionError.isVisible = error != null
            binding.tvActionError.text = error ?: ""
        }

        val activityId = requireArguments().getString(Constants.EXTRA_ACTIVITY_ID).orEmpty()
        viewModel.loadActivityDetails(activityId)
    }

    private fun bindActivity(activity: Activity) {
        binding.tvProgramme.text = activity.programme.uppercase()
        binding.tvTitle.text = activity.title
        binding.tvDescription.text = activity.description
        binding.rowDateTime.tvRowValue.text = activity.dateTimeLabel
        binding.rowLocation.tvRowValue.text = activity.location
        binding.rowRole.tvRowValue.text = activity.volunteerRole
        binding.rowSpots.tvRowValue.text = getString(R.string.spots_available, activity.spotsRemaining)

        val isBooked = viewModel.currentApplication.value != null
        binding.tvStatusPill.text = getString(if (isBooked) R.string.status_booked else R.string.status_open)
        val (bg, text) = R.color.status_success_bg to R.color.status_success_text
        binding.tvStatusPill.background.setTint(requireContext().getColor(bg))
        binding.tvStatusPill.setTextColor(requireContext().getColor(text))
    }

    private fun showCancelConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.cancel_confirm_title)
            .setMessage(R.string.cancel_confirm_message)
            .setPositiveButton(R.string.action_yes_cancel) { _, _ -> viewModel.cancelParticipation() }
            .setNegativeButton(R.string.action_keep_spot, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
