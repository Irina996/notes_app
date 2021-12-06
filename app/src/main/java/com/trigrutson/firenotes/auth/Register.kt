package com.trigrutson.firenotes.auth

import com.trigrutson.firenotes.MainActivity
import android.content.Intent
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.StateSet.TAG
import com.trigrutson.firenotes.R

class Register : AppCompatActivity() {
    private lateinit var rUserName: EditText
    private lateinit var rUserEmail: EditText
    private lateinit var rUserPass: EditText
    private lateinit var rUserConfPass: EditText
    private lateinit var syncAccount: Button
    private lateinit var loginAct: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.title = "Connect to FireNotes"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        rUserName = findViewById(R.id.userName)
        rUserEmail = findViewById(R.id.userEmail)
        rUserPass = findViewById(R.id.password)
        rUserConfPass = findViewById(R.id.passwordConfirm)
        syncAccount = findViewById(R.id.createAccount)
        loginAct = findViewById(R.id.login)
        progressBar = findViewById(R.id.progressBar4)
        fAuth = FirebaseAuth.getInstance()
        loginAct.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))
        }
        syncAccount.setOnClickListener {
            syncAc()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun syncAc() {
        val uUsername = rUserName.text.toString()
        val uUserEmail = rUserEmail.text.toString()
        val uUserPass = rUserPass.text.toString()
        val uConfPass = rUserConfPass.text.toString()
        if (uUserEmail.isEmpty() || uUsername.isEmpty() || uUserPass.isEmpty() || uConfPass.isEmpty()) {
            Toast.makeText(this, "All Fields Are Required.", Toast.LENGTH_SHORT)
                .show()

        } else {
            if (uUserPass != uConfPass) {
                rUserConfPass.error = "Password Do not Match."
                return
            }
            if (uUserPass.length < 6) {
                rUserConfPass.error = "Password should be 6 characters at least."
                return
            }
            progressBar.visibility = View.VISIBLE
            val credential = EmailAuthProvider.getCredential(uUserEmail, uUserPass)

            fAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")

                        Toast.makeText(this, "Notes are Synced.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        val usr = fAuth.currentUser
                        val request = UserProfileChangeRequest.Builder()
                            .setDisplayName(uUsername)
                            .build()
                        usr!!.updateProfile(request)
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                        finish()
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", task.exception)

                        Toast.makeText(
                            this,
                            "Failed to Connect. Try Again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.VISIBLE
                    }
                }
        }
    }
}