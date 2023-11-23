package org.wit.macrocount.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import org.wit.macrocount.R
import timber.log.Timber.Forest.i

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        i("onCreateOptionsMenu home called")
        menuInflater.inflate(R.menu.menu_macro_list, menu)
        return super.onCreateOptionsMenu(menu)

    }
}