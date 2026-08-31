package com.philisa.volunteers.admin.activities

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
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.databinding.FragmentManageActivitiesBinding
import com.philisa.volunteers.navigation.AdminNavGraph

class ManageActivitiesFragment : Fragment() {

    private var _binding: FragmentManageActivitiesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminActivitiesViewModel by activityViewModels()

    private lateinit var adapter: ManageActivityAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ManageActivityAdapter(
            emptyList(),
            onTogglePublish = { viewModel.togglePublish(it) },
            onEdit = { AdminNavGraph.toEditActivity(findNavController(), it.id) },
            onDelete = { confirmDelete(it) }
        )
        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter

        binding.btnAdd.setOnClickListener { AdminNavGraph.toCreateActivity(findNavController()) }
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadActivities() }

        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            adapter.submitList(activities)
            binding.tvEmpty.isVisible = activities.isEmpty()
            val published = activities.count { it.status == Activity.STATUS_PUBLISHED }
            val drafts = activities.count { it.status == Activity.STATUS_DRAFT }
            binding.tvSummary.text = getString(R.string.published_drafts_summary, published, drafts)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadActivities()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadActivities()
    }

    private fun confirmDelete(activity: Activity) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_activity_confirm_title)
            .setMessage(R.string.delete_activity_confirm_message)
            .setPositiveButton(R.string.action_remove) { _, _ -> viewModel.deleteActivity(activity) }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
