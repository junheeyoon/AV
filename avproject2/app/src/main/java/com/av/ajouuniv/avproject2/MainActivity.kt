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


class MainActivity : AppCompatActivity() {

    var mRecognizer: SpeechRecognizer? = null
    var apiService: ApiInterface? = null
    var textToSpeech : TextToSpeech? = null
    private var bt: BluetoothSPP? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt = BluetoothSPP(this)
        if (!bt!!.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(applicationContext, "Bluetooth is not available", Toast.LENGTH_SHORT).show()
        }

        bt!!.setOnDataReceivedListener(BluetoothSPP.OnDataReceivedListener { data, message ->
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

        apiService = ApiClient.getClient().create(ApiInterface::class.java)
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
    }

    override fun onStart() {
        super.onStart()
        if (!bt!!.isBluetoothEnabled()) { //
            val intent : Intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
        } else {
            if (!bt!!.isServiceAvailable()) {
                bt!!.setupService();
                bt!!.startService(BluetoothState.DEVICE_ANDROID)
            }
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

    private val speechToTextListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult.size)
            mResult.toArray(rs)
            updateStatus(rs[0])

            val call = apiService!!.updateDevice(rs[0])
            call.enqueue(object : Callback<NetworkExample> {
                override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                    updateServerStatus(response.body().isOk)
                    textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                    //블루투스 데이터 송신
                    bt!!.send("Text", true)
                }
                override fun onFailure(call: Call<NetworkExample>, t: Throwable) {
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

    companion object {
        val TAG = "MainActivity"
    }
}
