package org.wit.macrocount.ui.detail

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentMacroDetailBinding
import org.wit.macrocount.firebase.FirebaseImageManager
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader
import timber.log.Timber

class MacroDetailFragment : Fragment() {

    private val args by navArgs<MacroDetailFragmentArgs>()
    private lateinit var detailViewModel: MacroDetailViewModel
    private var _fragBinding: FragmentMacroDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog

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

        loader = createLoader(requireActivity())
        showLoader(loader,"Loading macro")

        detailViewModel.getMacro(FirebaseAuth.getInstance().currentUser!!.uid, args.macroid)

        detailViewModel.observableMacro.observe(viewLifecycleOwner, Observer {
            render()
            hideLoader(loader)
        })

        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupMenu()

        Timber.i(" onViewCreated macro ${detailViewModel.observableMacro.value}")


    }

    private fun render() {
        if (detailViewModel.observableMacro.value != null) {
            fragBinding.macrovm = detailViewModel

            if (detailViewModel.observableMacro.value?.image != "") {
                Timber.i("Loading Existing imageUri: ${detailViewModel.observableMacro.value?.image}")
                Picasso.get()
                    .load(detailViewModel.observableMacro.value?.image!!.toUri())
                    .resize(600, 600)
                    .into(fragBinding.macroCountImage)
            }
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

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

}