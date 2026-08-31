package com.philisa.volunteers.admin.volunteers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.VolunteerApplication
import com.philisa.volunteers.databinding.FragmentManageVolunteersBinding
import com.philisa.volunteers.navigation.AdminNavGraph

class ManageVolunteersFragment : Fragment() {

    private var _binding: FragmentManageVolunteersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VolunteersViewModel by activityViewModels()

    private lateinit var adapter: VolunteerApplicationAdapter
    private var allApplications: List<VolunteerApplication> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageVolunteersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VolunteerApplicationAdapter(emptyList()) { application ->
            AdminNavGraph.toVolunteerDetails(findNavController(), application.id)
        }
        binding.rvVolunteers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVolunteers.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadApplications() }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = applyFilter(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        viewModel.allApplications.observe(viewLifecycleOwner) { applications ->
            allApplications = applications
            updateTabLabels()
            applyFilter(binding.tabLayout.selectedTabPosition.coerceAtLeast(0))
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadApplications()
    }

    private fun updateTabLabels() {
        binding.tabLayout.getTabAt(0)?.text = getString(R.string.tab_all, allApplications.size)
        binding.tabLayout.getTabAt(1)?.text = getString(R.string.tab_pending, allApplications.count { it.status == VolunteerApplication.STATUS_PENDING })
        binding.tabLayout.getTabAt(2)?.text = getString(R.string.tab_approved, allApplications.count { it.status == VolunteerApplication.STATUS_APPROVED })
        binding.tabLayout.getTabAt(3)?.text = getString(R.string.tab_rejected, allApplications.count { it.status == VolunteerApplication.STATUS_REJECTED })
    }

    private fun applyFilter(position: Int) {
        val filtered = when (position) {
            1 -> allApplications.filter { it.status == VolunteerApplication.STATUS_PENDING }
            2 -> allApplications.filter { it.status == VolunteerApplication.STATUS_APPROVED }
            3 -> allApplications.filter { it.status == VolunteerApplication.STATUS_REJECTED }
            else -> allApplications
        }
        adapter.submitList(filtered)
        binding.tvEmpty.isVisible = filtered.isEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
