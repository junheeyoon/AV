package com.av.ajouuniv.avproject2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.av.ajouuniv.avproject2.Server.LocalBinder
import android.widget.Toast
import android.os.IBinder
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection


class MainActivity : AppCompatActivity() {

    var mRecognizer: SpeechRecognizer? = null
    var mBounded: Boolean = false
    var mServer: Server? = null

    private var mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            Toast.makeText(this@MainActivity, "Service is disconnected", 1000).show()
            mBounded = false
            mServer = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Toast.makeText(this@MainActivity, "Service is connected", 1000).show()
            mBounded = true
            val mLocalBinder = service as LocalBinder
            mServer = mLocalBinder.serverInstance
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val button = findViewById<Button>(R.id.speech_to_text_btn)
        button.setOnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)            //음성인식 intent생성
            i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    //데이터 설정
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)//음성인식 언어 설정
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)                //음성인식 객체
            mRecognizer?.setRecognitionListener(listener)                                        //음성인식 리스너 등록
            mRecognizer?.startListening(i)
        }
    }

    override fun onStart() {
        super.onStart()
        val mIntent = Intent(this, Server::class.java)
        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (mBounded) {
            unbindService(mConnection)
            mBounded = false
        }
    };

    private val listener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult.size)
            mResult.toArray(rs)
            updateStatus(rs[0])
        }
        override fun onReadyForSpeech(params: Bundle) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onBeginningOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
        override fun onBufferReceived(buffer: ByteArray) {}
    }

    fun updateStatus(status: String?) {
        this.runOnUiThread {
            val labelView = findViewById<TextView>(R.id.speech_to_text)
            labelView.text = status
        }
    }

}
