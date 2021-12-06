package com.trigrutson.firenotes.note

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseUser
import android.widget.ProgressBar
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.firestore.DocumentReference
import com.trigrutson.firenotes.MainActivity
import com.trigrutson.firenotes.R


class AddNote : AppCompatActivity() {
    private lateinit var fStore: FirebaseFirestore
    private lateinit var noteTitle: EditText
    private lateinit var noteContent: EditText
    private lateinit var progressBarSave: ProgressBar
    private lateinit var user: FirebaseUser
    private lateinit var nTitle: String
    private lateinit var nContent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fStore = FirebaseFirestore.getInstance()
        noteContent = findViewById(R.id.addNoteContent)
        noteTitle = findViewById(R.id.addNoteTitle)
        progressBarSave = findViewById(R.id.progressBar)
        user = FirebaseAuth.getInstance().currentUser!!
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            if (isInputCorrect())
                createNote()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.close_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.close) {
            Toast.makeText(this, "Not Saved.", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isInputCorrect(): Boolean {
        nTitle = noteTitle.text.toString()
        nContent = noteContent.text.toString()
        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(
                this,
                "Can not Save note with Empty Field.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun createNote() {
        progressBarSave.visibility = View.VISIBLE
        // save note
        val docref = fStore.collection("notes").document(
            user.uid
        ).collection("myNotes").document()
        val note: MutableMap<String, Any> = HashMap()
        note["title"] = nTitle
        note["content"] = nContent

        docref.set(note).addOnSuccessListener {
            Toast.makeText(this, "Note Added.", Toast.LENGTH_SHORT).show()
            this.startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }.addOnFailureListener {
            Toast.makeText(this, "Error, Try again.", Toast.LENGTH_SHORT).show()
            progressBarSave.visibility = View.VISIBLE
        }

    }
}