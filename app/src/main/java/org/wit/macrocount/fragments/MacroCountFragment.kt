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
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.wit.macrocount.R
import org.wit.macrocount.databinding.ActivityMacrocountBinding
import org.wit.macrocount.databinding.FragmentMacroCountBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber

class MacroCountFragment : Fragment() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityMacrocountBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var userRepo: UserRepo
    var macroCount = MacroCountModel()
    var editMacro = false
    var copyMacro = false
    var currentUserId: Long = 0
    var copiedMacro = MacroCountModel()

    private var _fragBinding: FragmentMacroCountBinding? = null
    private val fragBinding get() = _fragBinding!!

    //seekbar data value stores
    var calories: Int = 0
    var protein: Int = 0
    var carbs: Int = 0
    var fat: Int = 0
    //seekbar max and min
    val seekBarMin = 0
    val seekBarMax = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentMacroCountBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_macro_list)

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

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Timber.i("onCreateOptionsMenu called")
        inflater.inflate(R.menu.menu_macrocount, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.i("onOptionsItemSelected called")
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MacroCountFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }


}