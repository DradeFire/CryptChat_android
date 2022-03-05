package com.example.cryptchat.fragments.chat

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptchat.MainActivity
import com.example.cryptchat.R
import com.example.cryptchat.databinding.FragmentChatBinding
import com.example.cryptchat.viewmodel.MainViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

@RequiresApi(Build.VERSION_CODES.O)
class ChatFragment : Fragment() {

    private companion object{
        const val req = 101
    }

    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: MainViewModel
    private var chatName: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        (requireActivity() as MainActivity).supportActionBar?.title = "${viewModel.nameOfChatPerson}"
        chatName = viewModel.dbAPI?.getHashcodeForChat(
            viewModel.nameOfChatPerson!!
        )!!

        bindButtons()

        return binding.root
    }

    private fun bindObs() = with(viewModel.dbAPI) {
        this?.getChats()
            ?.child("$chatName")
            ?.child("key")
            ?.get()?.addOnCompleteListener { aesKeyToDecrypt ->
                if(aesKeyToDecrypt.result?.value != null){
                    viewModel.aesKey?.observe(viewLifecycleOwner){
                        bindRcView()
                    }
                } else{
                    bindRcView()
                }
            }

    }

    private fun bindButtons() = with(binding){
        btStartChat.setOnClickListener {
            viewModel.dbAPI?.startChat(
                chatName = chatName
            )
            Thread.sleep(1500)

            btStartChat.visibility = View.GONE
            startBackground.visibility = View.GONE

            bindObs()
            observeEndChat()
        }

        btSendMessage.setOnClickListener {
            if (inputSendMessage.text.isNotEmpty() and inputSendMessage.text.isNotBlank()){
                val mess = inputSendMessage.text.toString()
                viewModel.dbAPI?.sendMessage(
                    message = mess,
                    chatName = chatName
                )

                inputSendMessage.text.clear()
                scrollToEnd()
            }
        }

        btSendImage.setOnClickListener {
            getAndSendImg()
        }

    }

    private fun getAndSendImg() {
        Toast.makeText(requireActivity(), getString(R.string.choose_image_and_wait), Toast.LENGTH_LONG).show()
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, req)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            sendImage(data)
        } else {
            Toast.makeText(requireActivity(), getString(R.string.you_havent_picked_image), Toast.LENGTH_LONG).show()
        }
    }

    private fun sendImage(data: Intent?) {
        try {
            // get Img
            val imageUri: Uri = data?.data!!
            val imageStream: InputStream = context?.contentResolver?.openInputStream(imageUri)!!
            val selectedImage = BitmapFactory.decodeStream(imageStream) // Image

            // convert Img to ByteArray
            val baos = ByteArrayOutputStream()
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
            val b: ByteArray = baos.toByteArray()

            // Convert Img(ByteArray) to String
            val encodedImage: String = (Base64.encodeToString(b, Base64.DEFAULT))
            viewModel.dbAPI?.sendMessage(encodedImage, chatName, true)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
        }
    }

    private fun observeEndChat() = with(viewModel.dbAPI) {
        this?.getChats()
            ?.child("$chatName")
            ?.addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.value == null) {
                            binding.btStartChat.visibility = View.VISIBLE
                            binding.startBackground.visibility = View.VISIBLE
                        } else {
                            binding.btStartChat.visibility = View.GONE
                            binding.startBackground.visibility = View.GONE
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                }

            )
    }

    private fun bindRcView() = with(viewModel.dbAPI) {
        this?.getChats()
            ?.child("$chatName")
            ?.get()
            ?.addOnCompleteListener { messages ->
                bindRcAdapter(messages)
            }

        this?.getChats()
            ?.child("$chatName")
            ?.addValueEventListener(
                object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(binding.rcViewChatMessage.adapter is ChatAdapter) {
                        (binding.rcViewChatMessage.adapter as ChatAdapter).changeListOfChats(dataSnapshot.children)
                        scrollToEnd()
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun bindRcAdapter(messages: Task<DataSnapshot>) = with(binding.rcViewChatMessage) {
        adapter = ChatAdapter(
                messages.result!!.children,
                viewModel
            )
        layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun scrollToEnd() = with(binding.rcViewChatMessage) {
        scrollToPosition(
            (binding.rcViewChatMessage.adapter as ChatAdapter).itemCount - 1
        )
    }

    override fun onDestroyView() = with(viewModel) {
        aesKey?.removeObservers(viewLifecycleOwner)
        dbAPI?.finishChat(chatName)
        super.onDestroyView()
    }

}