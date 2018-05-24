package com.av.ajouuniv.avproject2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var mRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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