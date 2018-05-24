package com.av.ajouuniv.avproject2

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

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    var mRecognizer: SpeechRecognizer? = null
    var apiService: ApiInterface? = null
    var textToSpeech : TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        apiService = ApiClient.getClient().create(ApiInterface::class.java)
        textToSpeech = TextToSpeech(this,textToSpeechListener)

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
    private val speechToTextListener = object : RecognitionListener {
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onResults(results: Bundle) {
            var key = ""
            key = SpeechRecognizer.RESULTS_RECOGNITION
            val mResult = results.getStringArrayList(key)
            val rs = arrayOfNulls<String>(mResult.size)
            mResult.toArray(rs)
            updateStatus(rs[0])

            val call = apiService!!.postUser()
            call.enqueue(object : Callback<NetworkExample> {
                override fun onResponse(call: Call<NetworkExample>, response: Response<NetworkExample>) {
                    textToSpeech!!.speak(response.body().message,TextToSpeech.QUEUE_FLUSH, null)
                }
                override fun onFailure(call: Call<NetworkExample>, t: Throwable) {
                    Log.d(TAG, "what: ")
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

}
