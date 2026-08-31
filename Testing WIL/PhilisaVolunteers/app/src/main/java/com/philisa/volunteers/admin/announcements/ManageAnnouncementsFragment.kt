package com.philisa.volunteers.admin.announcements

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Announcement
import com.philisa.volunteers.databinding.FragmentManageAnnouncementsBinding
import com.philisa.volunteers.navigation.AdminNavGraph

class ManageAnnouncementsFragment : Fragment() {

    private var _binding: FragmentManageAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnnouncementsViewModel by activityViewModels()

    private lateinit var adapter: ManageAnnouncementAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageAnnouncementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ManageAnnouncementAdapter(
            emptyList(),
            onTogglePublish = { viewModel.togglePublish(it) },
            onDelete = { confirmDelete(it) }
        )
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = adapter

        binding.btnAdd.setOnClickListener { AdminNavGraph.toCreateAnnouncement(findNavController()) }
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadAnnouncements() }

        viewModel.announcements.observe(viewLifecycleOwner) { announcements ->
            adapter.submitList(announcements)
            binding.tvEmpty.isVisible = announcements.isEmpty()
            val published = announcements.count { it.status == Announcement.STATUS_PUBLISHED }
            val drafts = announcements.count { it.status == Announcement.STATUS_DRAFT }
            binding.tvSummary.text = getString(R.string.published_drafts_summary, published, drafts)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadAnnouncements()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAnnouncements()
    }

    private fun confirmDelete(announcement: Announcement) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_announcement_confirm_title)
            .setPositiveButton(R.string.action_remove) { _, _ -> viewModel.deleteAnnouncement(announcement) }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
