package com.av.ajouuniv.avproject2

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.av.ajouuniv.avproject2.api.ApiClient
import com.av.ajouuniv.avproject2.api.ApiInterface
import com.av.ajouuniv.avproject2.data.NetworkExample
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.speech.tts.TextToSpeech
import java.util.*
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import android.widget.Toast
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.av.ajouuniv.avproject2.data.NetworkExample1
import com.av.ajouuniv.avproject2.data.NetworkExample2
import com.philips.lighting.hue.listener.PHLightListener
import com.philips.lighting.hue.sdk.PHHueSDK
import com.philips.lighting.model.PHBridgeResource
import com.philips.lighting.model.PHHueError
import com.philips.lighting.model.PHLight
import com.philips.lighting.model.PHLightState
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var mRecognizer: SpeechRecognizer? = null
    var apiService: ApiInterface? = null
    var textToSpeech : TextToSpeech? = null
    private var bt: BluetoothSPP? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phHueSDK = PHHueSDK.create()

        bt = BluetoothSPP(this)
        if (!bt!!.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(applicationContext, "Bluetooth is not available", Toast.LENGTH_SHORT).show()
        }

        bt!!.setOnDataReceivedListener(BluetoothSPP.OnDataReceivedListener { _, _ ->
            //데이터 수신
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)            //음성인식 intent생성
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    //데이터 설정
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)//음성인식 언어 설정
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)                //음성인식 객체
            mRecognizer?.setRecognitionListener(speechToTextListener)                                        //음성인식 리스너 등록
            mRecognizer?.startListening(i)
        })

        bt!!.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener { //연결됐을 때
            override fun onDeviceConnected(name: String, address: String) {
                Toast.makeText(applicationContext, "Connected to $name\n$address", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceDisconnected() { //연결해제
                Toast.makeText(applicationContext, "Connection lost", Toast.LENGTH_SHORT).show()
            }

            override fun onDeviceConnectionFailed() { //연결실패
                Toast.makeText(applicationContext, "Unable to connect", Toast.LENGTH_SHORT).show()
            }
        })

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        apiService = ApiClient.client?.create(ApiInterface::class.java)
        textToSpeech = TextToSpeech(this,textToSpeechListener)

        // 블루투스로 바꿀예정..
        
        val speechToTextBtn = findViewById<Button>(R.id.speech_to_text_btn)
        speechToTextBtn.setOnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)            //음성인식 intent생성
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    //데이터 설정
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)//음성인식 언어 설정
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)                //음성인식 객체
            mRecognizer?.setRecognitionListener(speechToTextListener)                                        //음성인식 리스너 등록
            mRecognizer?.startListening(i)
        }

        val turnOnBtn = findViewById<Button>(R.id.turn_on_btn)
        turnOnBtn.setOnClickListener {
            val call = apiService!!.updateDevice("관리자 전등 켜 줘")
            call.enqueue(object : Callback<NetworkExample> {
                override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                    textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                    // IOT
                    trunOnLights2()
                }
                override fun onFailure(call: Call<NetworkExample>, t: Throwable) {

                }
            })
        }

        val turnOffBtn = findViewById<Button>(R.id.turn_off_btn)
        turnOffBtn.setOnClickListener {
            val call = apiService!!.updateDevice("관리자 전등 꺼 줘")
            call.enqueue(object : Callback<NetworkExample> {
                override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                    textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                    // IOT
                    trunOffLights2()
                }
                override fun onFailure(call: Call<NetworkExample>, t: Throwable) {

                }
            })
        }

        val repeatBtn = findViewById<Button>(R.id.repeat_btn)
        repeatBtn.setOnClickListener {
            val call = apiService!!.updateDevice("관리자 켜")
            call.enqueue(object : Callback<NetworkExample> {
                override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                    textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                }
                override fun onFailure(call: Call<NetworkExample>, t: Throwable) {
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        if (!bt!!.isBluetoothEnabled()) {
            val intent : Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
        } else {
            if (!bt!!.isServiceAvailable()) {
                bt!!.setupService()
                bt!!.startService(BluetoothState.DEVICE_ANDROID)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt!!.connect(data)
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt!!.setupService()
                bt!!.startService(BluetoothState.DEVICE_OTHER)
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    fun randomLights() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights
        val rand = Random()

        for (light in allLights) {
            val lightState = PHLightState()
            lightState.effectMode = PHLight.PHLightEffectMode.EFFECT_COLORLOOP
            //lightState.hue = rand.nextInt(MAX_HUE)
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener)
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    fun trunOffLights( i : Int) {
        val bridge = phHueSDK!!.selectedBridge
        val allLights = bridge.resourceCache.allLights

            val lightState = PHLightState()
            lightState.isOn = false
            bridge.updateLightState(allLights[i], lightState, listener)

    }

    fun trunOnLights( i : Int) {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights
            val lightState = PHLightState()
            lightState.isOn = true
            bridge.updateLightState(allLights[i], lightState, listener)

    }

    fun trunOffLights0() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights
        val lightState = PHLightState()
        lightState.isOn = false
        bridge.updateLightState(allLights[0], lightState, listener)
    }
    fun trunOffLights1() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights
        val lightState = PHLightState()
        lightState.isOn = false
        bridge.updateLightState(allLights[1], lightState, listener)
    }
    fun trunOffLights2() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights

        val lightState = PHLightState()
        lightState.isOn = false
        bridge.updateLightState(allLights[2], lightState, listener)
    }
    fun trunOnLights0() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights



        //1 0 2 0이 가운데 1이 왼쪽 2가 오른쪽
        val lightState = PHLightState()
        lightState.isOn = true
        bridge.updateLightState(allLights[0], lightState, listener)


    }

    fun trunOnLights1() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights



        //1 0 2 0이 가운데 1이 왼쪽 2가 오른쪽
        val lightState = PHLightState()
        lightState.isOn = true
        bridge.updateLightState(allLights[1], lightState, listener)


    }


    fun trunOnLights2() {
        val bridge = phHueSDK!!.selectedBridge

        val allLights = bridge.resourceCache.allLights



             //1 0 2 0이 가운데 1이 왼쪽 2가 오른쪽
            val lightState = PHLightState()
            lightState.isOn = true
            bridge.updateLightState(allLights[2], lightState, listener)


    }

    @Suppress("DEPRECATION")
    private val postSpeechToTextListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult.size)
            mResult.toArray(rs)
            updateStatus(rs[0])
            val call = apiService!!.postDevice(rs[0].toString())
            call.enqueue(object : Callback<NetworkExample2> {
                override fun onResponse(call: Call<NetworkExample2>, response: Response<NetworkExample2>) {
                    updateServerStatus(response.body().isOk.toString())
                    textToSpeech!!.speak(response.body().message, TextToSpeech.QUEUE_FLUSH, null)
                }
                override fun onFailure(call: Call<NetworkExample2>, t: Throwable) {
                    updateServerStatus(t.toString())
                }
            })
        }
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onBeginningOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
        override fun onBufferReceived(buffer: ByteArray) {}
    }

    @Suppress("DEPRECATION")
    private val speechToTextListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult.size)
            mResult.toArray(rs)
            updateStatus(rs[0])

