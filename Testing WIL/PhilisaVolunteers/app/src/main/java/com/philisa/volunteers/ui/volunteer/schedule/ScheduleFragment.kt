package com.philisa.volunteers.ui.volunteer.schedule

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
import com.philisa.volunteers.databinding.FragmentScheduleBinding
import com.philisa.volunteers.navigation.VolunteerNavGraph

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by viewModels()

    private lateinit var todayAdapter: ScheduleAdapter
    private lateinit var weekAdapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayAdapter = ScheduleAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvToday.layoutManager = LinearLayoutManager(requireContext())
        binding.rvToday.adapter = todayAdapter

        weekAdapter = ScheduleAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvThisWeek.layoutManager = LinearLayoutManager(requireContext())
        binding.rvThisWeek.adapter = weekAdapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadSchedule() }

        viewModel.todaysActivities.observe(viewLifecycleOwner) { todayAdapter.submitList(it) }
        viewModel.thisWeekActivities.observe(viewLifecycleOwner) { activities ->
            weekAdapter.submitList(activities)
            binding.tvEmptySchedule.isVisible = activities.isEmpty()
            binding.tvScheduleSummary.text = getString(
                R.string.schedule_summary,
                viewModel.todaysActivities.value?.size ?: 0,
                activities.size
            )
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadSchedule()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
