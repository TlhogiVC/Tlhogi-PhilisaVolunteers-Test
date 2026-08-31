package com.philisa.volunteers.ui.volunteer.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.databinding.FragmentCommunityBinding
import com.philisa.volunteers.navigation.VolunteerNavGraph

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommunityViewModel by viewModels()

    private lateinit var announcementAdapter: AnnouncementAdapter
    private lateinit var galleryAdapter: GalleryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        announcementAdapter = AnnouncementAdapter(emptyList()) { announcement ->
            VolunteerNavGraph.toAnnouncementDetails(findNavController(), announcement.id)
        }
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = announcementAdapter

        galleryAdapter = GalleryAdapter(emptyList())
        binding.rvGallery.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGallery.adapter = galleryAdapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadCommunity() }

        viewModel.impactStats.observe(viewLifecycleOwner) { stats ->
            binding.statFamiliesFed.tvStatValue.text = (stats?.familiesFed ?: 0).toString()
            binding.statFamiliesFed.tvStatLabel.text = getString(R.string.stat_families_fed)
            binding.statYouthMentored.tvStatValue.text = (stats?.youthMentored ?: 0).toString()
            binding.statYouthMentored.tvStatLabel.text = getString(R.string.stat_youth_mentored)
            binding.statSafeHouseIntakes.tvStatValue.text = (stats?.safeHouseIntakes ?: 0).toString()
            binding.statSafeHouseIntakes.tvStatLabel.text = getString(R.string.stat_safe_house_intakes)
            binding.statSeniorsVisited.tvStatValue.text = (stats?.seniorsVisited ?: 0).toString()
            binding.statSeniorsVisited.tvStatLabel.text = getString(R.string.stat_seniors_visited)

            binding.tvVolunteerOfMonth.text = if (stats?.volunteerOfMonthName.isNullOrBlank()) {
                ""
            } else {
                getString(
                    R.string.volunteer_of_month_line,
                    stats?.volunteerOfMonthName,
                    stats?.volunteerOfMonthProgramme,
                    stats?.volunteerOfMonthActivityCount ?: 0
                )
            }
        }
        viewModel.announcements.observe(viewLifecycleOwner) { announcements ->
            announcementAdapter.submitList(announcements)
            binding.tvEmptyAnnouncements.isVisible = announcements.isEmpty()
        }
        viewModel.galleryItems.observe(viewLifecycleOwner) { items ->
            galleryAdapter.submitList(items)
            binding.tvEmptyGallery.isVisible = items.isEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.loadCommunity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
