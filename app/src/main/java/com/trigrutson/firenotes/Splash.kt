package com.trigrutson.firenotes

import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import android.content.Intent

import com.google.firebase.auth.AuthResult

import com.google.android.gms.tasks.OnSuccessListener

import com.google.firebase.auth.FirebaseAuth

import android.os.Bundle
import android.os.Handler


class Splash : AppCompatActivity() {
    lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        fAuth = FirebaseAuth.getInstance()
        val handler = Handler()
        handler.postDelayed(Runnable {
            // check if user is logged in
            if (fAuth.currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                // create new anonymous account
                fAuth.signInAnonymously().addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Logged in With Temporary Account.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error ! " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }, 2000)
    }
}