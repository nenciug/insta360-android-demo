package com.insta360.sdk.demo

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

        // UI Bindings
        videoPreview = findViewById(R.id.videoPreview)
        tvStatus = findViewById(R.id.tvStatus)
        tvAiMessage = findViewById(R.id.tvAiMessage)
        btnConnectWifi = findViewById(R.id.btnConnectWifi)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnRecordVideo = findViewById(R.id.btnRecordVideo)
        btnGallery = findViewById(R.id.btnGallery)

        // Wi-Fi Local Connection
        btnConnectWifi.setOnClickListener {
            animateButton(btnConnectWifi)
            tvStatus.text = "Connecting..."
            tvAiMessage.text = "🤖 Searching for camera Wi-Fi AP..."

            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI, object : IConnectInstaCameraCallback {
                override fun onConnectSuccess() {
                    runOnUiThread {
                        tvStatus.text = "Connected (5.8G)"
                        tvAiMessage.text = "🤖 Camera connected successfully! Live stream active."
                        startLivePreview()
                    }
                }

                override fun onConnectFail(errorCode: Int) {
                    runOnUiThread {
                        tvStatus.text = "Connection Failed"
                        tvAiMessage.text = "🤖 Wi-Fi connection error ($errorCode). Check settings."
                    }
                }

                override fun onCameraStateChanged(state: Int) {}
            })
        }

        // Take Photo
        btnTakePhoto.setOnClickListener {
            animateButton(btnTakePhoto)
            InstaCameraManager.getInstance().takePicture(object : ICameraOperateCallback {
                override fun onSuccess() {
                    runOnUiThread {
                        tvAiMessage.text = "🤖 Photo saved to camera SD card!"
                        Toast.makeText(this@MainActivity, "Photo Captured!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailed(errorCode: Int) {
                    runOnUiThread { tvAiMessage.text = "🤖 Failed to take photo. Code: $errorCode" }
                }
            })
        }

        // Record Video / Stop
        btnRecordVideo.setOnClickListener {
            animateButton(btnRecordVideo)
            if (!isRecording) {
                InstaCameraManager.getInstance().startNormalRecord(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        isRecording = true
                        runOnUiThread {
                            btnRecordVideo.text = "STOP"
                            tvStatus.text = "Recording..."
                            tvAiMessage.text = "🤖 Recording video in progress. Keep steady."
                        }
                    }

                    override fun onFailed(errorCode: Int) {
                        runOnUiThread { tvAiMessage.text = "🤖 Failed to start video recording." }
                    }
                })
            } else {
                InstaCameraManager.getInstance().stopNormalRecord(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        isRecording = false
                        runOnUiThread {
                            btnRecordVideo.text = "REC"
                            tvStatus.text = "Connected (5.8G)"
                            tvAiMessage.text = "🤖 Recording stopped. Video saved locally on SD."
                        }
                    }

                    override fun onFailed(errorCode: Int) {
                        runOnUiThread { tvAiMessage.text = "🤖 Failed to stop recording." }
                    }
                })
            }
        }

        // Gallery & Local Download Handler
        btnGallery.setOnClickListener {
            animateButton(btnGallery)
            tvAiMessage.text = "🤖 Opening SD Card Gallery & Local Download manager..."
            Toast.makeText(this, "Loading Camera Album...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLivePreview() {
        InstaCameraManager.getInstance().startPreviewStream()
    }

    private fun animateButton(button: Button) {
        button.animate().scaleX(0.90f).scaleY(0.90f).setDuration(70).withEndAction {
            button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(70)
        }
    }
}
