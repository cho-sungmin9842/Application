package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener  {
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= 23)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), 0)
        }
        tts = TextToSpeech(this, this)
        binding.button.setOnClickListener {
            val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                setRecognitionListener(recognitionListener())
                startListening(speechRecognizerIntent)
            }
        }
        binding.button2.setOnClickListener {
            if(binding.editText.text.isEmpty())
            {
                Snackbar.make(it,"아무것도 입력되지 않았습니다.",Snackbar.LENGTH_SHORT).show()
            }
            else
            {
                tts?.speak(binding.editText.text.toString(), TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }
    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) = Snackbar.make(binding.root, "음성인식 시작", Snackbar.LENGTH_SHORT).show()
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            var message:String
            when (error) {
                SpeechRecognizer.ERROR_AUDIO->message = "오디오 에러";
                SpeechRecognizer.ERROR_CLIENT->message = "클라이언트 에러";
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS->message = "퍼미션 없음";
                SpeechRecognizer.ERROR_NETWORK->message = "네트워크 에러";
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT->message = "네트웍 타임아웃";
                SpeechRecognizer.ERROR_NO_MATCH->message = "찾을 수 없음";
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY->message = "RECOGNIZER가 바쁨";
                SpeechRecognizer.ERROR_SERVER->message = "서버가 이상함";
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT->message = "말하는 시간초과";
                else-> message = "알 수 없는 오류임";
            }
            Snackbar.make(binding.root, "에러가 발생하였습니다. : $message",Snackbar.LENGTH_SHORT).show();
        }
        override fun onResults(results: Bundle) {
            var matches=results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(i in 0 until matches?.size!!)
            {
                binding.textView.text=matches?.get(i)
            }
        }
    }
    override fun onDestroy() {
        if (tts != null)
        {
            tts?.stop()
            tts?.shutdown()
        }
        if (speechRecognizer != null)
        {
            speechRecognizer?.stopListening()
        }
        super.onDestroy()
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS)
        {
            val result = tts?.setLanguage(Locale.KOREA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Snackbar.make(binding.root,"입력한 언어는 지원하지 않거나 누락되었습니다!",Snackbar.LENGTH_SHORT).show()
            }
            else
            {
                Snackbar.make(binding.root,"${binding.editText.text}",Snackbar.LENGTH_SHORT).show()
            }
        }
        else
        {
            Snackbar.make(binding.root,"초기화 실패!",Snackbar.LENGTH_SHORT).show()
        }
    }
    //추가할 코드 작성
}