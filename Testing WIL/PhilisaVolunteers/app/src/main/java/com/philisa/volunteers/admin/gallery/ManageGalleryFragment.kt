package com.philisa.volunteers.admin.gallery

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.GalleryItem
import com.philisa.volunteers.databinding.FragmentManageGalleryBinding

class ManageGalleryFragment : Fragment() {

    private var _binding: FragmentManageGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GalleryViewModel by viewModels()

    private lateinit var adapter: ManageGalleryAdapter

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val caption = binding.etCaption.text?.toString().orEmpty().trim()
            viewModel.uploadPhoto(uri, caption)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ManageGalleryAdapter(emptyList()) { item -> confirmDelete(item) }
        binding.rvGallery.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGallery.adapter = adapter

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnUpload.setOnClickListener { pickImage.launch("image/*") }
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadGallery() }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.isVisible = items.isEmpty()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.swipeRefresh.isRefreshing = isLoading
            binding.btnUpload.isEnabled = !isLoading
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                android.widget.Toast.makeText(requireContext(), getString(R.string.error_generic), android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadGallery()
    }

    private fun confirmDelete(item: GalleryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_photo_confirm_title)
            .setPositiveButton(R.string.action_remove) { _, _ -> viewModel.deletePhoto(item) }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
