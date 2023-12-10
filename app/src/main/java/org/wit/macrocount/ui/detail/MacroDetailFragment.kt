package org.wit.macrocount.ui.detail

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.squareup.picasso.Picasso
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentMacroDetailBinding
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroDetailFragment : Fragment() {

    private val args by navArgs<MacroDetailFragmentArgs>()
    private lateinit var detailViewModel: MacroDetailViewModel
    private var _fragBinding: FragmentMacroDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    companion object {
        fun newInstance() = MacroDetailFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_macro_detail, container, false)

        _fragBinding = FragmentMacroDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(MacroDetailViewModel::class.java)



        return root

        //val args = arguments
//        Timber.i("MacroDetailFragment passed args ${args}")
//        Toast.makeText(context,"Macro ID Selected : ${args}", Toast.LENGTH_LONG).show()
//        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupMenu()

        detailViewModel.observableMacro.observe(viewLifecycleOwner, Observer {render() })
        //Timber.i("observableMacro ${macro}")

        detailViewModel.getMacro(args.macroid)

        if (detailViewModel.observableMacro.value?.image != Uri.EMPTY) {
            Timber.i("Loading image: ${detailViewModel.observableMacro.value?.image}")
            Picasso.get()
                .load(detailViewModel.observableMacro.value?.image)
                .into(fragBinding.macroCountImage)
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_macro_detail, menu)
            }

            val action = MacroDetailFragmentDirections.actionMacroDetailFragmentToMacroCountFragment(args.macroid)

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.macro_count -> {
                        findNavController().navigate(action)
                        return true
                    }
                }
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render() {
        //fragBinding.
        fragBinding.macrovm = detailViewModel
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

}