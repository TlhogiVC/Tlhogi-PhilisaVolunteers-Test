package com.philisa.volunteers.ui.volunteer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.FragmentHomeBinding
import com.philisa.volunteers.navigation.VolunteerNavGraph
import com.philisa.volunteers.ui.volunteer.activities.ActivityAdapter
import com.philisa.volunteers.ui.volunteer.activities.ActivityListItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var todaysAdapter: ActivityAdapter
    private lateinit var comingUpAdapter: ActivityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todaysAdapter = ActivityAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvTodaysActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodaysActivities.adapter = todaysAdapter

        comingUpAdapter = ActivityAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvComingUp.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComingUp.adapter = comingUpAdapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadDashboard() }

        observeViewModel()
        viewModel.loadDashboard()
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.fullName
                binding.tvVolunteerId.text = getString(R.string.volunteer_id_label, user.volunteerId)
            }
        }
        viewModel.todaysActivities.observe(viewLifecycleOwner) { activities ->
            todaysAdapter.submitList(activities.map { ActivityListItem(it) })
            binding.tvEmptyToday.isVisible = activities.isEmpty()
            binding.tvActivitiesToday.text = getString(R.string.activities_scheduled_today, activities.size)
        }
        viewModel.comingUpActivities.observe(viewLifecycleOwner) { activities ->
            comingUpAdapter.submitList(activities.map { ActivityListItem(it) })
        }
        viewModel.assignedCount.observe(viewLifecycleOwner) { count ->
            binding.statAssigned.tvStatValue.text = count.toString()
            binding.statAssigned.tvStatLabel.text = getString(R.string.stat_assigned)
        }
        viewModel.todayCount.observe(viewLifecycleOwner) { count ->
            binding.statToday.tvStatValue.text = count.toString()
            binding.statToday.tvStatLabel.text = getString(R.string.stat_today)
        }
        viewModel.completedCount.observe(viewLifecycleOwner) { count ->
            binding.statCompleted.tvStatValue.text = count.toString()
            binding.statCompleted.tvStatLabel.text = getString(R.string.stat_completed)
        }
        viewModel.impactStats.observe(viewLifecycleOwner) { stats ->
            binding.tvImpactBanner.text = getString(R.string.impact_banner_text, stats?.familiesFed ?: 0)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
