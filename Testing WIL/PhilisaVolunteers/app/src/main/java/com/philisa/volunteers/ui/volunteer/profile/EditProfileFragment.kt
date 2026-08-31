package com.philisa.volunteers.ui.volunteer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.FragmentEditProfileBinding

/**
 * New screen, not shown in the PDF (figure numbering jumps 65 -> 67) — Stage 1 inferred its
 * fields directly from Profile's editable data (phone, area, skills, availability).
 */
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.user.value?.let { user ->
            binding.etPhone.setText(user.phone)
            binding.etArea.setText(user.area)
            binding.etSkills.setText(user.skills.joinToString(", "))
            binding.etAvailability.setText(user.availability.joinToString(", "))
        }

        binding.btnSave.setOnClickListener { saveChanges() }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.btnSave.isEnabled = !isLoading
        }
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun saveChanges() {
        val phone = binding.etPhone.text?.toString().orEmpty().trim()
        val area = binding.etArea.text?.toString().orEmpty().trim()
        val skills = binding.etSkills.text?.toString().orEmpty()
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val availability = binding.etAvailability.text?.toString().orEmpty()
            .split(",").map { it.trim() }.filter { it.isNotEmpty() }

        viewModel.saveProfile(phone, area, skills, availability)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
