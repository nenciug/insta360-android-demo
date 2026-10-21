package com.insta360.sdk.demo

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
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
    private var lastX = 0f
    private var lastY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bindings
        videoPreview = findViewById(R.id.videoPreview)
        tvStatus = findViewById(R.id.tvStatus)
        tvAiMessage = findViewById(R.id.tvAiMessage)
        btnConnectWifi = findViewById(R.id.btnConnectWifi)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnRecordVideo = findViewById(R.id.btnRecordVideo)
        btnGallery = findViewById(R.id.btnGallery)

        // Touch listener for 360 degree pan/tilt image rotation
        videoPreview.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    lastY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - lastX
                    val deltaY = event.y - lastY
                    // Pan and rotate 360 Live Viewport via SDK
                    InstaCameraManager.getInstance().setPipelinePanFactor(deltaX * 0.01f)
                    InstaCameraManager.getInstance().setPipelineTiltFactor(deltaY * 0.01f)
                    lastX = event.x
                    lastY = event.y
                }
            }
            true
        }

        // Wi-Fi Connection
        btnConnectWifi.setOnClickListener {
            animateButton(btnConnectWifi)
            tvStatus.text = "Connecting..."
            tvAiMessage.text = "🤖 Searching for camera Wi-Fi network..."

            InstaCameraManager.getInstance().openCamera(InstaCameraManager.CONNECT_TYPE_WIFI, object : IConnectInstaCameraCallback {
                override fun onConnectSuccess() {
                    runOnUiThread {
                        tvStatus.text = "Connected (5.8G)"
                        tvAiMessage.text = "🤖 Camera connected! Swipe screen to rotate 360 view."
                        startLivePreview()
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
            animateButton(btnTakePhoto)
            InstaCameraManager.getInstance().takePicture(object : ICameraOperateCallback {
                override fun onSuccess() {
                    runOnUiThread {
                        tvAiMessage.text = "🤖 360 Photo saved! Tap Album to edit with AI."
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
            animateButton(btnRecordVideo)
            if (!isRecording) {
                InstaCameraManager.getInstance().startNormalRecord(object : ICameraOperateCallback {
                    override fun onSuccess() {
                        isRecording = true
                        runOnUiThread {
                            btnRecordVideo.text = "STOP"
                            tvStatus.text = "Recording 360..."
                            tvAiMessage.text = "🤖 Recording 360 video. Swipe to check camera angles!"
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
            animateButton(btnGallery)
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
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
