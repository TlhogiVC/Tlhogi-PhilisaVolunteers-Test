package com.philisa.volunteers.admin.applications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.Activity
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.databinding.FragmentManageActivityApplicationsBinding
import com.philisa.volunteers.navigation.AdminNavGraph

class ManageActivityApplicationsFragment : Fragment() {

    private var _binding: FragmentManageActivityApplicationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApplicationsViewModel by activityViewModels()

    private lateinit var applicantAdapter: ActivityApplicantAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private var activityList: List<Activity> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageActivityApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applicantAdapter = ActivityApplicantAdapter(emptyList()) { applicant ->
            AdminNavGraph.toApplicantDetails(findNavController(), applicant.id)
        }
        binding.rvApplicants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApplicants.adapter = applicantAdapter

        spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mutableListOf())
        binding.spinnerActivity.adapter = spinnerAdapter
        binding.spinnerActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activityList.getOrNull(position)?.let { viewModel.selectActivity(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadActivities() }

        viewModel.activities.observe(viewLifecycleOwner) { activities ->
            activityList = activities
            spinnerAdapter.clear()
            spinnerAdapter.addAll(activities.map { it.title })
            spinnerAdapter.notifyDataSetChanged()
        }
        viewModel.selectedActivity.observe(viewLifecycleOwner) { activity -> activity?.let { bindActivity(it) } }
        viewModel.applicants.observe(viewLifecycleOwner) { applicants ->
            applicantAdapter.submitList(applicants)
            binding.tvEmpty.isVisible = applicants.isEmpty()
            viewModel.selectedActivity.value?.let { bindActivity(it) }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadActivities()
    }

    private fun bindActivity(activity: Activity) {
        binding.tvActivityTitle.text = activity.title
        val applicants = viewModel.applicants.value.orEmpty()
        val confirmed = applicants.count { it.status == ActivityApplication.STATUS_CONFIRMED }
        val pending = applicants.count { it.status == ActivityApplication.STATUS_PENDING }
        binding.tvActivitySummary.text = getString(
            R.string.applicants_summary,
            applicants.size,
            confirmed,
            pending,
            activity.spotsRemaining
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.selectedActivity.value?.let { viewModel.selectActivity(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
