package com.philisa.volunteers.ui.volunteer.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.data.repository.AnnouncementRepository
import com.philisa.volunteers.databinding.FragmentAnnouncementDetailsBinding
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.DateUtils
import kotlinx.coroutines.launch

class AnnouncementDetailsFragment : Fragment() {

    private var _binding: FragmentAnnouncementDetailsBinding? = null
    private val binding get() = _binding!!
    private val announcementRepository = AnnouncementRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnnouncementDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val announcementId = requireArguments().getString(Constants.EXTRA_ANNOUNCEMENT_ID).orEmpty()
        lifecycleScope.launch {
            val announcement = announcementRepository.getAllAnnouncements().firstOrNull { it.id == announcementId }
            if (announcement != null) {
                binding.tvDate.text = DateUtils.formatDate(announcement.date)
                binding.tvTitle.text = announcement.title
                binding.tvBody.text = announcement.messageBody
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
