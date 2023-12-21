package org.wit.macrocount.ui.macrosearch

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.R
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.FragmentMacroSearchBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader
import timber.log.Timber

class MacroSearchFragment : Fragment(), MacroCountListener {
    private lateinit var macroCountAdapter: MacroCountAdapter
    private lateinit var filteredMacros: ArrayList<MacroCountModel>
    lateinit var loader : AlertDialog

    private var _fragBinding: FragmentMacroSearchBinding? = null

    private val fragBinding get() = _fragBinding!!

    private lateinit var macroSearchViewModel: MacroSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        setupMenu()

        fragBinding.macroSearchRecyclerView.setLayoutManager(LinearLayoutManager(activity))

        macroSearchViewModel = ViewModelProvider(this).get(MacroSearchViewModel::class.java)

        loader = createLoader(requireActivity())
        showLoader(loader,"Loading macros")

        macroSearchViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {
                macros ->
            macros?.let {
                render(macros as ArrayList<MacroCountModel>)
                hideLoader(loader)
            }
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
                    val filteredMacros = macroSearchViewModel.observableMacroList.value?.filter { it.title.contains(query, ignoreCase = true) }
                    macroCountAdapter.updateData(filteredMacros as ArrayList<MacroCountModel>)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredMacros = macroSearchViewModel.observableMacroList.value?.filter { it.title.contains(newText, ignoreCase = true) }
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

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_macro_search, menu)
                val item = menu.findItem(R.id.toggleDonations) as MenuItem
                item.setActionView(R.layout.togglebutton_layout)
                val toggleDonations: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
                toggleDonations.isChecked = false

                toggleDonations.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) macroSearchViewModel.loadAll()
                    else macroSearchViewModel.load()
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }     }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(macroList: ArrayList<MacroCountModel>) {

        fragBinding.macroSearchRecyclerView.adapter = MacroCountAdapter(
            macroList,
            this,
            macroSearchViewModel.observableFavourites.value as ArrayList<String>
        )


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
        filteredMacros = macroSearchViewModel.observableMacroList.value as ArrayList<MacroCountModel>

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

    override fun handleFavourite(macroCount: MacroCountModel, isFavourite: Boolean) {
        Toast.makeText(activity, "Favourite Toggled $isFavourite, for $macroCount", Toast.LENGTH_LONG).show()
        macroSearchViewModel.handleFavourite(macroCount, isFavourite, FirebaseAuth.getInstance().currentUser!!)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MacroSearchFragment().apply {
            }
    }
}