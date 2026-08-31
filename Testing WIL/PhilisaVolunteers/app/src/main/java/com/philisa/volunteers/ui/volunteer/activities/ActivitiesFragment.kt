package com.philisa.volunteers.ui.volunteer.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.philisa.volunteers.databinding.FragmentActivitiesBinding
import com.philisa.volunteers.navigation.VolunteerNavGraph

class ActivitiesFragment : Fragment() {

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ActivitiesViewModel by viewModels()

    private lateinit var applyAdapter: ActivityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyAdapter = ActivityAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvApply.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApply.adapter = applyAdapter

        binding.swipeRefreshApply.setOnRefreshListener { viewModel.loadAvailableActivities() }

        viewModel.publishedActivities.observe(viewLifecycleOwner) { items ->
            applyAdapter.submitList(items)
            binding.tvEmptyApply.isVisible = items.isEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshApply.isRefreshing = isLoading
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showTab(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        showTab(0)
        viewModel.loadAvailableActivities()
    }

    private fun showTab(position: Int) {
        if (position == 0) {
            binding.swipeRefreshApply.isVisible = true
            binding.myActivitiesContainer.isVisible = false
            viewModel.loadAvailableActivities()
        } else {
            binding.swipeRefreshApply.isVisible = false
            binding.myActivitiesContainer.isVisible = true
            val filter = when (position) {
                1 -> ActivityTimeFilter.TODAY
                2 -> ActivityTimeFilter.UPCOMING
                else -> ActivityTimeFilter.COMPLETED
            }
            childFragmentManager.beginTransaction()
                .replace(binding.myActivitiesContainer.id, MyActivitiesFragment.newInstance(filter))
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
