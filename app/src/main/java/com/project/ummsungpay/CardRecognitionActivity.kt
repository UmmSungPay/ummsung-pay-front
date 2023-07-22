package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.odml.image.MediaMlImageBuilder
import com.google.android.odml.image.MlImage
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_card_recognition.buttonNext
import kotlinx.android.synthetic.main.activity_card_recognition.textView
import java.util.Locale

class CardRecognitionActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    private var previewView: PreviewView? = null
    private var textView: TextView? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var cameraSelector: CameraSelector? = null
    private var textRecognizer: TextRecognizer? = null
    private var analysisUseCase: ImageAnalysis? = null

    private var executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_recognition)

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), REQUEST_CODE)
        }

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                }
                else {

                }
            } else {

            }
        }

        //재시도 여부 확인
        val isRetry = intent.getIntExtra("isFail", 0)

        if (isRetry == 1) {
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("카드정보를 인식하지 못했습니다. 다시 시도해주세요.")
            }, 1000)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("카메라가 실행되었습니다.")
            }, 1000)
        }

        textView = findViewById(R.id.textView)
        previewView = findViewById(R.id.preview)


        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture?.addListener({
            cameraProvider = cameraProviderFuture!!.get()
            bindPreviewCase()
            bindAnalyzeUserCase() //오류 뜨지만 실행에 문제X
        }, ContextCompat.getMainExecutor(this))

        /*
        buttonNext.setOnClickListener{
            intentNext.putExtra("recognized text", textView?.text)
            startActivity(intentNext)
            finish()
        }
        */
    }

    private fun bindPreviewCase() {
        preview = Preview.Builder().build()

        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        if (preview != null) {
            preview!!.setSurfaceProvider(previewView?.surfaceProvider)

            cameraProvider!!.bindToLifecycle(this as LifecycleOwner, cameraSelector!!, preview)
        }
    }

    @ExperimentalGetImage
    private fun bindAnalyzeUserCase() {
        textRecognizer?.close()
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

        analysisUseCase = ImageAnalysis.Builder().build()
        analysisUseCase!!.setAnalyzer(
            ContextCompat.getMainExecutor(this)
        ) {imageProxy: ImageProxy ->
            try {
                processImageProxy(imageProxy)
            } catch (e: MlKitException) {
            }
        }

        cameraProvider!!.bindToLifecycle(this, cameraSelector!!, analysisUseCase)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @ExperimentalGetImage
    fun processImageProxy(image: ImageProxy) {
        val mlImage = MediaMlImageBuilder(image.image!!).setRotation(image.imageInfo.rotationDegrees).build()
        requestDetectInImage(mlImage)
            .addOnCompleteListener{
                image.close()

                //16자리 숫자 추출
                val regexCardnum = Regex("""\s\d{4}\s\d{4}\s\d{4}\s\d{4}""")
                val matchCardnum: MatchResult? = regexCardnum.find(textView!!.text)

                //유효기간 추출
                val regexValidity = Regex("""\s\d{2}/\d{2}""")
                val matchValidity: MatchResult? = regexValidity.find(textView!!.text)

                //카드번호와 유효기간이 모두 추출되었다면
                if (matchCardnum != null && matchValidity != null) {
                    val intentNext = Intent(this, CardNameActivity::class.java)
                    intentNext.putExtra("recognized cardnum", matchCardnum!!.value)
                    intentNext.putExtra("recognized validity", matchValidity!!.value)
                    startActivity(intentNext)
                    finish()
                }
            }
    }

    private fun requestDetectInImage(image: MlImage): Task<Text> {
        return setUpListener(textRecognizer!!.process(image))
    }

    private fun setUpListener(task: Task<Text>): Task<Text> {
        return task.addOnSuccessListener(executor) { results: Text ->
            val textResult: Text = results
            textView?.text = textResult.text
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}