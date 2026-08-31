package com.philisa.volunteers.admin.impact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.ImpactStats
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.databinding.FragmentManageImpactStatsBinding

class ManageImpactStatsFragment : Fragment() {

    private var _binding: FragmentManageImpactStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ImpactStatsAdminViewModel by viewModels()

    private var volunteers: List<User> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageImpactStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { save() }

        viewModel.stats.observe(viewLifecycleOwner) { stats -> bind(stats) }
        viewModel.approvedVolunteers.observe(viewLifecycleOwner) { list ->
            volunteers = list
            val names = list.map { it.fullName }
            binding.spinnerVolunteer.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            val currentName = viewModel.stats.value?.volunteerOfMonthName
            val index = list.indexOfFirst { it.fullName == currentName }
            if (index >= 0) binding.spinnerVolunteer.setSelection(index)
        }
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

        viewModel.loadStats()
    }

    private fun bind(stats: ImpactStats) {
        binding.etFamiliesFed.setText(stats.familiesFed.toString())
        binding.etYouthMentored.setText(stats.youthMentored.toString())
        binding.etSafeHouseIntakes.setText(stats.safeHouseIntakes.toString())
        binding.etSeniorsVisited.setText(stats.seniorsVisited.toString())
        binding.etActivityCount.setText(stats.volunteerOfMonthActivityCount.toString())
    }

    private fun save() {
        val selectedVolunteer = volunteers.getOrNull(binding.spinnerVolunteer.selectedItemPosition)
        val stats = ImpactStats(
            familiesFed = binding.etFamiliesFed.text?.toString()?.toIntOrNull() ?: 0,
            youthMentored = binding.etYouthMentored.text?.toString()?.toIntOrNull() ?: 0,
            safeHouseIntakes = binding.etSafeHouseIntakes.text?.toString()?.toIntOrNull() ?: 0,
            seniorsVisited = binding.etSeniorsVisited.text?.toString()?.toIntOrNull() ?: 0,
            volunteerOfMonthName = selectedVolunteer?.fullName.orEmpty(),
            volunteerOfMonthProgramme = "",
            volunteerOfMonthActivityCount = binding.etActivityCount.text?.toString()?.toIntOrNull() ?: 0,
            month = ""
        )
        viewModel.saveStats(stats)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
