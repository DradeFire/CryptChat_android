package com.example.cryptchat.fragments.main_menu

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cryptchat.R
import com.example.cryptchat.databinding.FragmentMainMenuBinding
import com.example.cryptchat.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if(viewModel.dbAPI?.isAuthCorrect()!!){
            try {
                findNavController().navigate(R.id.menuFragment)
            } catch (e: Exception) {
                findNavController().navigate(R.id.fragment_menu)
            }
        }

        bindButtons()

        return binding.root
    }

    private fun bindButtons() {
        binding.btToLogin.setOnClickListener {
            try {
                findNavController().navigate(R.id.loginFragment)
            } catch (e: Exception) {
                findNavController().navigate(R.id.fragment_login)
            }
        }

        binding.btToRegistration.setOnClickListener {
            try {
                findNavController().navigate(R.id.registrationFragment)
            } catch (e: Exception) {
                findNavController().navigate(R.id.fragment_reg)
            }
        }
    }

}