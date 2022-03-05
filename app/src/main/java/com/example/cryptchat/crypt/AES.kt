package com.example.cryptchat.crypt

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.cryptchat.const.Consts
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@RequiresApi(Build.VERSION_CODES.O)
object AES {
    // шифрование
    @SuppressLint("GetInstance")
    fun encrypt(input:String, key: String): String {
        // Создать зашифрованный объект
        val cipher = Cipher.getInstance("AES")
        // Инициализация: шифрование / дешифрование
        val keySpec = SecretKeySpec(key.toByteArray(),"AES")
        cipher.init(Cipher.ENCRYPT_MODE,keySpec)
        // шифрование
        val encrypt = cipher.doFinal(input.toByteArray())
        return String(Base64.getEncoder().encode(encrypt))
    }
    // Расшифровать
    @SuppressLint("GetInstance")
    fun decrypt(input:String, key:String): String {
        return try {
            // Создать зашифрованный объект
            val cipher = Cipher.getInstance("AES")
            // Инициализация: шифрование / дешифрование
            val keySpec = SecretKeySpec(key.toByteArray(),"AES")
            cipher.init(Cipher.DECRYPT_MODE,keySpec)
            // Поскольку переданная строка зашифрована Base64, расшифровка Base64
            val encrypt = cipher
                .doFinal(Base64.getDecoder().decode(input))
            String(encrypt)
        } catch (e: Exception){
            Consts.ERROR_DECRYPT
        }

    }

    fun generateKey() = Date().time.toString() + "123"
}