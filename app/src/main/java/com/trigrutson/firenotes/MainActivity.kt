package com.trigrutson.firenotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.cardview.widget.CardView
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.trigrutson.firenotes.auth.Login
import com.trigrutson.firenotes.auth.Register
import com.trigrutson.firenotes.model.Note
import com.trigrutson.firenotes.note.AddNote
import com.trigrutson.firenotes.note.EditNote
import com.trigrutson.firenotes.note.NoteDetails
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var noteLists: RecyclerView
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var noteAdapter: FirestoreRecyclerAdapter<Note, NoteViewHolder>
    private lateinit var user: FirebaseUser
    private lateinit var fireAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        fireStore = FirebaseFirestore.getInstance()
        fireAuth = FirebaseAuth.getInstance()

        user = fireAuth.currentUser!!

        val query: Query = fireStore.collection("notes").document(user.uid).collection("myNotes")
            .orderBy("title", Query.Direction.DESCENDING)
        // query notes > uuid > mynotes

        val allNotes = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        noteAdapter = object : FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onBindViewHolder(noteViewHolder: NoteViewHolder, i: Int, note: Note) {
                noteViewHolder.noteTitle.text = note.title
                noteViewHolder.noteContent.text = note.content
                val code: Int = getRandomColor()
                noteViewHolder.mCardView.setCardBackgroundColor(
                    noteViewHolder.view.resources.getColor(code, null)
                )
                val docId: String = noteAdapter.snapshots.getSnapshot(i).id
                noteViewHolder.view.setOnClickListener {
                    val intent = Intent(it.context, NoteDetails::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("code", code)
                    intent.putExtra("noteId", docId)
                    it.context.startActivity(intent)
                }
                val menuIcon: ImageView = noteViewHolder.view.findViewById(R.id.menuIcon)
                menuIcon.setOnClickListener {
                    fun onClick(v: View) {
                        val docId: String = noteAdapter.snapshots.getSnapshot(i).id
                        val menu = PopupMenu(v.context, v)
                        menu.gravity = Gravity.END
                        menu.menu.add("Edit")
                            .setOnMenuItemClickListener {
                                val intent = Intent(v.context, EditNote::class.java)
                                intent.putExtra("title", note.title)
                                intent.putExtra("content", note.content)
                                intent.putExtra("noteId", docId)
                                startActivity(intent)
                                false
                            }
                        menu.menu.add("Delete")
                            .setOnMenuItemClickListener {
                                val docRef: DocumentReference =
                                    fireStore.collection("notes").document(user.uid)
                                        .collection("myNotes").document(docId)
                                docRef.delete().addOnSuccessListener {
                                    // note deleted
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error in Deleting Note.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                false
                            }
                        menu.show()
                    }
                    onClick(it)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_view_layout, parent, false)
                return NoteViewHolder(view)
            }
        }

        noteLists = findViewById(R.id.notelist)
        drawerLayout = findViewById(R.id.drawer)
        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener {
            onNavigationItemSelected(it)
        }
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        noteLists.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        noteLists.adapter = noteAdapter

        val headerView: View = navView.getHeaderView(0)
        val username: TextView = headerView.findViewById(R.id.userDisplayName)
        val userEmail: TextView = headerView.findViewById(R.id.userDisplayEmail)

        if (user.isAnonymous) {
            userEmail.visibility = View.GONE
            username.text = "Temporary User"
        } else {
            userEmail.text = user.email
            username.text = user.displayName
        }

        val fab = findViewById<FloatingActionButton>(R.id.addNoteFloat)
        fab.setOnClickListener { view ->
            startActivity(Intent(view.context, AddNote::class.java))
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            //finish()
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.notes -> {
                Toast.makeText(this, "Notes", Toast.LENGTH_SHORT).show()
            }
            R.id.addNote -> {
                startActivity(Intent(this, AddNote::class.java))
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            }
            R.id.logInAccount -> if (user.isAnonymous) {
                startActivity(Intent(this, Login::class.java))
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            } else {
                Toast.makeText(this, "Your Are Connected.", Toast.LENGTH_SHORT).show()
            }
            R.id.logout -> checkUser()
            else -> Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun checkUser() {
        // if user is real or not
        if (user.isAnonymous) {
            displayTemporaryUserLogoutAlert()
        } else {
            displayUserLogoutAlert()
        }
    }

    private fun displayTemporaryUserLogoutAlert() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure ?")
            .setMessage("You are logged in with Temporary Account. Logging out will Delete All the notes.")
            .setPositiveButton(
                "Sync Note"
            ) { _, _ ->
                startActivity(Intent(applicationContext, Register::class.java))
                finish()
            }.setNegativeButton(
                "Logout"
            ) { _, _ ->
                user.delete().addOnSuccessListener {
                    startActivity(Intent(applicationContext, Splash::class.java))
                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                }
            }
        warning.show()
    }

    private fun displayUserLogoutAlert() {
        val warning = AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Log out from account?")
            .setPositiveButton("No, go back") {_, _ ->
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            }
            .setNegativeButton("Logout") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(applicationContext, Splash::class.java))
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            }
        warning.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.settings) {
            Toast.makeText(this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var noteTitle: TextView = itemView.findViewById(R.id.titles)
        var noteContent: TextView = itemView.findViewById(R.id.content)
        var view: View = itemView
        var mCardView: CardView = itemView.findViewById(R.id.noteCard)

    }

    private fun getRandomColor(): Int {
        val colorCode: MutableList<Int> = ArrayList()
        colorCode.add(R.color.blue)
        colorCode.add(R.color.yellow)
        colorCode.add(R.color.skyblue)
        colorCode.add(R.color.lightPurple)
        colorCode.add(R.color.lightGreen)
        colorCode.add(R.color.gray)
        colorCode.add(R.color.pink)
        colorCode.add(R.color.red)
        colorCode.add(R.color.greenlight)
        colorCode.add(R.color.notgreen)
        val number: Int = Random.nextInt(colorCode.size)
        return colorCode[number]
    }

    override fun onStart() {
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        noteAdapter.stopListening()
    }
}