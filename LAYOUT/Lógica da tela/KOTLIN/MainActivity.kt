package com.example.panicsimulator

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.media.MediaPlayer
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartAlert: Button
    private lateinit var editTitle: TextView
    private lateinit var editMessage: TextView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartAlert = findViewById(R.id.btnStartAlert)
        editTitle = findViewById(R.id.editTitle)
        editMessage = findViewById(R.id.editMessage)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        btnStartAlert.setOnClickListener {
            val title = editTitle.text.toString()
            val message = editMessage.text.toString()
            
            // Aqui você pode escolher a imagem com base no tipo de alerta que deseja
            // Por enquanto, vamos usar a imagem de "Emergência" por padrão
            showAlert(title, message, R.drawable.alerta_emergencia)
        }
    }

    private fun showAlert(title: String, message: String, imageResId: Int) {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            android.graphics.PixelFormat.TRANSLUCENT
        )

        val alertView = LayoutInflater.from(this).inflate(R.layout.alert_layout, null)

        val txtTitle = alertView.findViewById<TextView>(R.id.txtTitle)
        val txtMessage = alertView.findViewById<TextView>(R.id.txtMessage)
        val imgAlert = alertView.findViewById<ImageView>(R.id.imgAlert)
        val btnClose = alertView.findViewById<Button>(R.id.btnClose)

        txtTitle.text = title
        txtMessage.text = message
        imgAlert.setImageResource(imageResId)

        btnClose.setOnClickListener {
            windowManager.removeView(alertView)
            mediaPlayer?.stop()
        }

        windowManager.addView(alertView, params)

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.emergency_sound)
        mediaPlayer?.start()
        vibrator.vibrate(500)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
