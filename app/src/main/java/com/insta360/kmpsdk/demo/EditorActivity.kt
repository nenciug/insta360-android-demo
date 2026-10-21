package com.insta360.kmpsdk.demo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditorActivity : AppCompatActivity() {

    private lateinit var ivEditorImage: ImageView
    private lateinit var tvAiEditorStatus: TextView
    private lateinit var btnAiTinyPlanet: Button
    private lateinit var btnAiFisheye: Button
    private lateinit var btnAiEnhance: Button
    private lateinit var btnAiReframe: Button
    private lateinit var btnExport: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        ivEditorImage = findViewById(R.id.ivEditorImage)
        tvAiEditorStatus = findViewById(R.id.tvAiEditorStatus)
        btnAiTinyPlanet = findViewById(R.id.btnAiTinyPlanet)
        btnAiFisheye = findViewById(R.id.btnAiFisheye)
        btnAiEnhance = findViewById(R.id.btnAiEnhance)
        btnAiReframe = findViewById(R.id.btnAiReframe)
        btnExport = findViewById(R.id.btnExport)

        btnAiTinyPlanet.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Tiny Planet Projection."
            Toast.makeText(this, "Tiny Planet Applied", Toast.LENGTH_SHORT).show()
        }

        btnAiFisheye.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Fisheye View."
            Toast.makeText(this, "Fisheye Applied", Toast.LENGTH_SHORT).show()
        }

        btnAiEnhance.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Auto-Enhance."
            Toast.makeText(this, "Auto-Enhance Applied", Toast.LENGTH_SHORT).show()
        }

        btnAiReframe.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Reframe (9:16)."
            Toast.makeText(this, "Reframe Applied", Toast.LENGTH_SHORT).show()
        }

        btnExport.setOnClickListener {
            tvAiEditorStatus.text = "🤖 Exported successfully!"
            Toast.makeText(this, "Saved to device storage", Toast.LENGTH_LONG).show()
        }
    }
}
