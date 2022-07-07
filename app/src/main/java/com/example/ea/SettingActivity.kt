package com.example.ea

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Phoenix 21 June -> 6 July 2022
class SettingActivity : AppCompatActivity() {

    private lateinit var contact: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)

        val topToolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val contactField = findViewById<EditText>(R.id.contact_field)

        // Get and set contact_field
        retrieveData(contactField)

        setSupportActionBar(topToolbar)
        supportActionBar?.apply {
            title = "Settings"

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val saveBt = findViewById<Button>(R.id.save)
        saveBt.setOnClickListener {
            contact = contactField.text.toString()
            saveData(contact)
        }
    }

    // this event will enable the back
    // function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }

    // Save contact number data to SharePreference
    private fun saveData(contact: String) {
        val editor = getSharedPreferences("MyPreference", MODE_PRIVATE).edit()
        editor.putString("contact", contact)
        editor.commit()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
    }

    // Retrieve contact number data from SharePreference
    private fun retrieveData(contact_field: EditText) {
        val prefs = getSharedPreferences("MyPreference", MODE_PRIVATE)
        contact_field.setText(prefs.getString("contact", ""))
    }

}

/*
Phoenix 21 June -> 6 July 2022
@CREDIT,@SOURCE
https://www.geeksforgeeks.org/how-to-post-data-to-api-using-volley-in-android/

# Django REST api
https://medium.com/swlh/build-your-first-rest-api-with-django-rest-framework-e394e39a482c
*/
