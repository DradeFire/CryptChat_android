package com.example.cryptchat.fragments.login

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
import com.example.cryptchat.databinding.FragmentLoginBinding
import com.example.cryptchat.user.User
import com.example.cryptchat.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if (viewModel.nickname_ != null) {
            binding.inputNicknameLogin.setText(viewModel.nickname_)
        }
        if (viewModel.password_ != null) {
            binding.inputPasswordLogin.setText(viewModel.password_)
        }

        binding.btSignIn.setOnClickListener {
            if(binding.inputNicknameLogin.text.isNotEmpty() and binding.inputPasswordLogin.text.isNotEmpty()){
                login(binding.inputNicknameLogin.text.toString(), binding.inputPasswordLogin.text.toString())
            } else {
                Toast.makeText(requireActivity(),
                    ERROR_INPUT,
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }

    private fun login(nickname: String, password: String) {
        viewModel.dbAPI?.signIn(
            User(nickName = nickname, password = password)
        )
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Thread.sleep(200)
                    viewModel.currentPerson = nickname

                    try {
                        findNavController().navigate(R.id.menuFragment)
                    } catch (e: Exception){
                        findNavController().navigate(R.id.fragment_menu)
                    }
                }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.nickname_ = binding.inputNicknameLogin.text.toString()
        viewModel.password_ = binding.inputPasswordLogin.text.toString()
        super.onSaveInstanceState(outState)
    }

}