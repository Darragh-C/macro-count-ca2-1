package org.wit.macrocount.ui.macro

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentMacroCountBinding
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.showImagePicker
import timber.log.Timber
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader
import org.wit.macrocount.ui.detail.MacroDetailFragmentArgs
import org.wit.macrocount.ui.login.LoggedInViewModel

class MacroCountFragment : Fragment() {

    private var editMacro = false
    private var copyMacro = false
    private var currentUserId: Long = 0
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var loggedInViewModel : LoggedInViewModel
    private var _fragBinding: FragmentMacroCountBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var macroViewModel: EditMacroViewModel
    private val args by navArgs<MacroDetailFragmentArgs>()
    lateinit var loader : AlertDialog

    companion object {
        fun newInstance() = MacroCountFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        loggedInViewModel = ViewModelProvider(requireActivity()).get(LoggedInViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _fragBinding = FragmentMacroCountBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_macro_list)

        macroViewModel = ViewModelProvider(requireActivity()).get(EditMacroViewModel::class.java)

        loader = createLoader(requireActivity())
        showLoader(loader,"Loading macro")

        val macroId = args.macroid
        if (macroId != "") {
            editMacro = true
            macroViewModel.getMacro(FirebaseAuth.getInstance().currentUser!!.uid, macroId)
            macroViewModel.observableMacro.observe(viewLifecycleOwner, Observer {
                render()
                hideLoader(loader)
            })
        } else {
            macroViewModel.setMacro(MacroCountModel())
        }
        return root
    }



    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_macrocount, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return NavigationUI.onNavDestinationSelected(menuItem,
                    requireView().findNavController())
            }     }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupMenu()

        fragBinding.calorieSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(calorieSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Timber.i("calorie onProgressChanged")
                macroViewModel.editCalories(progress)
                Timber.i("Macro progress: ${macroViewModel.observableMacro.value}")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.proteinSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(proteinSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                macroViewModel.editProtein(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.carbsSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(carbsSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                macroViewModel.editCarbs(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.fatSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(fatSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                macroViewModel.editFat(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        fragBinding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }


        fragBinding.searchFab.setOnClickListener() {
            val directions = MacroCountFragmentDirections.actionMacroCountFragmentToMacroSearchFragment()
            findNavController().navigate(directions)

            parentFragmentManager.setFragmentResultListener("search_result", this) { key, result ->
                val selectedItem = result.getParcelable<MacroCountModel>("searched_macro")

                Timber.i("Got searchedMacro Result $selectedItem")

                if (selectedItem != null) {
                    Timber.i("returned selectedItem: ${selectedItem}")

                    copyMacro = true

                    macroViewModel.setMacro(selectedItem)
                    macroViewModel.setCopy(selectedItem)

                }

            }
        }

        fragBinding.btnAdd.setOnClickListener() {
            val validationChecks = listOf(
                Pair(macroViewModel.observableMacro.value?.title?.isEmpty(), R.string.snackbar_macroCountTitle),
            )
            var validationFailed = false
            for (check in validationChecks) {
                if (check.first == true) {
                    Snackbar
                        .make(it, check.second, Snackbar.LENGTH_LONG)
                        .show()
                    validationFailed = true
                    break
                }
            }

            if (!validationFailed) {
                Timber.i("Validation passed")
                if (editMacro) {
                    Timber.i("updating macroCount: ${macroViewModel.observableMacro.value}")
                    macroViewModel.updateMacro(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        macroViewModel.observableMacro.value!!.uid!!,
                        macroViewModel.observableMacro.value!!
                    )
                } else if (copyMacro) {
                    Timber.i("copiedMacro: ${macroViewModel.observableCopy.value}")
                    if (macroViewModel.observableCopy.equals(macroViewModel.observableMacro.value)) {
                        macroViewModel.addToDay(currentUserId)
                        Timber.i("copied macroCount added to today: ${macroViewModel.observableMacro.value}")
                    } else {
                        macroViewModel.addMacro(loggedInViewModel.liveFirebaseUser, macroViewModel.observableMacro.value!!)
                        Timber.i("creating new macroCount from copied and edited macro: $macroViewModel.observableMacro.value")
                    }
                } else {

//                        val currentUser = FirebaseAuth.getInstance().currentUser
//
//                        Timber.i("adding macroCount : ${macroViewModel.observableMacro.value} for ${currentUser} or ${currentUser!!.uid}")
                    macroViewModel.addMacro(loggedInViewModel.liveFirebaseUser, macroViewModel.observableMacro.value!!)

                }
                //Timber.i("LocalDate.now(): ${LocalDate.now()}")
//                Timber.i(
//                    "Today's macros: ${
//                        app.days.findByUserDate(
//                            currentUserId,
//                            LocalDate.now()
//                        )
//                    }"
                //)
                Timber.i("starting navigation")
                val directions = MacroCountFragmentDirections.actionMacroCountFragmentToMacroListFragment()
                findNavController().navigate(directions)
            }
        }


//            fragBinding.takePhoto.setOnClickListener {
//                val action = MacroCountFragmentDirections.actionMacroCountFragmentToCameraFragment()
//                findNavController().navigate(action)
//
//                parentFragmentManager.setFragmentResultListener("photoResult", this) { key, result ->
//                    val imageUri = result.getParcelable<Uri>("image_uri")
//
//                    Timber.i("Got Result $imageUri")
//                    if (imageUri != null) {
//                        macroCount.image = imageUri.toString()
//                        Timber.i("Got Result macrocount image ${macroCount.image}")
//                        Picasso.get()
//                            .load(macroCount.image)
//                            .into(fragBinding.macroCountImage)
//                    }
//                }
//            }



            //registerImagePickerCallback()


//        }

    }

    private fun render() {

        if (macroViewModel.observableMacro.value != null) {
            fragBinding.macrovm = macroViewModel

//            if (macroViewModel.observableMacro.value?.image != "") {
//                Picasso.get()
//                    .load(macroViewModel.observableMacro.value?.image)
//                    .into(fragBinding.macroCountImage)
            }


    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Result ${result.data!!.data!!}")
                            macroViewModel.observableMacro.value?.image = result.data!!.data!!.toString()
                            Timber.i("Got Result macrocount image ${macroViewModel.observableMacro.value?.image}")
                            Picasso.get()
                                .load(macroViewModel.observableMacro.value?.image)
                                .into(fragBinding.macroCountImage)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    fun initData(value: String): String {
        return if (value.isNotEmpty()) value else "0"
    }

//    companion object {
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MacroCountFragment().apply {
//                arguments = Bundle().apply {
//                }
//            }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        render()

    }
}