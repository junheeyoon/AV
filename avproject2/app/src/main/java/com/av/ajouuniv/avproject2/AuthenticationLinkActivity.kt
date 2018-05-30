package com.av.ajouuniv.avproject2

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar

import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHMessageType
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHHueParsingError

class AuthenticationLinkActivity : Activity() {
    private var pbar: ProgressBar? = null
    private var phHueSDK: PHHueSDK? = null
    private var isDialogShowing: Boolean = false

    private val listener = object : PHSDKListener {

        override fun onAccessPointsFound(arg0: List<PHAccessPoint>) {}

        override fun onAuthenticationRequired(arg0: PHAccessPoint) {}

        override fun onBridgeConnected(bridge: PHBridge, username: String) {}

        override fun onCacheUpdated(arg0: List<Int>, bridge: PHBridge) {}

        override fun onConnectionLost(arg0: PHAccessPoint) {}

        override fun onConnectionResumed(arg0: PHBridge) {}

        override fun onError(code: Int, message: String) {
            if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                incrementProgress()
            } else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                incrementProgress()

                if (!isDialogShowing) {
                    isDialogShowing = true
                    this@AuthenticationLinkActivity.runOnUiThread {
                        val builder = AlertDialog.Builder(this@AuthenticationLinkActivity)
                        builder.setMessage(message).setNeutralButton(R.string.btn_ok
                        ) { _, _ -> finish() }

                        builder.create()
                        builder.show()
                    }
                }

            }

        } // End of On Error

        override fun onParsingErrors(parsingErrorsList: List<PHHueParsingError>) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pushlink)
        setTitle(R.string.txt_pushlink)
        isDialogShowing = false
        phHueSDK = PHHueSDK.getInstance()

        pbar = findViewById<View>(R.id.countdownPB) as ProgressBar?
        pbar!!.max = MAX_TIME

        phHueSDK!!.notificationManager.registerSDKListener(listener)
    }

    override fun onStop() {
        super.onStop()
        phHueSDK!!.notificationManager.unregisterSDKListener(listener)
    }

    fun incrementProgress() {
        pbar!!.incrementProgressBy(1)
    }

    public override fun onDestroy() {
        super.onDestroy()
        phHueSDK!!.notificationManager.unregisterSDKListener(listener)
    }

    companion object {
        private val MAX_TIME = 30
    }

}
