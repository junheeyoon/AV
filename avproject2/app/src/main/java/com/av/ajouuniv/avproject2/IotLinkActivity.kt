package com.av.ajouuniv.avproject2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView

import com.av.ajouuniv.avproject2.data.AccessPointListAdapter
import com.av.ajouuniv.avproject2.data.HueSharedPreferences
import com.philips.lighting.hue.sdk.PHAccessPoint
import com.philips.lighting.hue.sdk.PHBridgeSearchManager
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.hue.sdk.PHMessageType
import com.philips.lighting.hue.sdk.PHSDKListener
import com.philips.lighting.model.PHBridge
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHHueParsingError

class IotLinkActivity : Activity(), OnItemClickListener {

    private var phHueSDK: PHHueSDK? = null
    private var prefs: HueSharedPreferences? = null
    private var adapter: AccessPointListAdapter? = null

    private var lastSearchWasIPScan = false

    // Local SDK Listener
    private val listener = object : PHSDKListener {

        override fun onAccessPointsFound(accessPoint: List<PHAccessPoint>) {
            Log.w(TAG, "Access Points Found. " + accessPoint.size)

            ProgressDialog.instance.closeProgressDialog()
            if (accessPoint.isNotEmpty()) {
                phHueSDK!!.accessPointsFound.clear()
                phHueSDK!!.accessPointsFound.addAll(accessPoint)
                runOnUiThread { adapter!!.updateData(phHueSDK!!.accessPointsFound) }
            }

        }

        override fun onCacheUpdated(arg0: List<Int>, bridge: PHBridge) {
            Log.w(TAG, "On CacheUpdated")
        }

        override fun onBridgeConnected(b: PHBridge, username: String) {
            phHueSDK!!.selectedBridge = b
            phHueSDK!!.enableHeartbeat(b, PHHueSDK.HB_INTERVAL.toLong())
            phHueSDK!!.lastHeartbeat[b.resourceCache.bridgeConfiguration.ipAddress] = System.currentTimeMillis()
            prefs!!.lastConnectedIPAddress = b.resourceCache.bridgeConfiguration.ipAddress
            prefs!!.username = username
            ProgressDialog.instance.closeProgressDialog()
            startMainActivity()
        }

        override fun onAuthenticationRequired(accessPoint: PHAccessPoint) {
            Log.w(TAG, "Authentication Required.")
            phHueSDK!!.startPushlinkAuthentication(accessPoint)
            startActivity(Intent(this@IotLinkActivity, AuthenticationLinkActivity::class.java))
        }

        override fun onConnectionResumed(bridge: PHBridge) {
            if (this@IotLinkActivity.isFinishing)
                return

            Log.v(TAG, "onConnectionResumed" + bridge.resourceCache.bridgeConfiguration.ipAddress)
            phHueSDK!!.lastHeartbeat[bridge.resourceCache.bridgeConfiguration.ipAddress] = System.currentTimeMillis()
            for (i in 0 until phHueSDK!!.disconnectedAccessPoint.size) {

                if (phHueSDK!!.disconnectedAccessPoint[i].ipAddress == bridge.resourceCache.bridgeConfiguration.ipAddress) {
                    phHueSDK!!.disconnectedAccessPoint.removeAt(i)
                }
            }

        }

        override fun onConnectionLost(accessPoint: PHAccessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.ipAddress)
            if (!phHueSDK!!.disconnectedAccessPoint.contains(accessPoint)) {
                phHueSDK!!.disconnectedAccessPoint.add(accessPoint)
            }
        }

        override fun onError(code: Int, message: String) {
            Log.e(TAG, "on Error Called : $code:$message")

            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection")
            } else if (code == PHHueError.AUTHENTICATION_FAILED || code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                ProgressDialog.instance.closeProgressDialog()
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ")
                ProgressDialog.instance.closeProgressDialog()
                this@IotLinkActivity.runOnUiThread { ProgressDialog.showErrorDialog(this@IotLinkActivity, message, R.string.btn_ok) }

            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {
                    phHueSDK = PHHueSDK.getInstance()
                    val sm = phHueSDK!!.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
                    sm.search(false, false, true)
                    lastSearchWasIPScan = true
                } else {
                    ProgressDialog.instance.closeProgressDialog()
                    this@IotLinkActivity.runOnUiThread { ProgressDialog.showErrorDialog(this@IotLinkActivity, message, R.string.btn_ok) }
                }


            }
        }

        override fun onParsingErrors(parsingErrorsList: List<PHHueParsingError>) {
            for (parsingError in parsingErrorsList) {
                Log.e(TAG, "ParsingError : " + parsingError.message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bridgelistlinear)

        phHueSDK = PHHueSDK.create()

        phHueSDK!!.appName = "avproject2"
        phHueSDK!!.deviceName = android.os.Build.MODEL

        phHueSDK!!.notificationManager.registerSDKListener(listener)

        adapter = AccessPointListAdapter(applicationContext, phHueSDK!!.accessPointsFound)

        val accessPointList = findViewById<View>(R.id.bridge_list) as ListView
        accessPointList.onItemClickListener = this
        accessPointList.adapter = adapter

        prefs = HueSharedPreferences.getInstance(applicationContext)
        val lastIpAddress = prefs!!.lastConnectedIPAddress
        val lastUsername = prefs!!.username

        if (lastIpAddress != "") {
            val lastAccessPoint = PHAccessPoint()
            lastAccessPoint.ipAddress = lastIpAddress
            lastAccessPoint.username = lastUsername

            if (!phHueSDK!!.isAccessPointConnected(lastAccessPoint)) {
                ProgressDialog.instance.showProgressDialog(R.string.connecting, this@IotLinkActivity)
                phHueSDK!!.connect(lastAccessPoint)
            }
        } else {  // First time use, so perform a bridge search.
            doBridgeSearch()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.w(TAG, "Inflating home menu")
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.find_new_bridge -> doBridgeSearch()
        }
        return true
    }


    public override fun onDestroy() {
        super.onDestroy()
        phHueSDK!!.notificationManager.unregisterSDKListener(listener)
        phHueSDK!!.disableAllHeartbeat()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        val accessPoint = adapter!!.getItem(position) as PHAccessPoint

        val connectedBridge = phHueSDK!!.selectedBridge

        if (connectedBridge != null) {
            val connectedIP = connectedBridge.resourceCache.bridgeConfiguration.ipAddress
            if (connectedIP != null) {   // We are already connected here:-
                phHueSDK!!.disableHeartbeat(connectedBridge)
                phHueSDK!!.disconnect(connectedBridge)
            }
        }
        ProgressDialog.instance.showProgressDialog(R.string.connecting, this@IotLinkActivity)
        phHueSDK!!.connect(accessPoint)
    }

    fun doBridgeSearch() {
        ProgressDialog.instance.showProgressDialog(R.string.search_progress, this@IotLinkActivity)
        val sm = phHueSDK!!.getSDKService(PHHueSDK.SEARCH_BRIDGE) as PHBridgeSearchManager
        // Start the UPNP Searching of local bridges.
        sm.search(true, true)
    }

    fun startMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    companion object {
        val TAG = "IOT_HOME"
    }

}