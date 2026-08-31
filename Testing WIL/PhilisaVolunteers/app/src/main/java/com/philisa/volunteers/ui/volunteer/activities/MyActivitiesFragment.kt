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
import com.philisa.volunteers.databinding.FragmentMyActivitiesBinding
import com.philisa.volunteers.navigation.VolunteerNavGraph

class MyActivitiesFragment : Fragment() {

    private var _binding: FragmentMyActivitiesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ActivitiesViewModel by viewModels({ requireParentFragment() })

    private lateinit var adapter: ActivityAdapter
    private lateinit var filter: ActivityTimeFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filter = ActivityTimeFilter.valueOf(requireArguments().getString(ARG_FILTER)!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyActivitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ActivityAdapter(emptyList()) { activity ->
            VolunteerNavGraph.toActivityDetails(findNavController(), activity.id)
        }
        binding.rvMyActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyActivities.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadMyActivities(filter) }

        viewModel.myActivities.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmptyMyActivities.isVisible = items.isEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.loadMyActivities(filter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_FILTER = "arg_filter"

        fun newInstance(filter: ActivityTimeFilter): MyActivitiesFragment {
            return MyActivitiesFragment().apply {
                arguments = Bundle().apply { putString(ARG_FILTER, filter.name) }
            }
        }
    }
}
