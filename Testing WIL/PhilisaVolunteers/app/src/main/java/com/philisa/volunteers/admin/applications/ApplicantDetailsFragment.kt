package com.philisa.volunteers.admin.applications

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.philisa.volunteers.R
import com.philisa.volunteers.data.model.ActivityApplication
import com.philisa.volunteers.databinding.FragmentApplicantDetailsBinding
import com.philisa.volunteers.utils.Constants

class ApplicantDetailsFragment : Fragment() {

    private var _binding: FragmentApplicantDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ApplicationsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentApplicantDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowEmail.ivRowIcon.setImageResource(R.drawable.ic_email)
        binding.rowEmail.tvRowLabel.text = getString(R.string.label_email_caps)
        binding.rowPhone.ivRowIcon.setImageResource(R.drawable.ic_phone)
        binding.rowPhone.tvRowLabel.text = getString(R.string.label_phone_caps)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.selectedApplicant.observe(viewLifecycleOwner) { applicant -> applicant?.let { bind(it) } }
        viewModel.selectedApplicantUser.observe(viewLifecycleOwner) { user ->
            binding.rowEmail.tvRowValue.text = user?.email.orEmpty()
            binding.rowPhone.tvRowValue.text = user?.phone.orEmpty()
        }

        binding.btnConfirm.setOnClickListener {
            viewModel.selectedApplicant.value?.let { viewModel.confirmApplicant(it) }
            findNavController().popBackStack()
        }
        binding.btnRemove.setOnClickListener { confirmRemove() }

        val activityApplicationId = requireArguments().getString(Constants.EXTRA_ACTIVITY_APPLICATION_ID).orEmpty()
        viewModel.selectApplicant(activityApplicationId)
    }

    private fun bind(applicant: ActivityApplication) {
        binding.tvInitial.text = applicant.volunteerName.take(1).uppercase()
        binding.tvName.text = applicant.volunteerName

        val isConfirmed = applicant.status == ActivityApplication.STATUS_CONFIRMED
        binding.tvStatusPill.text = getString(if (isConfirmed) R.string.status_confirmed else R.string.status_pending)
        binding.tvStatusPill.background.setTint(
            requireContext().getColor(if (isConfirmed) R.color.status_success_bg else R.color.status_pending_bg)
        )
        binding.tvStatusPill.setTextColor(
            requireContext().getColor(if (isConfirmed) R.color.status_success_text else R.color.status_pending_text)
        )
        binding.btnConfirm.isVisible = !isConfirmed
    }

    private fun confirmRemove() {
        val applicant = viewModel.selectedApplicant.value ?: return
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.remove_applicant_confirm_title)
            .setMessage(getString(R.string.remove_applicant_confirm_message, applicant.volunteerName))
            .setPositiveButton(R.string.action_remove) { _, _ ->
                viewModel.removeApplicant(applicant)
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
