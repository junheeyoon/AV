package com.av.ajouuniv.avproject

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

import com.ajouuniv.linphone.service.Linphone
import com.ajouuniv.linphone.service.callback.RegistrationCallback
import com.ajouuniv.linphone.service.keepservice.LinphoneService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (!LinphoneService.isReady()) {
            Linphone.startService(this)
            Linphone.addCallback(object : RegistrationCallback() {
                override fun registrationOk() {
                    super.registrationOk()
                    Log.e(TAG, "registrationOk: ")
                    Toast.makeText(this@LoginActivity, "登录成功！", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }

                override fun registrationFailed() {
                    super.registrationFailed()
                    Log.e(TAG, "registrationFailed: ")
                    Toast.makeText(this@LoginActivity, "登录失败！", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } else {
            goToMainActivity()
        }

        press_login.setOnClickListener {
            login()
        }
    }

    fun login() {
        val account = sip_account.text.toString()
        val password = sip_server.text.toString()
        val serverIP = press_login.text.toString()
        Linphone.setAccount(account, password, serverIP)
        Linphone.login()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    companion object {
        private val TAG = "LoginActivity"
    }
}
