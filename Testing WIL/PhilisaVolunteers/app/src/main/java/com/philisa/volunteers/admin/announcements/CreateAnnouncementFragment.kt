package com.philisa.volunteers.admin.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.FragmentCreateAnnouncementBinding
import com.philisa.volunteers.utils.ValidationUtils

class CreateAnnouncementFragment : Fragment() {

    private var _binding: FragmentCreateAnnouncementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnnouncementsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnCreate.setOnClickListener { save() }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnCreate.isEnabled = !isLoading
        }
        viewModel.saveError.observe(viewLifecycleOwner) { error ->
            binding.tvFormError.isVisible = error != null
            binding.tvFormError.text = error ?: ""
        }
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) findNavController().popBackStack()
        }
    }

    private fun save() {
        val title = binding.etTitle.text?.toString().orEmpty().trim()
        val body = binding.etMessageBody.text?.toString().orEmpty().trim()

        var isValid = true
        if (!ValidationUtils.isNotBlank(title)) { binding.etTitle.error = getString(R.string.error_field_required); isValid = false }
        if (!ValidationUtils.isNotBlank(body)) { binding.etMessageBody.error = getString(R.string.error_field_required); isValid = false }
        if (!isValid) return

        viewModel.createAnnouncement(title, body, publish = binding.radioPublished.isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
