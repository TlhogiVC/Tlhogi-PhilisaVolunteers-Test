package com.philisa.volunteers.admin.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.FragmentEditActivityBinding
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.ValidationUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditActivityFragment : Fragment() {

    private var _binding: FragmentEditActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminActivitiesViewModel by activityViewModels()

    private var activityId: String = ""
    private var dateMillis: Long = 0L
    private val dateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.etStartTime.setOnClickListener { showTimePicker(binding.etStartTime) }
        binding.etEndTime.setOnClickListener { showTimePicker(binding.etEndTime) }
        binding.btnSave.setOnClickListener { save() }

        viewModel.selectedActivity.observe(viewLifecycleOwner) { activity -> activity?.let { bind(it) } }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnSave.isEnabled = !isLoading
        }
        viewModel.saveError.observe(viewLifecycleOwner) { error ->
            binding.tvFormError.isVisible = error != null
            binding.tvFormError.text = error ?: ""
        }
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().popBackStack()
        }

        activityId = requireArguments().getString(Constants.EXTRA_ACTIVITY_ID).orEmpty()
        viewModel.loadActivity(activityId)
    }

    private fun bind(activity: Activity) {
        binding.etTitle.setText(activity.title)
        binding.etProgramme.setText(activity.programme)
        binding.etDate.setText(activity.date)
        dateMillis = activity.dateMillis
        binding.etStartTime.setText(activity.startTime)
        binding.etEndTime.setText(activity.endTime)
        binding.etLocation.setText(activity.location)
        binding.etRole.setText(activity.volunteerRole)
        binding.etTotalSpots.setText(activity.totalSpots.toString())
        binding.etDescription.setText(activity.description)
        binding.radioPublished.isChecked = activity.status == Activity.STATUS_PUBLISHED
        binding.radioDraft.isChecked = activity.status == Activity.STATUS_DRAFT
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (dateMillis > 0) calendar.timeInMillis = dateMillis
        DatePickerDialog(requireContext(), { _, year, month, day ->
            calendar.set(year, month, day, 0, 0, 0)
            dateMillis = calendar.timeInMillis
            binding.etDate.setText(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker(target: com.google.android.material.textfield.TextInputEditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hour, minute ->
            target.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun save() {
        val title = binding.etTitle.text?.toString().orEmpty().trim()
        val programme = binding.etProgramme.text?.toString().orEmpty().trim()
        val date = binding.etDate.text?.toString().orEmpty().trim()
        val startTime = binding.etStartTime.text?.toString().orEmpty().trim()
        val endTime = binding.etEndTime.text?.toString().orEmpty().trim()
        val location = binding.etLocation.text?.toString().orEmpty().trim()
        val role = binding.etRole.text?.toString().orEmpty().trim()
        val totalSpotsText = binding.etTotalSpots.text?.toString().orEmpty().trim()
        val description = binding.etDescription.text?.toString().orEmpty().trim()

        var isValid = true
        if (!ValidationUtils.isNotBlank(title)) { binding.etTitle.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(programme)) { binding.etProgramme.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(date)) { binding.etDate.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(startTime)) { binding.etStartTime.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(endTime)) { binding.etEndTime.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(location)) { binding.etLocation.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(role)) { binding.etRole.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isValidSpotsCount(totalSpotsText)) { binding.etTotalSpots.error = getString(R.string.error_total_spots_invalid); isValid = false }
        if (!isValid) return

        val currentFilled = viewModel.selectedActivity.value?.filledSpots ?: 0
        val activity = Activity(
            id = activityId,
            title = title,
            programme = programme,
            date = date,
            dateMillis = dateMillis,
            startTime = startTime,
            endTime = endTime,
            location = location,
            volunteerRole = role,
            totalSpots = totalSpotsText.toInt(),
            filledSpots = currentFilled,
            description = description,
            status = if (binding.radioPublished.isChecked) Activity.STATUS_PUBLISHED else Activity.STATUS_DRAFT,
            createdBy = viewModel.selectedActivity.value?.createdBy.orEmpty(),
            createdDate = viewModel.selectedActivity.value?.createdDate ?: 0L
        )
        viewModel.updateActivity(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
