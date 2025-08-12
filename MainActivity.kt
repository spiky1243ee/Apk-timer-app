package com.example.simpletimer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var timer: CountDownTimer? = null
    private var timeLeftMs: Long = 0
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvTime = findViewById<TextView>(R.id.tvTime)
        val etMinutes = findViewById<EditText>(R.id.etMinutes)
        val etSeconds = findViewById<EditText>(R.id.etSeconds)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnChat = findViewById<Button>(R.id.btnChat)

        fun updateDisplay(ms: Long) {
            val totalSec = (ms / 1000).toInt()
            val m = totalSec / 60
            val s = totalSec % 60
            tvTime.text = String.format("%02d:%02d", m, s)
        }

        btnStart.setOnClickListener {
            if (!isRunning) {
                val minutes = etMinutes.text.toString().toIntOrNull() ?: 0
                val seconds = etSeconds.text.toString().toIntOrNull() ?: 0
                if (timeLeftMs == 0L) {
                    timeLeftMs = ((minutes * 60) + seconds) * 1000L
                }
                timer = object : CountDownTimer(timeLeftMs, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timeLeftMs = millisUntilFinished
                        updateDisplay(timeLeftMs)
                    }

                    override fun onFinish() {
                        isRunning = false
                        updateDisplay(0)
                    }
                }.start()
                isRunning = true
            }
        }

        btnPause.setOnClickListener {
            timer?.cancel()
            isRunning = false
        }

        btnReset.setOnClickListener {
            timer?.cancel()
            isRunning = false
            timeLeftMs = 0
            updateDisplay(0)
        }

        btnChat.setOnClickListener {
            val chatUrl = "https://chat.openai.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(chatUrl))
            startActivity(intent)
        }

        updateDisplay(0)
    }
}
