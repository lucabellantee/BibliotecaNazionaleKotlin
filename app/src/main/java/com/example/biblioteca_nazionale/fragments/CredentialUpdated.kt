package com.example.biblioteca_nazionale.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.biblioteca_nazionale.databinding.FragmentCredentialUpdatedBinding

class CredentialUpdated : Fragment() {

    lateinit var binding: FragmentCredentialUpdatedBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCredentialUpdatedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regButtonLayReg.setOnClickListener {
            val action = CredentialUpdatedDirections.actionCredentialUpdatedToProfileInfoFragment()
            findNavController().navigate(action)
        }
    }

}