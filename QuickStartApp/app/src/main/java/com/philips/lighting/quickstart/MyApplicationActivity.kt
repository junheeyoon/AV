package com.philips.lighting.quickstart

import java.util.Random

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button

import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHBridgeResource
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 *
 * @author soulmade00
 */
class MyApplicationActivity : Activity() {
    private var phHueSDK: PHHueSDK? = null
    // If you want to handle the response from the bridge, create a PHLightListener object.
    internal var listener: PHLightListener = object : PHLightListener {

        override fun onSuccess() {}

        override fun onStateUpdate(arg0: Map<String, String>, arg1: List<PHHueError>) {
            Log.w(TAG, "Light has updated")
        }

        override fun onError(arg0: Int, arg1: String) {}

        override fun onReceivingLightDetails(arg0: PHLight) {}

        override fun onReceivingLights(arg0: List<PHBridgeResource>) {}

        override fun onSearchComplete() {}
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.app_name)
        setContentView(R.layout.activity_main)
        phHueSDK = PHHueSDK.create()
        val randomButton: Button
        randomButton = findViewById(R.id.buttonRand) as Button
        randomButton.setOnClickListener { randomLights() }

    }

    fun randomLights() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights
        val rand = Random()

        for (light in allLights) {
            val lightState = PHLightState()
            lightState.hue = rand.nextInt(MAX_HUE)
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener)
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    override fun onDestroy() {
        val bridge = phHueSDK!!.selectedBridge
        if (bridge != null) {

            if (phHueSDK!!.isHeartbeatEnabled(bridge)) {
                phHueSDK!!.disableHeartbeat(bridge)
            }

            phHueSDK!!.disconnect(bridge)
            super.onDestroy()
        }
    }

    companion object {
        private val MAX_HUE = 65535
        val TAG = "QuickStart"
    }
}
