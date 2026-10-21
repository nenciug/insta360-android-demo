package com.insta360.kmpsdk.demo

import android.content.Intent
import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arashivision.sdkcamera.camera.InstaCameraManager
import com.arashivision.sdkcamera.camera.callback.ICameraOperateCallback
import com.arashivision.sdkcamera.camera.callback.IConnectInstaCameraCallback

class MainActivity : AppCompatActivity() {

    private lateinit var videoPreview: TextureView
    private lateinit var tvStatus: TextView
    private lateinit var tvAiMessage: TextView
    private lateinit var btnConnectWifi: Button
    private lateinit var btnTakePhoto: Button
    private lateinit var btnRecordVideo: Button
    private lateinit var btnGallery: Button

    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoPreview = findViewById(R.id.videoPreview)
        tvStatus = findViewById(R.id.tvStatus)
        tvAiMessage = findViewById(R.id.tvAiMessage)
        btnConnectWifi = findViewById(R.id.btnConnectWifi)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnRecordVideo = findViewById(R.id.btnRecordVideo)
        btnGallery = findViewById(R.id.btnGallery)

        btnConnectWifi.setOnClickListener {
            tvStatus.text = "Connecting..."
            tvAiMessage.text = "🤖 Connecting to camera..."
            try {
                InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI, object : IConnectInstaCameraCallback {
                    override fun onConnectSuccess() {
                        runOnUiThread {
                            tvStatus.text = "Connected"
                            tvAiMessage.text = "🤖 Camera connected successfully!"
                        }
                    }

                    override fun onConnectFail(errorCode: Int) {
                        runOnUiThread {
                            tvStatus.text = "Failed"
                            tvAiMessage.text = "🤖 Connection failed ($errorCode)."
                        }
                    }

                    override fun onCameraStateChanged(state: Int) {}
                })
            } catch (e: Exception) {
                tvStatus.text = "Error"
                tvAiMessage.text = "🤖 Exception during connection."
            }
        }

        btnTakePhoto.setOnClickListener {
            try {
                InstaCameraManager.getInstance().takePicture(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        runOnUiThread {
                            tvAiMessage.text = "🤖 Photo saved!"
                            Toast.makeText(this@MainActivity, "Photo Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailed(errorCode: Int) {
                        runOnUiThread { tvAiMessage.text = "🤖 Photo failed." }
                    }
                })
            } catch (e: Exception) {
                tvAiMessage.text = "🤖 Error capturing photo."
            }
        }

        btnRecordVideo.setOnClickListener {
            try {
                if (!isRecording) {
                    InstaCameraManager.getInstance().startNormalRecord(object : ICameraOperateCallback {
                        override fun onSuccess() {
                            isRecording = true
                            runOnUiThread {
                                btnRecordVideo.text = "STOP"
                                tvStatus.text = "Recording"
                                tvAiMessage.text = "🤖 Recording video..."
                            }
                        }

                        override fun onFailed(errorCode: Int) {
                            runOnUiThread { tvAiMessage.text = "🤖 Start record failed." }
                        }
                    })
                } else {
                    InstaCameraManager.getInstance().stopNormalRecord(object : ICameraOperateCallback {
                        override fun onSuccess() {
                            isRecording = false
                            runOnUiThread {
                                btnRecordVideo.text = "REC"
                                tvStatus.text = "Connected"
                                tvAiMessage.text = "🤖 Video saved."
                            }
                        }

                        override fun onFailed(errorCode: Int) {
                            runOnUiThread { tvAiMessage.text = "🤖 Stop record failed." }
                        }
                    })
                }
            } catch (e: Exception) {
                tvAiMessage.text = "🤖 Error recording video."
            }
        }

        btnGallery.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
    }
}
