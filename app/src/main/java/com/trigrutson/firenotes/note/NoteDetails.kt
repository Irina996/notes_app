package com.trigrutson.firenotes.note

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.trigrutson.firenotes.R


class NoteDetails : AppCompatActivity() {
    private lateinit var data: Intent

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        data = intent
        val content = findViewById<TextView>(R.id.noteDetailsContent)
        val title = findViewById<TextView>(R.id.noteDetailsTitle)
        content.movementMethod = ScrollingMovementMethod()
        content.text = data.getStringExtra("content")
        title.text = data.getStringExtra("title")
        content.setBackgroundColor(resources.getColor(data.getIntExtra("code", 0), null))
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val i = Intent(it.context, EditNote::class.java)
            i.putExtra("title", data.getStringExtra("title"))
            i.putExtra("content", data.getStringExtra("content"))
            i.putExtra("noteId", data.getStringExtra("noteId"))
            startActivity(i)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 16908332)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}