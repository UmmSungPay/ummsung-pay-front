package com.project.ummsungpay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException
import java.lang.StringBuilder

class VisionActivity : AppCompatActivity() {

    private lateinit var mCameraView: SurfaceView
    private lateinit var mTextView: TextView
    private lateinit var mCameraSource: CameraSource
    private lateinit var previewSize: com.google.android.gms.common.images.Size

    companion object {
        private const val TAG = "VisionActivity"
        private const val requestPermissionID = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vision)
        mCameraView = findViewById(R.id.surfaceView)
        mTextView = findViewById(R.id.text_view)

        startCameraSource()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                previewSize = mCameraSource.previewSize
                mCameraSource.start(mCameraView.holder)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startCameraSource() {
        val textRecognizer = TextRecognizer.Builder(this).build()
        if (!textRecognizer.isOperational) {
            Log.w(TAG, "Detector dependencies are not yet available")
        } else {
            mCameraSource = CameraSource.Builder(this, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build()

            mCameraView.holder.addCallback(object: SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this@VisionActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this@VisionActivity, arrayOf(Manifest.permission.CAMERA), requestPermissionID)
                            return
                        }
                        mCameraSource.start(mCameraView.holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mCameraSource.stop()
                }
            })

            textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
                override fun release() {

                }

                override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                    val items = detections.detectedItems
                    if (items.size() != 0) {
                        mTextView.post {
                            val stringBuilder = StringBuilder()
                            for (i in 0 until items.size()) {
                                val item = items.valueAt(i)
                                stringBuilder.append(item.value)
                                stringBuilder.append("\n")
                            }
                            mTextView.text = stringBuilder.toString()

                            //16자리 숫자 추출
                            val regexCardnum = Regex("""\s\d{4}\s\d{4}\s\d{4}\s\d{4}""")
                            val matchCardnum: MatchResult? = regexCardnum.find(stringBuilder.toString())

                            //유효기간 추출
                            val regexValidity = Regex("""\s\d{2}/\d{2}""")
                            val matchValidity: MatchResult? = regexValidity.find(stringBuilder.toString())

                            //카드번호와 유효기간이 모두 추출되었다면
                            if (matchCardnum != null && matchValidity != null) {

                                val intentNext = Intent(this@VisionActivity, CardNameActivity::class.java)
                                intentNext.putExtra("recognized cardnum", matchCardnum!!.value)
                                intentNext.putExtra("recognized validity", matchValidity!!.value)
                                startActivity(intentNext)
                                finish()
                            }
                        }
                    }
                }
            })
        }
    }
}