package com.example.biblioteca_nazionale.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.biblioteca_nazionale.R
import com.example.biblioteca_nazionale.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = auth.currentUser
        val email = currentUser?.email

        binding.currentEmail.text = email

        binding.updateButtonFrag.setOnClickListener {
            updateAll()
        }

        binding.toolbarMyProfile.setOnMenuItemClickListener {
            showLogoutConfirmationDialog()
            return@setOnMenuItemClickListener true
        }

        /*binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }*/
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Logout")
            .setMessage("Are you sure you want to disconnect?")
            .setPositiveButton("Confirm") { dialog, which ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun updateAll() {
        val user = auth.currentUser
        val newEmail = binding.editTextTextEmailAddress.text.toString()
        val newPassword = binding.editTextTextPassword.text.toString()

        if (!binding.editTextTextEmailAddress.text.isEmpty() && !binding.editTextTextPassword.text.isEmpty()) {
            user?.updateEmail(newEmail)
                ?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Toast.makeText(context, "Email changed succesfully!", Toast.LENGTH_SHORT).show()

                        user.updatePassword(newPassword)
                            .addOnCompleteListener { passwordTask ->
                                if (passwordTask.isSuccessful) {
                                    Toast.makeText(context, "Password changed succesfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Password change operation not completed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        val navController = Navigation.findNavController(binding.root)
                        navController.navigate(R.id.action_profileInfoFragment_to_credentialUpdated)
                    } else {
                        Toast.makeText(context, "Credential not changed, problems occured", Toast.LENGTH_SHORT).show()
                    }
                }
        } else if (!binding.editTextTextEmailAddress.text.isEmpty() && binding.editTextTextPassword.text.isEmpty()) {
            user?.updateEmail(newEmail)
                ?.addOnCompleteListener { emailTask ->
                    if (emailTask.isSuccessful) {
                        Toast.makeText(context, "Email changed succesfully!", Toast.LENGTH_SHORT).show()
                        val action = ProfileFragmentDirections.actionProfileInfoFragmentToCredentialUpdated()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(context, "Email not changed, problems occured!", Toast.LENGTH_SHORT).show()
                    }
                }
        } else if (binding.editTextTextEmailAddress.text.isEmpty() && !binding.editTextTextPassword.text.isEmpty()) {
            if (binding.editTextTextPassword.text.toString() == binding.editTextTextPassword2.text.toString()) {
                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            Toast.makeText(context, "Password changed succesfully!", Toast.LENGTH_SHORT).show()
                            val action = ProfileFragmentDirections.actionProfileInfoFragmentToCredentialUpdated()
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(context, "Password not changed, problems occured!", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Password must have the same lenght and characters", Toast.LENGTH_SHORT).show()
            }
        } else if (binding.editTextTextEmailAddress.text.isEmpty() && binding.editTextTextPassword.text.isEmpty()) {
            Toast.makeText(context, "No new credential inserted", Toast.LENGTH_SHORT).show()
        }
    }

    fun logout(){
        auth.signOut()
        val navController = Navigation.findNavController(binding.root)
        navController.navigate(R.id.action_profileInfoFragment_to_mainActivity)
    }
}