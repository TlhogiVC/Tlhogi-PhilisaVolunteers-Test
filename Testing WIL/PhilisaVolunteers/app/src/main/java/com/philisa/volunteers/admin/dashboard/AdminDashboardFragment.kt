package com.philisa.volunteers.admin.dashboard

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
import com.philisa.volunteers.admin.volunteers.VolunteerApplicationAdapter
import com.philisa.volunteers.databinding.FragmentAdminDashboardBinding
import com.philisa.volunteers.navigation.AdminNavGraph

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminDashboardViewModel by viewModels()

    private lateinit var adapter: VolunteerApplicationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = VolunteerApplicationAdapter(emptyList()) { application ->
            AdminNavGraph.toVolunteerDetails(findNavController(), application.id)
        }
        binding.rvRecentApplications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentApplications.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadDashboard() }
        binding.btnViewAllApplications.setOnClickListener {
            findNavController().navigate(R.id.manageVolunteersFragment)
        }

        setupQuickAction(binding.actionCreateActivity, R.drawable.ic_add, R.string.action_create_new_activity, R.string.action_create_new_activity_desc) {
            AdminNavGraph.toCreateActivity(findNavController())
        }
        setupQuickAction(binding.actionPostAnnouncement, R.drawable.ic_admin_posts, R.string.action_post_announcement, R.string.action_post_announcement_desc) {
            AdminNavGraph.toCreateAnnouncement(findNavController())
        }
        setupQuickAction(binding.actionViewSignups, R.drawable.ic_admin_applied, R.string.action_view_signups, R.string.action_view_signups_desc) {
            findNavController().navigate(R.id.manageActivityApplicationsFragment)
        }
        setupQuickAction(binding.actionManageImpactStats, R.drawable.ic_admin_overview, R.string.action_manage_impact_stats, R.string.action_manage_impact_stats_desc) {
            AdminNavGraph.toManageImpactStats(findNavController())
        }
        setupQuickAction(binding.actionManageGallery, R.drawable.ic_photo, R.string.action_manage_gallery, R.string.action_manage_gallery_desc) {
            AdminNavGraph.toManageGallery(findNavController())
        }

        observeViewModel()
        viewModel.loadDashboard()
    }

    private fun setupQuickAction(
        row: com.philisa.volunteers.databinding.ItemQuickActionBinding,
        iconRes: Int,
        titleRes: Int,
        subtitleRes: Int,
        onClick: () -> Unit
    ) {
        row.ivActionIcon.setImageResource(iconRes)
        row.tvActionTitle.text = getString(titleRes)
        row.tvActionSubtitle.text = getString(subtitleRes)
        row.root.setOnClickListener { onClick() }
    }

    private fun observeViewModel() {
        viewModel.pendingCount.observe(viewLifecycleOwner) { count ->
            binding.layoutPendingBanner.isVisible = count > 0
            binding.tvPendingBanner.text = getString(R.string.applications_awaiting_review, count)
        }
        viewModel.totalVolunteers.observe(viewLifecycleOwner) { count ->
            binding.statTotalVolunteers.tvStatValue.text = count.toString()
            binding.statTotalVolunteers.tvStatLabel.text = getString(R.string.stat_total_volunteers)
        }
        viewModel.publishedActivities.observe(viewLifecycleOwner) { count ->
            binding.statPublishedActivities.tvStatValue.text = count.toString()
            binding.statPublishedActivities.tvStatLabel.text = getString(R.string.stat_published_activities)
        }
        viewModel.announcementsCount.observe(viewLifecycleOwner) { count ->
            binding.statAnnouncements.tvStatValue.text = count.toString()
            binding.statAnnouncements.tvStatLabel.text = getString(R.string.stat_announcements)
        }
        viewModel.totalActivityApplications.observe(viewLifecycleOwner) { count ->
            binding.statActivityApplications.tvStatValue.text = count.toString()
            binding.statActivityApplications.tvStatLabel.text = getString(R.string.stat_activity_applications)
        }
        viewModel.recentApplications.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
