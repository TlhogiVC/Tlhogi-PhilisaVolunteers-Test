package com.philisa.volunteers.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.philisa.volunteers.R
import com.philisa.volunteers.data.repository.AuthRepository
import com.philisa.volunteers.databinding.ActivityLoginChoiceBinding
import com.philisa.volunteers.navigation.AuthNavGraph
import com.philisa.volunteers.utils.Constants
import com.philisa.volunteers.utils.ValidationUtils
import kotlinx.coroutines.launch

class LoginChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginChoiceBinding
    private val authRepository = AuthRepository()
    private var selectedRole = Constants.ROLE_VOLUNTEER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.toggleRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            selectedRole = if (checkedId == R.id.btnTabAdmin) Constants.ROLE_ADMIN else Constants.ROLE_VOLUNTEER
            applyRoleStyling()
        }
        applyRoleStyling()

        binding.tvForgotPassword.setOnClickListener { showForgotPasswordDialog() }
        binding.tvApplyHere.setOnClickListener { AuthNavGraph.goToPersonalDetails(this) }
        binding.btnSignIn.setOnClickListener { attemptSignIn() }
    }

    private fun applyRoleStyling() {
        val isAdmin = selectedRole == Constants.ROLE_ADMIN
        binding.tvLoginTitle.text = getString(if (isAdmin) R.string.admin_login_title else R.string.login_title)
        binding.tvLoginSubtitle.text = getString(if (isAdmin) R.string.admin_login_subtitle else R.string.login_subtitle)
        binding.btnSignIn.text = getString(if (isAdmin) R.string.action_sign_in_admin else R.string.action_sign_in_volunteer)
        binding.tvNoticeText.text = getString(if (isAdmin) R.string.admin_login_notice else R.string.volunteer_login_notice)
        binding.layoutNotice.background.setTint(
            getColor(if (isAdmin) R.color.status_pending_bg else R.color.status_info_bg)
        )
        binding.tvNoticeText.setTextColor(
            getColor(if (isAdmin) R.color.status_pending_text else R.color.status_info_text)
        )
        binding.tvApplyHere.isVisible = !isAdmin
        binding.tvLoginError.isVisible = false
    }

    private fun attemptSignIn() {
        val email = binding.etEmail.text?.toString().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        binding.tilEmail.error = if (!ValidationUtils.isValidEmail(email)) getString(R.string.error_invalid_email) else null
        binding.tilPassword.error = if (!ValidationUtils.isNotBlank(password)) getString(R.string.error_field_required) else null
        if (binding.tilEmail.error != null || binding.tilPassword.error != null) return

        setLoading(true)
        lifecycleScope.launch {
            val result = authRepository.signIn(email, password, selectedRole)
            setLoading(false)
            result.onSuccess {
                if (selectedRole == Constants.ROLE_ADMIN) {
                    AuthNavGraph.goToAdminMain(this@LoginChoiceActivity)
                } else {
                    AuthNavGraph.goToVolunteerMain(this@LoginChoiceActivity)
                }
            }.onFailure { error ->
                val message = when (error.message) {
                    "error_account_not_found" -> getString(R.string.error_account_not_found)
                    "error_wrong_role" -> getString(R.string.error_wrong_role, selectedRole)
                    else -> getString(R.string.error_login_failed)
                }
                binding.tvLoginError.text = message
                binding.tvLoginError.isVisible = true
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnSignIn.isEnabled = !isLoading
    }

    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null)
        val etResetEmail = dialogView.findViewById<TextInputEditText>(R.id.etResetEmail)
        etResetEmail.setText(binding.etEmail.text)

        AlertDialog.Builder(this)
            .setTitle(R.string.reset_password_title)
            .setView(dialogView)
            .setPositiveButton(R.string.action_send_reset_link) { _, _ ->
                val email = etResetEmail.text?.toString().orEmpty()
                if (ValidationUtils.isValidEmail(email)) {
                    lifecycleScope.launch {
                        authRepository.sendPasswordResetEmail(email)
                        android.widget.Toast.makeText(this@LoginChoiceActivity, R.string.reset_link_sent, android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
}
