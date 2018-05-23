package com.av.ajouuniv.avproject

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.Toast

import com.ajouuniv.linphone.service.Linphone
import com.ajouuniv.linphone.service.callback.RegistrationCallback
import com.ajouuniv.linphone.service.keepservice.LinphoneService

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class LoginActivity : AppCompatActivity() {
    @BindView(R.id.sip_account)
    private var mAccount: EditText? = null
    @BindView(R.id.sip_password)
    private var mPassword: EditText? = null
    @BindView(R.id.sip_server)
    private var mServer: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        if (!LinphoneService.isReady()) {
            Linphone.startService(this)
            Linphone.addCallback(object : RegistrationCallback() {
                override fun registrationOk() {
                    super.registrationOk()
                    Log.e(TAG, "registrationOk: ")
                    Toast.makeText(this@LoginActivity, "로그인성공", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }

                override fun registrationFailed() {
                    super.registrationFailed()
                    Log.e(TAG, "registrationFailed: ")
                    Toast.makeText(this@LoginActivity, "로그인실패！", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } else {
            goToMainActivity()
        }
    }

    @OnClick(R.id.press_login)
    fun login() {
        val account = mAccount!!.text.toString()
        val password = mPassword!!.text.toString()
        val serverIP = mServer!!.text.toString()
        Linphone.setAccount(account, password, serverIP)
        Linphone.login()
    }

    private fun goToMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
