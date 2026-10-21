package com.insta360.sdk.demo

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

        // Wi-Fi Connection
        btnConnectWifi.setOnClickListener {
            tvStatus.text = "Connecting..."
            tvAiMessage.text = "🤖 Searching for camera Wi-Fi network..."

            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI, object : IConnectInstaCameraCallback {
                override fun onConnectSuccess() {
                    runOnUiThread {
                        tvStatus.text = "Connected (5.8G)"
                        tvAiMessage.text = "🤖 Camera connected successfully! Live stream active."
                        InstaCameraManager.getInstance().startPreviewStream()
                    }
                }

                override fun onConnectFail(errorCode: Int) {
                    runOnUiThread {
                        tvStatus.text = "Connection Failed"
                        tvAiMessage.text = "🤖 Wi-Fi connection error ($errorCode). Please retry."
                    }
                }

                override fun onCameraStateChanged(state: Int) {}
            })
        }

        // Take Photo
        btnTakePhoto.setOnClickListener {
            InstaCameraManager.getInstance().takePicture(object : ICameraOperateCallback {
                override fun onSuccess() {
                    runOnUiThread {
                        tvAiMessage.text = "🤖 360 Photo saved! Tap AI Album to edit."
                        Toast.makeText(this@MainActivity, "Photo Saved!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailed(errorCode: Int) {
                    runOnUiThread { tvAiMessage.text = "🤖 Failed to take photo ($errorCode)" }
                }
            })
        }

        // Record Toggle
        btnRecordVideo.setOnClickListener {
            if (!isRecording) {
                InstaCameraManager.getInstance().startNormalRecord(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        isRecording = true
                        runOnUiThread {
                            btnRecordVideo.text = "STOP"
                            tvStatus.text = "Recording 360..."
                            tvAiMessage.text = "🤖 Recording 360 video in progress."
                        }
                    }

                    override fun onFailed(errorCode: Int) {
                        runOnUiThread { tvAiMessage.text = "🤖 Failed to start video." }
                    }
                })
            } else {
                InstaCameraManager.getInstance().stopNormalRecord(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        isRecording = false
                        runOnUiThread {
                            btnRecordVideo.text = "REC"
                            tvStatus.text = "Connected (5.8G)"
                            tvAiMessage.text = "🤖 Video saved to camera SD card."
                        }
                    }

                    override fun onFailed(errorCode: Int) {
                        runOnUiThread { tvAiMessage.text = "🤖 Failed to stop video." }
                    }
                })
            }
        }

        // Open AI Editor & Local Album
        btnGallery.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
    }
}
