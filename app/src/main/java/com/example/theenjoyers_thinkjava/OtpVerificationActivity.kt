package com.example.theenjoyers_thinkjava

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity  // <- Penting!

class OtpVerificationActivity : AppCompatActivity() {  // <- Tambahkan inheritance ke AppCompatActivity
    private lateinit var otpDigit1: EditText
    private lateinit var otpDigit2: EditText
    private lateinit var otpDigit3: EditText
    private lateinit var otpDigit4: EditText
    private lateinit var buttonNext: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)  // <- Pastikan nama XML layout sesuai

        otpDigit1 = findViewById(R.id.otp_digit_1)
        otpDigit2 = findViewById(R.id.otp_digit_2)
        otpDigit3 = findViewById(R.id.otp_digit_3)
        otpDigit4 = findViewById(R.id.otp_digit_4)
        buttonNext = findViewById(R.id.buttonNextOtp)
        backButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener { finish() }

        buttonNext.setOnClickListener {
            val otp = otpDigit1.text.toString() +
                    otpDigit2.text.toString() +
                    otpDigit3.text.toString() +
                    otpDigit4.text.toString()

            if (otp.length == 4) {
                // Lanjut ke halaman reset password
                val intent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Isi semua digit OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}