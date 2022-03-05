package com.example.cryptchat

import com.example.cryptchat.crypt.AES
import com.example.cryptchat.crypt.RSA
import org.junit.jupiter.api.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private companion object{
        const val TEST_STRING = "Добро пожаловать"
    }

    @Test
    fun testAES_Crypt() {
        // пароль AES должен быть 16 бит
        val key = AES.generateKey()

        val e = AES.encrypt(TEST_STRING, key)
        val d = AES.decrypt(e, key)

        assert(d == TEST_STRING)
    }

    @Test
    fun testRSA_Crypt() {
        val rsa = RSA()

        val e = rsa.encryptMessage(TEST_STRING)
        val d = rsa.decryptMessage(e)

        assert(d == TEST_STRING)
    }
}