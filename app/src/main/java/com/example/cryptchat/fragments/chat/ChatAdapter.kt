package com.example.cryptchat.fragments.chat

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptchat.crypt.AES
import com.example.cryptchat.databinding.ItemChatMessageBinding
import com.example.cryptchat.viewmodel.MainViewModel
import com.google.firebase.database.DataSnapshot

@RequiresApi(Build.VERSION_CODES.O)
class ChatAdapter(
    listOfChatsOut: MutableIterable<DataSnapshot>,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<ChatAdapter.ItemViewHolder>() {

    private var listOfChats = listOfChatsOut.toMutableList()

    class ItemViewHolder(private val binding: ItemChatMessageBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            message: DataSnapshot,
            viewModel: MainViewModel
        ) {
            if((message.key.toString() != "key") and (message.key.toString() != "isStarted")){
                binding.txChatPerson.text = (message.value as HashMap<String, String>).keys.elementAt(0) //Nickname

                val mess = (message.value as HashMap<String, String>)[
                        (message.value as HashMap<String, String>).keys.elementAt(0)
                ]!!

                if(mess.startsWith("img: ")){
                    binding.txMessage.visibility = View.GONE
                    decodeAndSetImg(mess.substring(5), viewModel.aesKey?.value!!)
                    binding.imMessage.visibility = View.VISIBLE
                } else {
                    binding.imMessage.visibility = View.GONE
                    binding.txMessage.text = AES.decrypt(
                        mess,
                        viewModel.aesKey?.value!!
                    )
                    binding.txMessage.visibility = View.VISIBLE
                }

            }

        }

        private fun decodeAndSetImg(encodedImage: String, aesKey: String){
            val decryptString = AES.decrypt(
                encodedImage,
                aesKey
            )
            val decodedString: ByteArray = Base64.decode(decryptString, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.imMessage.setImageBitmap(decodedByte)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder
        = ItemViewHolder(ItemChatMessageBinding
            .inflate(LayoutInflater
            .from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if((listOfChats[position].key.toString() != "key") and (listOfChats[position].key.toString() != "isStarted"))
            holder.bind(listOfChats[position], viewModel)
    }

    override fun getItemCount(): Int = listOfChats.size

    @SuppressLint("NotifyDataSetChanged")
    fun changeListOfChats(listOfChats: MutableIterable<DataSnapshot>){
        this.listOfChats = listOfChats.toMutableList()
        if(this.listOfChats.size > 2){
            this.listOfChats.removeAt(this.listOfChats.size - 1)
            this.listOfChats.removeAt(this.listOfChats.size - 1)
        }
        notifyDataSetChanged()
    }

}