//            if(rs[0].toString().startsWith("화상")){
//                bt!!.send("Text", true)
//            } else {
//                if(rs[0].toString().contains("자")) {
//                    if (rs[0].toString().contains("켜")) {
//                        if(rs[0].toString().contains("거실")){
//                            textToSpeech!!.speak("관리자님의 요청으로 거실불이 켜졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOnLights0()
//                        }
//                        if(rs[0].toString().contains("방")){
//                            textToSpeech!!.speak("관리자님의 요청으로 방불이 켜졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOnLights1()
//                        }
//                        if(rs[0].toString().contains("화장")){
//                            textToSpeech!!.speak("관리자님의 요청으로 화장실 불이 켜졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOnLights2()
//                        }
//
//                    } else if (rs[0].toString().contains("꺼")) {
//                        if(rs[0].toString().contains("거실")){
//                            textToSpeech!!.speak("관리자님의 요청으로 거실불이 꺼졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOffLights0()
//                        }
//                        if(rs[0].toString().contains("방")){
//                            textToSpeech!!.speak("관리자님의 요청으로 방불이 꺼졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOffLights1()
//                        }
//                        if(rs[0].toString().contains("화장")){
//                            textToSpeech!!.speak("관리자님의 요청으로 화장실 불이 꺼졌습니다!!",TextToSpeech.QUEUE_FLUSH, null)
//                            trunOffLights2()
//                        }
//                    } else {
//                        textToSpeech!!.speak("다시 한 번 명령해 주세요",TextToSpeech.QUEUE_FLUSH, null)
//                    }
//                } else {
//                    textToSpeech!!.speak("관리자가 아닙니다? 누구냐? 너?",TextToSpeech.QUEUE_FLUSH, null)
//                }
            if(rs[0].toString().contains("등록")) {

                textToSpeech!!.speak("시리얼 넘버를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null)
                TimeUnit.SECONDS.sleep(4);
                textToSpeech!!.speak("디바이스 이름을 말해주세요.", TextToSpeech.QUEUE_FLUSH, null)
                speechToText()
            }
            else if(rs[0].toString().contains("삭제")) {
                val call = apiService!!.deleteDevice(rs[0].toString())
                call.enqueue(object : Callback<NetworkExample1> {
                    override fun onResponse(call: Call<NetworkExample1>, response: Response<NetworkExample1>) {
                        updateServerStatus(response.body().isOk.toString())
                        textToSpeech!!.speak(response.body().message, TextToSpeech.QUEUE_FLUSH, null)
                    }
                    override fun onFailure(call: Call<NetworkExample1>, t: Throwable) {
                        updateServerStatus(t.toString())
                    }
                })
            }
            else{

                val call = apiService!!.updateDevice(rs[0].toString())
                call.enqueue(object : Callback<NetworkExample> {
                    override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                        updateServerStatus(response.body().isOk.toString())
                        textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                        // IOT
                        var d_id = response.body().device_id;
                        if(response.body().isOk){

                            trunOnLights(d_id)
                            //randomLights()
                        } else {
                            trunOffLights(d_id)
                        }
                    }
                    override fun onFailure(call: Call<NetworkExample>, t: Throwable) {
                        updateServerStatus(t.toString())
                    }
                })
            }
        }
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onBeginningOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
        override fun onBufferReceived(buffer: ByteArray) {}
    }
    private val textToSpeechListener = TextToSpeech.OnInitListener { textToSpeech!!.language = Locale.KOREAN }

    fun updateStatus(status: String?) {
        this.runOnUiThread {
            val labelView = findViewById<TextView>(R.id.speech_to_text)
            labelView.text = status
        }
    }
    fun updateServerStatus(status: String?) {
        this.runOnUiThread {
            val labelView = findViewById<TextView>(R.id.server_text)
            labelView.text = status
        }
    }

    fun speechToText(){
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)            //음성인식 intent생성
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    //데이터 설정
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)//음성인식 언어 설정
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)                //음성인식 객체
        mRecognizer?.setRecognitionListener(postSpeechToTextListener)                                        //음성인식 리스너 등록
        mRecognizer?.startListening(i)
    }



    companion object {
        private val MAX_HUE = 65535
        val TAG = "MainActivity"
    }
}
