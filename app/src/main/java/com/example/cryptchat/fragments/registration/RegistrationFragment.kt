package com.example.cryptchat.fragments.registration

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cryptchat.R
import com.example.cryptchat.const.Consts.ERROR_INPUT
import com.example.cryptchat.databinding.FragmentRegistrationBinding
import com.example.cryptchat.user.User
import com.example.cryptchat.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.btRegister.setOnClickListener {
            if(binding.inputNicknameReg.text.isNotEmpty() and binding.inputPasswordReg.text.isNotEmpty()){
                registration(binding.inputNicknameReg.text.toString(), binding.inputPasswordReg.text.toString())
            } else {
                Toast.makeText(requireActivity(),
                    ERROR_INPUT,
                    Toast.LENGTH_SHORT)
                    .show()
            }

        }

        return binding.root
    }

    private fun registration(nickname: String, password: String) {
        viewModel.dbAPI?.signUp(
            User(nickName = nickname, password = password)
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Thread.sleep(1000)

                try {
                    findNavController().navigate(R.id.mainMenuFragment)
                } catch (e: Exception) {
                    findNavController().navigate(R.id.fragment_main_menu)
                }
            }

        }

    }

}