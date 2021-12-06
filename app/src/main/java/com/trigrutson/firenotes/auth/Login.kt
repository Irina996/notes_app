package com.trigrutson.firenotes.auth

import android.content.Intent
import com.trigrutson.firenotes.MainActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.trigrutson.firenotes.R


class Login : AppCompatActivity() {
    private lateinit var lEmail: EditText
    private lateinit var lPassword: EditText
    private lateinit var loginNow: Button
    private lateinit var forgetPass: TextView
    private lateinit var createAcc: TextView
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Login to FireNotes"

        lEmail = findViewById(R.id.email)
        lPassword = findViewById(R.id.lPassword)
        loginNow = findViewById(R.id.loginBtn)
        spinner = findViewById(R.id.progressBar3)
        forgetPass = findViewById(R.id.forgotPasword)
        createAcc = findViewById(R.id.createAccount)
        user = FirebaseAuth.getInstance().currentUser!!
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        showWarning()

        loginNow.setOnClickListener {
            val mEmail = lEmail.text.toString()
            val mPassword = lPassword.text.toString()
            if (mEmail.isEmpty() || mPassword.isEmpty()) {
                Toast.makeText(this, "Fields Are Required.", Toast.LENGTH_SHORT).show()
            } else {
                // delete notes first
                spinner.visibility = View.VISIBLE
                if (fAuth.currentUser!!.isAnonymous) {
                    val user = fAuth.currentUser
                    fStore.collection("notes").document(user!!.uid).delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "All Temp Notes are Deleted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    // delete Temp user
                    user.delete().addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Temp user Deleted.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener {
                    Toast.makeText(this, "Success !", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Login Failed. " + e.message, Toast.LENGTH_SHORT)
                        .show()
                    spinner.visibility = View.GONE
                }
            }

        }
        createAcc.setOnClickListener {
            startActivity(Intent(applicationContext, Register::class.java))

        }
    }

    private fun showWarning() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure ?")
            .setMessage("Linking Existing Account Will delete all the temp notes. Create New Account To Save them.")
            .setPositiveButton( "Save Notes" ) { _, _ ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton("Its Ok" ) { _, _ ->
                // do nothing
            }
        warning.show()
    }
}