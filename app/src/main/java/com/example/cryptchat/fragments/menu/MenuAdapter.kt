package com.example.cryptchat.fragments.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptchat.R
import com.example.cryptchat.databinding.ItemChatPersonBinding
import com.example.cryptchat.viewmodel.MainViewModel
import com.google.firebase.database.DataSnapshot

class MenuAdapter(
    listOfUsersOut: MutableIterable<DataSnapshot>,
    private val findNavController: NavController,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<MenuAdapter.ItemViewHolder>() {

    private var listOfUsers = listOfUsersOut.toList()

    class ItemViewHolder(private val binding: ItemChatPersonBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: DataSnapshot,
                 findNavController: NavController,
                 viewModel: MainViewModel) {
            binding.txChatPerson.text = user.key

            binding.root.setOnClickListener {
                viewModel.nameOfChatPerson = user.key
                try {
                    findNavController.navigate(R.id.chatFragment)
                } catch (e: Exception) {
                    findNavController.navigate(R.id.fragment_chat)
                }

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder
        = ItemViewHolder(ItemChatPersonBinding
            .inflate(LayoutInflater
            .from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(listOfUsers[position], findNavController, viewModel)
    }

    override fun getItemCount(): Int = listOfUsers.size

//    @SuppressLint("NotifyDataSetChanged")
//    fun changeListOfUsers(listOfUsers: MutableIterable<DataSnapshot>){
//        this.listOfUsers = listOfUsers.toList()
//        notifyDataSetChanged()
//    }

}