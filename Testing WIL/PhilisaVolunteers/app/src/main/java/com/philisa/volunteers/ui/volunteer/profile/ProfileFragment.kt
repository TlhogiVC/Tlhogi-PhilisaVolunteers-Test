package com.philisa.volunteers.ui.volunteer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.User
import com.philisa.volunteers.databinding.FragmentProfileBinding
import com.philisa.volunteers.navigation.AppNavGraph
import com.philisa.volunteers.navigation.VolunteerNavGraph

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowPhone.ivRowIcon.setImageResource(R.drawable.ic_phone)
        binding.rowPhone.tvRowLabel.text = getString(R.string.label_phone_caps)
        binding.rowEmail.ivRowIcon.setImageResource(R.drawable.ic_email)
        binding.rowEmail.tvRowLabel.text = getString(R.string.label_email_caps)
        binding.rowArea.ivRowIcon.setImageResource(R.drawable.ic_location)
        binding.rowArea.tvRowLabel.text = getString(R.string.label_area_caps)

        binding.btnEditProfile.setOnClickListener { VolunteerNavGraph.toEditProfile(findNavController()) }
        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
            AppNavGraph.goToWelcome(requireActivity())
        }

        viewModel.user.observe(viewLifecycleOwner) { user -> user?.let { bindUser(it) } }
        viewModel.monthsActive.observe(viewLifecycleOwner) { count ->
            binding.statMonths.tvStatValue.text = count.toString()
            binding.statMonths.tvStatLabel.text = getString(R.string.stat_months)
        }
        viewModel.activityCount.observe(viewLifecycleOwner) { count ->
            binding.statActivities.tvStatValue.text = count.toString()
            binding.statActivities.tvStatLabel.text = getString(R.string.stat_activities)
        }
        viewModel.upcomingCount.observe(viewLifecycleOwner) { count ->
            binding.statUpcoming.tvStatValue.text = count.toString()
            binding.statUpcoming.tvStatLabel.text = getString(R.string.stat_upcoming)
        }

        viewModel.loadProfile()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfile()
    }

    private fun bindUser(user: User) {
        binding.tvUserName.text = user.fullName
        binding.tvVolunteerId.text = getString(R.string.volunteer_id_label, user.volunteerId)
        binding.rowPhone.tvRowValue.text = user.phone
        binding.rowEmail.tvRowValue.text = user.email
        binding.rowArea.tvRowValue.text = user.area

        binding.chipGroupSkills.removeAllViews()
        user.skills.forEach { skill -> binding.chipGroupSkills.addView(buildChip(skill)) }

        binding.chipGroupAvailability.removeAllViews()
        user.availability.forEach { day -> binding.chipGroupAvailability.addView(buildChip(day)) }
    }

    private fun buildChip(text: String): Chip {
        return Chip(requireContext()).apply {
            this.text = text
            isClickable = false
            isCheckable = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
