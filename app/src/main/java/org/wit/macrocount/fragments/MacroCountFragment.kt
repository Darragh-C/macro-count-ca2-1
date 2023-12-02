package org.wit.macrocount.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.wit.macrocount.R
import org.wit.macrocount.activities.CameraActivity
import org.wit.macrocount.databinding.FragmentMacroCountBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.showImagePicker
import timber.log.Timber
import java.time.LocalDate
import android.app.Activity
import android.net.Uri
import org.wit.macrocount.fragments.MacroCountFragmentDirections.Companion.actionMacroCountFragmentToCameraFragment

class MacroCountFragment : Fragment() {

    lateinit var app : MainApp
    private lateinit var userRepo: UserRepo
    var macroCount = MacroCountModel()
    var editMacro = false
    var copyMacro = false
    var currentUserId: Long = 0
    var copiedMacro = MacroCountModel()
    //seekbar data value stores
    var calories: Int = 0
    var protein: Int = 0
    var carbs: Int = 0
    var fat: Int = 0
    //seekbar max and min
    val seekBarMin = 0
    val seekBarMax = 500
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>

    private var _fragBinding: FragmentMacroCountBinding? = null
    private val fragBinding get() = _fragBinding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        userRepo = UserRepo(requireActivity().applicationContext)
        currentUserId = userRepo.userId!!.toLong()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentMacroCountBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_macro_list)

        val args = arguments
        val macroId = MacroCountFragmentArgs.fromBundle(args!!).id
        if (macroId != 0L) {
            macroCount = app.macroCounts.findById(macroId)!!
            fragBinding.macroCountTitle.setText(macroCount.title)
            fragBinding.macroCountDescription.setText(macroCount.description)
            calories = initData(macroCount.calories).toInt()
            protein = initData(macroCount.protein).toInt()
            carbs = initData(macroCount.carbs).toInt()
            fat = initData(macroCount.fat).toInt()
            fragBinding.btnAdd.setText(R.string.save_macroCount)
            editMacro = true

            if (macroCount.image.toString() != "") {
                Picasso.get()
                    .load(macroCount.image)
                    .into(fragBinding.macroCountImage)
            }
        }

        //binding initial values to data views
        fragBinding.caloriesDataView.text = calories.toString()
        fragBinding.proteinDataView.text = protein.toString()
        fragBinding.carbsDataView.text = carbs.toString()
        fragBinding.fatDataView.text = fat.toString()

        // Set the SeekBar range
        fragBinding.calorieSeekBar.min = seekBarMin
        fragBinding.calorieSeekBar.max = seekBarMax
        fragBinding.proteinSeekBar.min = seekBarMin
        fragBinding.proteinSeekBar.max = seekBarMax
        fragBinding.carbsSeekBar.min = seekBarMin
        fragBinding.carbsSeekBar.max = seekBarMax
        fragBinding.fatSeekBar.min = seekBarMin
        fragBinding.fatSeekBar.max = seekBarMax

        //seekbar progresses
        fragBinding.calorieSeekBar.progress = calories
        fragBinding.proteinSeekBar.progress = protein
        fragBinding.carbsSeekBar.progress = carbs
        fragBinding.fatSeekBar.progress = fat

        fragBinding.takePhoto.setOnClickListener {
            val action = MacroCountFragmentDirections.actionMacroCountFragmentToCameraFragment()
            findNavController().navigate(action)

            parentFragmentManager.setFragmentResultListener("photoResult", this) { key, result ->
                val imageUri = result.getParcelable<Uri>("image_uri")

                Timber.i("Got Result $imageUri")
                if (imageUri != null) {
                    macroCount.image = imageUri
                    Timber.i("Got Result macrocount image ${macroCount.image}")
                    Picasso.get()
                        .load(macroCount.image)
                        .into(fragBinding.macroCountImage)
                }
            }
        }

        fragBinding.calorieSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(calorieSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fragBinding.caloriesDataView.text = progress.toString()
                calories = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.proteinSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(proteinSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fragBinding.proteinDataView.text = progress.toString()
                protein = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.carbsSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(carbsSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fragBinding.carbsDataView.text = progress.toString()
                carbs = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fragBinding.fatSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(fatSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fragBinding.fatDataView.text = progress.toString()
                fat = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        registerImagePickerCallback()

        fragBinding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        fragBinding.searchFab.setOnClickListener() {
            val directions = MacroCountFragmentDirections.actionMacroCountFragmentToMacroSearchFragment()
            findNavController().navigate(directions)

            parentFragmentManager.setFragmentResultListener("search_result", this) { key, result ->
                val selectedItem = result.getParcelable<MacroCountModel>("searched_macro")

                Timber.i("Got searchedMacro Result $selectedItem")

                if (selectedItem != null) {
                    Timber.i("returned selectedItem: ${selectedItem}")

                    copyMacro = true

                    macroCount.id = selectedItem.id

                    fragBinding.macroCountTitle.setText(selectedItem.title)
                    fragBinding.macroCountDescription.setText(selectedItem.description)
                    calories = selectedItem.calories.toInt()
                    protein = selectedItem.protein.toInt()
                    carbs = selectedItem.carbs.toInt()
                    fat = selectedItem.fat.toInt()

                    // Update SeekBar progresses and data views
                    fragBinding.calorieSeekBar.progress = calories
                    fragBinding.proteinSeekBar.progress = protein
                    fragBinding.carbsSeekBar.progress = carbs
                    fragBinding.fatSeekBar.progress = fat
                    fragBinding.caloriesDataView.text = calories.toString()
                    fragBinding.proteinDataView.text = protein.toString()
                    fragBinding.carbsDataView.text = carbs.toString()
                    fragBinding.fatDataView.text = fat.toString()

                    copiedMacro = selectedItem.copy()
                }

            }
        }

        fragBinding.btnAdd.setOnClickListener() {
            macroCount.title = fragBinding.macroCountTitle.text.toString()
            macroCount.description = fragBinding.macroCountDescription.text.toString()

            macroCount.calories = calories.toString()
            macroCount.protein = protein.toString()
            macroCount.carbs = carbs.toString()
            macroCount.fat = fat.toString()

            if (currentUserId != null) {
                Timber.i("Before assignment: $macroCount")
                Timber.i("currentUserId at macro add: $currentUserId")
                macroCount.userId = currentUserId
                Timber.i("After assignment: $macroCount")
            }

            val validationChecks = listOf(
                Pair(macroCount.title.isEmpty(), R.string.snackbar_macroCountTitle),
            )

            var validationFailed = false

            for (check in validationChecks) {
                if (check.first) {
                    Snackbar
                        .make(it, check.second, Snackbar.LENGTH_LONG)
                        .show()
                    validationFailed = true
                    break
                }
            }

            if (!validationFailed) {
                if (editMacro) {
                    Timber.i("macroCount edited and saved: $macroCount")
                    app.macroCounts.update(macroCount.copy())
                } else if (copyMacro) {
                    Timber.i("copiedMacro: $copiedMacro")
                    Timber.i("macroCount after copy: $macroCount")
                    if (copiedMacro.equals(macroCount)) {
                        app.days.addMacroId(macroCount.id, macroCount.userId, LocalDate.now())
                        Timber.i("copied macroCount added to today: $macroCount")
                    } else {
                        app.macroCounts.create(macroCount.copy())
                        Timber.i("creating new macroCount from copied and edited macro: $macroCount")
                    }
                } else {
                    app.macroCounts.create(macroCount.copy())
                    Timber.i("macroCount added: $macroCount")
                }
                Timber.i("All user macros: ${app.macroCounts.findByUserId(currentUserId)}")
                Timber.i("LocalDate.now(): ${LocalDate.now()}")
                Timber.i(
                    "Today's macros: ${
                        app.days.findByUserDate(
                            currentUserId,
                            LocalDate.now()
                        )
                    }"
                )
                val directions = MacroCountFragmentDirections.actionMacroCountFragmentToMacroListFragment()
                findNavController().navigate(directions)

            }
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
                            macroCount.image = result.data!!.data!!
                            Timber.i("Got Result macrocount image ${macroCount.image}")
                            Picasso.get()
                                .load(macroCount.image)
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MacroCountFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()

    }
}