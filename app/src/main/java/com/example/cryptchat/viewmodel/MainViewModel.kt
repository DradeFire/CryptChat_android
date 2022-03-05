package com.example.cryptchat.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cryptchat.api.FirebaseApi
import com.example.cryptchat.crypt.RSA

class MainViewModel: ViewModel() {
    var dbAPI: FirebaseApi? = null
    var rsaCrypt: RSA? = null
    var aesKey: MutableLiveData<String>? = null

    var nameOfChatPerson: String? = null
    var currentPerson: String? = null

}