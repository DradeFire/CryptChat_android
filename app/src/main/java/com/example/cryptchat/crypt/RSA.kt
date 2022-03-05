package com.example.cryptchat.crypt

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.cryptchat.const.Consts
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.io.IOException
import java.security.*
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class RSA {
    private var privateKey: PrivateKey
    private var publicKey: PublicKey

    fun getPublicKey(): String = String(Base64.getEncoder().encode(publicKey.encoded))

    @Throws(GeneralSecurityException::class, IOException::class)
    private fun loadPublicKey(stored: String): Key {
        val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
        val spec = X509EncodedKeySpec(data)
        val fact = KeyFactory.getInstance("RSA")
        return fact.generatePublic(spec)
    }

    @Throws(Exception::class)
    fun encryptMessage(plainText: String, pb_key: String = getPublicKey()): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(pb_key))
        return String(Base64.getEncoder().encode(cipher.doFinal(plainText.toByteArray())))
    }

    @Throws(Exception::class)
    fun decryptMessage(encryptedText: String?): String {
        return try {
            val privateKey = String(Base64.getEncoder().encode(privateKey.encoded))
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKey))
            String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
        } catch (e: Exception){
            Consts.ERROR_DECRYPT
        }
    }

    @Throws(GeneralSecurityException::class)
    private fun loadPrivateKey(key64: String): PrivateKey {
        val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
        val keySpec = PKCS8EncodedKeySpec(clear)
        val fact = KeyFactory.getInstance("RSA")
        val priv = fact.generatePrivate(keySpec)
        Arrays.fill(clear, 0.toByte())
        return priv
    }

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }

}