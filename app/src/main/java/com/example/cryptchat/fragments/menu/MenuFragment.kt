package com.example.cryptchat.fragments.menu

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptchat.MainActivity
import com.example.cryptchat.R
import com.example.cryptchat.databinding.FragmentMenuBinding
import com.example.cryptchat.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        (requireActivity() as MainActivity).supportActionBar?.title = "You: ${viewModel.dbAPI?.getCurrentUser()}" //currentPerson
        setHasOptionsMenu(true)

        bindRcView()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.signout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sign_out -> {
                viewModel.dbAPI?.signOut()
                viewModel.currentPerson = null
                try {
                    findNavController().navigate(R.id.mainMenuFragment)
                } catch (e: Exception){
                    findNavController().navigate(R.id.fragment_main_menu)
                }

            }

        }
        return true
    }

    private fun bindRcView() {
        viewModel
            .dbAPI
            ?.getUsers()
            ?.get()
            ?.addOnCompleteListener { users ->
                binding.rcViewChatPersons.adapter =
                    MenuAdapter(users.result!!.children, findNavController(), viewModel)
                binding.rcViewChatPersons.layoutManager = LinearLayoutManager(requireContext())
            }
    }

}