package com.trigrutson.firenotes.note

import android.widget.Toast
import com.trigrutson.firenotes.MainActivity
import android.content.Intent
import android.content.Context
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.firebase.auth.FirebaseUser
import android.widget.ProgressBar
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.test.core.app.ApplicationProvider
import com.trigrutson.firenotes.R


class EditNote : AppCompatActivity() {
    private lateinit var data: Intent
    private lateinit var editNoteTitle: EditText
    private lateinit var editNoteContent: EditText
    private lateinit var fStore: FirebaseFirestore
    private lateinit var spinner: ProgressBar
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fStore = FirebaseFirestore.getInstance()
        spinner = findViewById(R.id.progressBar2)
        user = FirebaseAuth.getInstance().currentUser!!
        data = intent
        editNoteContent = findViewById(R.id.editNoteContent)
        editNoteTitle = findViewById(R.id.editNoteTitle)

        val noteTitle = data.getStringExtra("title")
        val noteContent = data.getStringExtra("content")
        editNoteTitle.setText(noteTitle)
        editNoteContent.setText(noteContent)

        val fab: FloatingActionButton = findViewById(R.id.saveEditedNote)
        fab.setOnClickListener {

            val nTitle = editNoteTitle.text.toString()
            val nContent: String = editNoteContent.text.toString()
            if (nTitle.isEmpty() || nContent.isEmpty()) {
                Toast.makeText(
                    this,
                    "Can not Save note with Empty Field.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                spinner.visibility = View.VISIBLE

                // save note
                val docref = fStore.collection("notes").document(
                    user.uid
                ).collection("myNotes").document(data.getStringExtra("noteId")!!)
                val note: MutableMap<String, Any> = HashMap()
                note["title"] = nTitle
                note["content"] = nContent
                docref.update(note).addOnSuccessListener {
                    Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            ApplicationProvider.getApplicationContext(),
                            MainActivity::class.java
                        )
                    )
                }.addOnFailureListener {
                    Toast.makeText(this, "Error, Try again.", Toast.LENGTH_SHORT)
                        .show()
                    spinner.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 16908332)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}