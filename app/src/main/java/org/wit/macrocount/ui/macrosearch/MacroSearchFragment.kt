package org.wit.macrocount.ui.macrosearch

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.macrocount.R
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.FragmentMacroSearchBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber

class MacroSearchFragment : Fragment(), MacroCountListener {
    private lateinit var app: MainApp
    private lateinit var macroCountAdapter: MacroCountAdapter
    private lateinit var userRepo: UserRepo
    private lateinit var userMacros: ArrayList<MacroCountModel>
    private lateinit var filteredMacros: ArrayList<MacroCountModel>
    private var currentUserId: Long = 0

    private var _fragBinding: FragmentMacroSearchBinding? = null
    private val fragBinding get() = _fragBinding!!

    private lateinit var macroSearchViewModel: MacroSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        userRepo = UserRepo(app.applicationContext)
        currentUserId = userRepo.userId!!.toLong()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _fragBinding = FragmentMacroSearchBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_macro_search)

        fragBinding.macroSearchRecyclerView.setLayoutManager(LinearLayoutManager(activity))

        macroSearchViewModel = ViewModelProvider(this).get(MacroSearchViewModel::class.java)
        macroSearchViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {
                macros ->
            macros?.let { render(macros  as ArrayList<MacroCountModel>) }
        })

//        if (currentUserId != null) {
//            //userMacros = app.macroCounts.findByUserId(currentUserId)
//            Timber.i("userMacros at search: $userMacros")
//            //filteredMacros = userMacros
//            macroCountAdapter = MacroCountAdapter(userMacros, this)
//            fragBinding.macroSearchRecyclerView.adapter = macroCountAdapter
//
//        }

        fragBinding.macroSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                fragBinding.macroSearchView.clearFocus()
                if (query != null) {
                    val filteredMacros = userMacros.filter { it.title.contains(query, ignoreCase = true) }
                    macroCountAdapter.updateData(filteredMacros as ArrayList<MacroCountModel>)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredMacros = userMacros.filter { it.title.contains(newText, ignoreCase = true) }
                    macroCountAdapter.updateData(filteredMacros as ArrayList<MacroCountModel>)
                }
                return false
            }
        })

        fragBinding.filterIcon.setOnClickListener {
            showFilterDialog(fragBinding.filterIcon)
        }


        return root
    }

    private fun render(macroList: ArrayList<MacroCountModel>) {
        fragBinding.macroSearchRecyclerView.adapter = MacroCountAdapter(macroList,this)
        if (macroList.isEmpty()) {
            fragBinding.macroSearchRecyclerView.visibility = View.GONE
            fragBinding.macrosNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.macroSearchRecyclerView.visibility = View.VISIBLE
            fragBinding.macrosNotFound.visibility = View.GONE
        }
    }

    private fun showFilterDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.card_filter_option, null)

        val propertySpinner = dialogView.findViewById<Spinner>(R.id.propertySpinner)
        val operatorSpinner = dialogView.findViewById<Spinner>(R.id.operatorSpinner)
        val valueEditText = dialogView.findViewById<EditText>(R.id.numberEditText)
        val filterButton = dialogView.findViewById<Button>(R.id.filterButton)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        filterButton.setOnClickListener {
            val property = propertySpinner.selectedItem.toString()
            val operator = operatorSpinner.selectedItem.toString()
            val filterNumber = valueEditText.text.toString()

            filteredMacros = applyFilter(property, operator, filterNumber)

            macroCountAdapter.updateData(filteredMacros)

            dialog.dismiss()

        }
    }

    private fun applyFilter(property: String, operator: String, filterValue: String): ArrayList<MacroCountModel> {
        filteredMacros = userMacros

        when (property) {
            "Calories" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.calories.toInt() == filterValue.toInt() } as ArrayList<MacroCountModel>
                    "Less than" -> filteredMacros.filter { it.calories.toInt() <= filterValue.toInt() } as ArrayList<MacroCountModel>
                    "More than" -> filteredMacros.filter { it.calories.toInt() >= filterValue.toInt() } as ArrayList<MacroCountModel>
                    else -> filteredMacros
                }
            }
            "Protein" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.protein.toInt() == filterValue.toInt() } as ArrayList<MacroCountModel>
                    "Less than" -> filteredMacros.filter { it.protein.toInt() <= filterValue.toInt() } as ArrayList<MacroCountModel>
                    "More than" -> filteredMacros.filter { it.protein.toInt() >= filterValue.toInt() } as ArrayList<MacroCountModel>
                    else -> filteredMacros
                }
            }
            "Carbohydrates" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.carbs.toInt() == filterValue.toInt() } as ArrayList<MacroCountModel>
                    "Less than" -> filteredMacros.filter { it.carbs.toInt() <= filterValue.toInt() } as ArrayList<MacroCountModel>
                    "More than" -> filteredMacros.filter { it.carbs.toInt() >= filterValue.toInt() } as ArrayList<MacroCountModel>
                    else -> filteredMacros
                }
            }
            "Fat" -> {
                filteredMacros = when (operator) {
                    "Equals" -> filteredMacros.filter { it.fat.toInt() == filterValue.toInt() } as ArrayList<MacroCountModel>
                    "Less than" -> filteredMacros.filter { it.fat.toInt() <= filterValue.toInt() } as ArrayList<MacroCountModel>
                    "More than" -> filteredMacros.filter { it.fat.toInt() >= filterValue.toInt() } as ArrayList<MacroCountModel>
                    else -> filteredMacros
                }
            }
            else -> filteredMacros
        }
        return filteredMacros
    }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        // Handle item click here
        Timber.i("Selected item: $macroCount")
        val resultIntent = Intent()
        resultIntent.putExtra("searched_macro", macroCount)

        val resultBundle = Bundle().apply {
            putParcelable("searched_macro", macroCount)
        }
        parentFragmentManager.setFragmentResult("search_result", resultBundle)
        findNavController().navigateUp()
    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {
        Timber.i("delete click")
    }

    override fun onMacroCountEdit(macroCount: MacroCountModel) {
        Timber.i("edit click")
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MacroSearchFragment().apply {
            }
    }
}