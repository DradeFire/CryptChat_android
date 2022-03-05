package com.example.cryptchat.api

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.cryptchat.MainActivity
import com.example.cryptchat.crypt.AES
import com.example.cryptchat.user.User
import com.example.cryptchat.viewmodel.MainViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashSet

@RequiresApi(Build.VERSION_CODES.O)
class FirebaseApi(
    private val activity: MainActivity,
    private val viewModel: MainViewModel
) {
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: User? = null

    fun isAuthCorrect(): Boolean {
        val some = mAuth.currentUser?.displayName
        return (some != null) and (some != "")
    }

    private fun setCurrentUser(user: User){
        this.user = user
    }
    fun getCurrentUser() = user?.nickName ?: mAuth.currentUser?.displayName

    fun signIn(user: User): Task<AuthResult> {
        setCurrentUser(user)

        return mAuth
            .signInWithEmailAndPassword(user.nickName + "@gmail.com", user.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    db.getReference("users")
                        .child(user.nickName)
                        .child("public_key")
                        .setValue(viewModel.rsaCrypt?.getPublicKey())
                } else {
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun signUp(user: User): Task<AuthResult> = mAuth
        .createUserWithEmailAndPassword(user.nickName + "@gmail.com", user.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    getUsers()
                        .child(user.nickName)
                        .setValue(user.nickName)

                    Toast.makeText(
                        activity, "Successful sign up!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        activity, "Registration failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    fun signOut(){
        mAuth.signOut()
    }

    fun getUsers() = db.getReference("users")
    fun getChats() = db.getReference("chats")

    fun sendMessage(message: String, chatName: Int, isImage: Boolean = false){
        val cryptMess = if(isImage)
            "img: " +  AES.encrypt(message, viewModel.aesKey?.value!!)
        else
            AES.encrypt(message, viewModel.aesKey?.value!!)

        getChats()
            .child("$chatName")
            .child("${Date().time}")
            .child(user?.nickName!!)
            .setValue(cryptMess)
    }

    fun getHashcodeForChat(consumer: String): Int {
        val nick = getCurrentUser()!!

        val chatName = HashSet<String>()
        chatName.add(consumer)
        chatName.add(nick)
        return chatName.hashCode()
    }

    fun startChat(chatName: Int){
        getChats()
            .child("$chatName")
            .get()
            .addOnSuccessListener {
                startChatNow(it, chatName)
            }
    }

    private fun startChatNow(it: DataSnapshot, chatName: Int) {
        if(it.child("isStarted").value == null) {
            // Если Б
            getChats()
                .child("$chatName")
                .child("isStarted")
                .setValue(true)

            Thread.sleep(800)

            getUsers()
                .child(viewModel.nameOfChatPerson!!)
                .child("public_key")
                .get()
                .addOnCompleteListener { pbKey ->
                    val cryptAESkey = viewModel.rsaCrypt?.encryptMessage(
                        pb_key = pbKey.result?.value.toString(),
                        plainText = viewModel.aesKey?.value!!
                    )

                    getChats()
                        .child("$chatName")
                        .child("key")
                        .setValue(cryptAESkey)
                }
        } else {
            // Если А
            getChats()
                .child("$chatName")
                .child("key")
                .get().addOnCompleteListener { aesKeyToDecrypt ->
                    viewModel.aesKey?.value =
                        viewModel.rsaCrypt?.decryptMessage(aesKeyToDecrypt.result?.value.toString())
                }
        }
    }

    fun finishChat(chatName: Int){
        getChats()
            .child("$chatName")
            .get().addOnSuccessListener {
                if(it.value != null){
                    getChats()
                        .child("$chatName")
                        .setValue(null)
                }
            }
    }

}