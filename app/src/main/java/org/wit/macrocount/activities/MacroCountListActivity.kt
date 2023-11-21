package org.wit.macrocount.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.macrocount.R
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.ActivityMacrocountListBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class MacroCountListActivity : AppCompatActivity(), MacroCountListener {

    private lateinit var app: MainApp
    private lateinit var binding: ActivityMacrocountListBinding
    private lateinit var adapter: MacroCountAdapter
    private lateinit var userRepo: UserRepo
    private var usersDailyMacroObjList = mutableListOf<MacroCountModel>()
    private var currentUserId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMacrocountListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        userRepo = UserRepo(applicationContext)
        currentUserId = userRepo.userId!!.toLong()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        updatedAdapterMacros()
        adapter = MacroCountAdapter(usersDailyMacroObjList, this)

        binding.recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.list_fab)

        fab.setOnClickListener {
            val launcherIntent = Intent(this, MacroCountActivity::class.java)
            getAddResult.launch(launcherIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.data_icon -> {
                val launcherIntent = Intent(this, MacroChartsActivity::class.java)
                startActivity(launcherIntent)
            }
            R.id.item_profile -> {
                Timber.i("clicked profile icon")
                val launcherIntent = Intent(this, UserProfileActivity::class.java)
                startActivity(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                updatedAdapterMacros()
                adapter.updateData(usersDailyMacroObjList)
                adapter.notifyDataSetChanged()
            }
        }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        val launcherIntent = Intent(this, MacroCountActivity::class.java)
        launcherIntent.putExtra("macrocount_edit", macroCount)
        getClickResult.launch(launcherIntent)
    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {
        val position = usersDailyMacroObjList.indexOfFirst { it.id == macroCount.id }
        if (position != -1) {
            usersDailyMacroObjList.removeAt(position)
            adapter.updateData(usersDailyMacroObjList)
            adapter.notifyItemRemoved(position)

            app.days.removeMacro(currentUserId, LocalDate.now().toString(), macroCount.id.toString())
        }
    }

    private val getClickResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                updatedAdapterMacros()
                adapter.updateData(usersDailyMacroObjList)
                adapter.notifyDataSetChanged()
            }
        }

    private fun updatedAdapterMacros() {
        val today = LocalDate.now()
        Timber.i("Checking if logged in user $currentUserId has added macros today on $today")
        val userToday = app.days.findByUserDate(currentUserId!!.toLong(), today)
        Timber.i("User's day object: $userToday")

        val usersDailyMacroList = userToday?.userMacroIds

        var usersDailyMacroListAsObjs = mutableListOf<MacroCountModel>()

        if (!usersDailyMacroList.isNullOrEmpty()) {
            var foundMacros = app.macroCounts.findByIds(usersDailyMacroList)
            if (!foundMacros.isNullOrEmpty()) {
                foundMacros.forEach { it -> it?.let {usersDailyMacroListAsObjs.add(it)} }
            }
            Timber.i("user's daily macro object list usersDailyMacroListAsObjs: $usersDailyMacroListAsObjs")
        }

        Timber.i("updateAdapterMacros result: $usersDailyMacroObjList.toList()")
        usersDailyMacroObjList = usersDailyMacroListAsObjs

    }
}