package com.example.cryptchat

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cryptchat.api.FirebaseApi
import com.example.cryptchat.crypt.AES
import com.example.cryptchat.crypt.RSA
import com.example.cryptchat.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = (supportFragmentManager.findFragmentById(R.id.frag_container)
                as NavHostFragment).navController
        setupActionBarWithNavController(navController)

        if(viewModel.aesKey == null){
            viewModel.aesKey = MutableLiveData()
            viewModel.aesKey?.value = AES.generateKey()
        }

        if(viewModel.rsaCrypt == null)
            viewModel.rsaCrypt = RSA()

        if(viewModel.dbAPI == null)
            viewModel.dbAPI = FirebaseApi(
                this,
                viewModel
            )

    }

}