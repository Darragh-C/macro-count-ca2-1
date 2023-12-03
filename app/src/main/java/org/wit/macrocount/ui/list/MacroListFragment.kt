package org.wit.macrocount.ui.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import org.wit.macrocount.R
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.FragmentMacroListBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import timber.log.Timber.Forest.i
import java.time.LocalDate

class MacroListFragment : Fragment(), MacroCountListener {

    private lateinit var app: MainApp
    private lateinit var userRepo: UserRepo
    private var usersDailyMacroObjList = mutableListOf<MacroCountModel>()
    private var currentUserId: Long = 0
    private lateinit var macroCountAdapter: MacroCountAdapter
    //private val navController = findNavController()

    //private lateinit var adapter: MacroCountAdapter
    private var _fragBinding: FragmentMacroListBinding? = null
    private val fragBinding get() = _fragBinding!!

    private lateinit var macroListViewModel: MacroListViewModel

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
        _fragBinding = FragmentMacroListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        setupMenu()
        activity?.title = getString(R.string.action_macro_list)

        fragBinding.recyclerView.setLayoutManager(LinearLayoutManager(activity))

        macroListViewModel = ViewModelProvider(this).get(MacroListViewModel::class.java)
        macroListViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {
                macros ->
            macros?.let { render(macros) }
        })

//        updatedAdapterMacros()
//        macroCountAdapter = MacroCountAdapter(usersDailyMacroObjList, this)
//        fragBinding.recyclerView.adapter = macroCountAdapter

        fragBinding.listFab.setOnClickListener {
            val directions = MacroListFragmentDirections.actionMacroListFragmentToMacroCountFragment(0)
            findNavController().navigate(directions)
        }

        return root
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_macro_list, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }     }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render(donationsList: List<MacroCountModel>) {
        fragBinding.recyclerView.adapter = MacroCountAdapter(donationsList,this)
        if (donationsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.macrosNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.macrosNotFound.visibility = View.GONE
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        i("onCreateOptionsMenu called")
//        inflater.inflate(R.menu.menu_macro_list, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        i("onOptionsItemSelected called")
//        return NavigationUI.onNavDestinationSelected(item,
//            requireView().findNavController()) || super.onOptionsItemSelected(item)
//    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MacroListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onResume() {
        super.onResume()
        macroListViewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        i("onMacroCountClick called")
        val directions = MacroListFragmentDirections.actionMacroListFragmentToMacroCountFragment(macroCount.id)
        findNavController().navigate(directions)
    }

//    override fun onMacroCountClick(macroCount: MacroCountModel) {
//        val launcherIntent = Intent(requireActivity(), MacroCountFragment::class.java)
//        launcherIntent.putExtra("macrocount_edit", macroCount)
//        getClickResult.launch(launcherIntent)
//    }
//
//    private val getClickResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                updatedAdapterMacros()
//                macroCountAdapter.updateData(usersDailyMacroObjList)
//                macroCountAdapter.notifyDataSetChanged()
//            }
//        }



//    private fun updatedAdapterMacros() {
//        val today = LocalDate.now()
//        Timber.i("Checking if logged in user $currentUserId has added macros today on $today")
//        val userToday = app.days.findByUserDate(currentUserId!!.toLong(), today)
//        Timber.i("User's day object: $userToday")
//
//        val usersDailyMacroList = userToday?.userMacroIds
//
//        var usersDailyMacroListAsObjs = mutableListOf<MacroCountModel>()
//
//        if (!usersDailyMacroList.isNullOrEmpty()) {
//            var foundMacros = app.macroCounts.findByIds(usersDailyMacroList)
//            if (!foundMacros.isNullOrEmpty()) {
//                foundMacros.forEach { it -> it?.let {usersDailyMacroListAsObjs.add(it)} }
//            }
//            Timber.i("user's daily macro object list usersDailyMacroListAsObjs: $usersDailyMacroListAsObjs")
//        }
//
//        Timber.i("updateAdapterMacros result: $usersDailyMacroObjList.toList()")
//        usersDailyMacroObjList = usersDailyMacroListAsObjs
//
//    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {
        val position = usersDailyMacroObjList.indexOfFirst { it.id == macroCount.id }
        if (position != -1) {
            usersDailyMacroObjList.removeAt(position)
            macroCountAdapter.updateData(usersDailyMacroObjList)
            macroCountAdapter.notifyItemRemoved(position)

            app.days.removeMacro(currentUserId, LocalDate.now().toString(), macroCount.id.toString())
        }
    }
}