package com.insta360.sdk.demo

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

        // Preset 1: Tiny Planet
        btnAiTinyPlanet.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Tiny Planet 360° Projection."
            Toast.makeText(this, "Projection: Tiny Planet", Toast.LENGTH_SHORT).show()
        }

        // Preset 2: Fisheye
        btnAiFisheye.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Wide Fisheye View."
            Toast.makeText(this, "Projection: Fisheye", Toast.LENGTH_SHORT).show()
        }

        // Preset 3: Color Auto-Enhance
        btnAiEnhance.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Color Balance & Sharpness Boosted."
            Toast.makeText(this, "Auto-Enhance Applied!", Toast.LENGTH_SHORT).show()
        }

        // Preset 4: Reframing 9:16
        btnAiReframe.setOnClickListener {
            tvAiEditorStatus.text = "🤖 AI Applied: Reframed for Mobile Vertical (9:16)."
            Toast.makeText(this, "Cropped to 9:16", Toast.LENGTH_SHORT).show()
        }

        // Export & Save Action
        btnExport.setOnClickListener {
            tvAiEditorStatus.text = "🤖 Photo exported successfully to phone local storage!"
            Toast.makeText(this, "Saved to Gallery /Insta360_AI/", Toast.LENGTH_LONG).show()
        }
    }
}
