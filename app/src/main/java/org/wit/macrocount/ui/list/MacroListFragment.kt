package org.wit.macrocount.ui.list

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.R
import org.wit.macrocount.adapters.MacroCountAdapter
import org.wit.macrocount.adapters.MacroCountListener
import org.wit.macrocount.databinding.FragmentMacroListBinding
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.utils.SwipeToDeleteCallback
import org.wit.macrocount.utils.SwipeToEditCallback
import timber.log.Timber
import timber.log.Timber.Forest.i

class MacroListFragment : Fragment(), MacroCountListener {

    private lateinit var macroCountAdapter: MacroCountAdapter
    lateinit var loader : AlertDialog
    private var _fragBinding: FragmentMacroListBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var macroListViewModel: MacroListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        loader = createLoader(requireActivity())
        showLoader(loader,"Downloading macros")

        macroListViewModel.observableSnapshotCheck.observe(viewLifecycleOwner, Observer { snapshotCheck ->
            Timber.i("SnapshotCheck change called: $snapshotCheck")
        })

        macroListViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {

                macros ->
            macros?.let {
                Timber.i("macroListViewModel.observableMacroList.observe called, snapshot: ${macroListViewModel.observableSnapshotCheck.value}")
                Timber.i("macroListViewModel.observableMacroList.observe called: ${macroListViewModel.observableMacroList.value}")
                render(macros)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        fragBinding.listFab.setOnClickListener {
            val directions = MacroListFragmentDirections.actionMacroListFragmentToMacroCountFragment("")
            findNavController().navigate(directions)
        }


        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting macro")
                val adapter = fragBinding.recyclerView.adapter as MacroCountAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                Timber.i("viewHolder.itemView.tag: ${viewHolder.itemView.tag}")
                macroListViewModel.delete(FirebaseAuth.getInstance().currentUser!!.uid!!,
                    (viewHolder.itemView.tag as MacroCountModel).uid!!)

                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onMacroCountEdit(viewHolder.itemView.tag as MacroCountModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

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

    private fun render(macroList: ArrayList<MacroCountModel>) {
        Timber.i("render called, snapshot: ${macroListViewModel.observableSnapshotCheck.value}")
        Timber.i("render called: ${macroListViewModel.observableMacroList.value}")
        fragBinding.recyclerView.adapter = MacroCountAdapter(
            macroList,
            this,
            macroListViewModel.observableFavourites.value as ArrayList<String>
        )

        if (macroList.isEmpty()) {
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
        Timber.i("onResume called snapshotCheck: ${macroListViewModel.observableSnapshotCheck.value}")
        Timber.i("onResume called: ${macroListViewModel.observableMacroList.value}")
        super.onResume()
        if (macroListViewModel.observableSnapshotCheck.value == true) {
            macroListViewModel.loadDayMacros(FirebaseAuth.getInstance().currentUser!!.uid)
        } else {
            Timber.i("snapshot waiting at onResume")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        //_fragBinding = null
    }

    override fun onMacroCountClick(macroCount: MacroCountModel) {
        i("onMacroCountClick called $macroCount")
        val directions = macroCount.uid?.let {
            MacroListFragmentDirections.actionMacroListFragmentToMacroDetailFragment(
                it)
        }
        if (directions != null) {
            findNavController().navigate(directions)
        }
    }

    override fun onMacroCountEdit(macroCount: MacroCountModel) {
        i("onMacroCountEdit called $macroCount")
        val directions = macroCount.uid?.let {
            MacroListFragmentDirections.actionMacroListFragmentToMacroCountFragment(
                it)
        }
        if (directions != null) {
            findNavController().navigate(directions)
        }
    }

    override fun onMacroDeleteClick(macroCount: MacroCountModel) {

        if (macroCount.uid != null) {
            Timber.i("Deleting macro: ${macroCount.uid}")
            macroListViewModel.delete(FirebaseAuth.getInstance().currentUser!!.uid,
                macroCount.uid!!
            )
            if (macroListViewModel.observableSnapshotCheck.value == true) {
                macroListViewModel.loadDayMacros(FirebaseAuth.getInstance().currentUser!!.uid)
            } else {
                Timber.i("snapshot waiting at onDelete")
            }

        }
    }

    override fun handleFavourite(macroCount: MacroCountModel, isFavourite: Boolean) {
        Toast.makeText(activity, "Favourite Toggled $isFavourite, for $macroCount", Toast.LENGTH_LONG).show()
        macroListViewModel.handleFavourite(macroCount, isFavourite, FirebaseAuth.getInstance().currentUser!!)
    }

    fun setSwipeRefresh() {
        fragBinding.swipeRefresh.setOnRefreshListener {
            fragBinding.swipeRefresh.isRefreshing = true
            showLoader(loader,"Loading MacroCounts...")
            if (macroListViewModel.observableSnapshotCheck.value == true) {
                macroListViewModel.loadDayMacros(FirebaseAuth.getInstance().currentUser!!.uid)
            } else {
                Timber.i("snapshot waiting at swipe refresh")
            }
        }
    }

    fun checkSwipeRefresh() {
        if (fragBinding.swipeRefresh.isRefreshing)
            fragBinding.swipeRefresh.isRefreshing = false
    }
}