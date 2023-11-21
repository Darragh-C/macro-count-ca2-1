package org.wit.macrocount.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.ActivityMacrocountSearchBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import org.wit.macrocount.R


class MacroCountSearchActivity : AppCompatActivity(), MacroCountListener {

    private lateinit var app: MainApp
    private lateinit var binding: ActivityMacrocountSearchBinding
    private lateinit var adapter: MacroCountAdapter
    private lateinit var userRepo: UserRepo
    private lateinit var userMacros: List<MacroCountModel>
    private lateinit var filteredMacros: List<MacroCountModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMacrocountSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "MacroCount search"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        app = application as MainApp

        userRepo = UserRepo(applicationContext)
        val currentUserId = userRepo.userId

        val layoutManager = LinearLayoutManager(this)
        binding.macroSearchRecyclerView.layoutManager = layoutManager

        if (currentUserId != null) {
            userMacros = app.macroCounts.findByUserId(currentUserId.toLong())
            filteredMacros = userMacros
            adapter = MacroCountAdapter(userMacros, this)
        }

        binding.macroSearchRecyclerView.adapter = adapter

        binding.macroSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.macroSearchView.clearFocus()
                if (query != null) {
                    val filteredMacros = userMacros.filter { it.title.contains(query, ignoreCase = true) }
                    adapter.updateData(filteredMacros)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredMacros = userMacros.filter { it.title.contains(newText, ignoreCase = true) }
                    adapter.updateData(filteredMacros)
                }
                return false
            }
        })

        binding.filterIcon.setOnClickListener {
            showFilterDialog(binding.filterIcon)
        }


    }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        // Handle item click here
        Timber.i("Selected item: $macroCount")
        val resultIntent = Intent()
        resultIntent.putExtra("macrocount_copy", macroCount)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {
        Timber.i("delete click")
    }

    private fun showFilterDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.card_filter_option, null)

        val propertySpinner = dialogView.findViewById<Spinner>(R.id.propertySpinner)
        val operatorSpinner = dialogView.findViewById<Spinner>(R.id.operatorSpinner)
        val valueEditText = dialogView.findViewById<EditText>(R.id.numberEditText)
        val filterButton = dialogView.findViewById<Button>(R.id.filterButton)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        filterButton.setOnClickListener {
            val property = propertySpinner.selectedItem.toString()
            val operator = operatorSpinner.selectedItem.toString()
            val filterNumber = valueEditText.text.toString()

            filteredMacros = applyFilter(property, operator, filterNumber)

            adapter.updateData(filteredMacros)

            dialog.dismiss()

        }
    }

    private fun applyFilter(property: String, operator: String, filterValue: String): List<MacroCountModel> {
        filteredMacros = userMacros

        when (property) {
            "Calories" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.calories.toInt() == filterValue.toInt() }
                    "Less than" -> filteredMacros.filter { it.calories.toInt() <= filterValue.toInt() }
                    "More than" -> filteredMacros.filter { it.calories.toInt() >= filterValue.toInt() }
                    else -> filteredMacros
                }
            }
            "Protein" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.protein.toInt() == filterValue.toInt() }
                    "Less than" -> filteredMacros.filter { it.protein.toInt() <= filterValue.toInt() }
                    "More than" -> filteredMacros.filter { it.protein.toInt() >= filterValue.toInt() }
                    else -> filteredMacros
                }
            }
            "Carbohydrates" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.carbs.toInt() == filterValue.toInt() }
                    "Less than" -> filteredMacros.filter { it.carbs.toInt() <= filterValue.toInt() }
                    "More than" -> filteredMacros.filter { it.carbs.toInt() >= filterValue.toInt() }
                    else -> filteredMacros
                }
            }
            "Fat" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.fat.toInt() == filterValue.toInt() }
                    "Less than" -> filteredMacros.filter { it.fat.toInt() <= filterValue.toInt() }
                    "More than" -> filteredMacros.filter { it.fat.toInt() >= filterValue.toInt() }
                    else -> filteredMacros
                }
            }
            else -> filteredMacros
        }
        return filteredMacros
    }
}