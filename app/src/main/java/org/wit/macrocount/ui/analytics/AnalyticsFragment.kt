package org.wit.macrocount.ui.analytics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentAnalyticsBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserModel

import timber.log.Timber
import org.wit.macrocount.composables.MPAndroidChartComponent
import org.wit.macrocount.composables.ProgressBar
import org.wit.macrocount.utils.createLoader
import org.wit.macrocount.utils.hideLoader
import org.wit.macrocount.utils.showLoader


class AnalyticsFragment : Fragment() {

//    lateinit var app : MainApp
//    private var currentUserId: Long = 0L
//    private lateinit var userRepo: UserRepo
    private var user: UserModel? = null
    private var userMacros: List<MacroCountModel>? = null
    lateinit var pieChart: PieChart
    val pieEntres: ArrayList<PieEntry> = ArrayList()
    private var _fragBinding: FragmentAnalyticsBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var analyticsViewModel: AnalyticsViewModel
    private var render = false
    lateinit var loader : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("user at charts: $user")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_analytics)

        analyticsViewModel = ViewModelProvider(requireActivity()).get(AnalyticsViewModel::class.java)

        loader = createLoader(requireActivity())
        showLoader(loader,"Downloading macros")

        analyticsViewModel.observableUser.observe(viewLifecycleOwner, Observer {
            analyticsViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {
                if (analyticsViewModel.observableUser.value != null && analyticsViewModel.observableMacroList.value?.isNotEmpty() == true) {
                    analyticsViewModel.runCalculations()
                    render()
                    hideLoader(loader)
                }
            })
        })

        return ComposeView(requireContext()).apply {

            analyticsViewModel.observableCalculationsStatus.observe(viewLifecycleOwner, Observer {
                if (analyticsViewModel.observableUser.value != null && analyticsViewModel.observableMacroList.value?.isNotEmpty() == true) {
                    setContent {
                        Timber.i("observableCalorieTotal: ${analyticsViewModel.observableCalorieTotal.value}")
                        Timber.i("observableProteinTotal: ${analyticsViewModel.observableProteinTotal.value}")
                        renderUI()
                    }
                }
            })

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
    }

    @Composable
    fun renderUI() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressBar(title = "Calories", progress = analyticsViewModel.observableCalorieTotal.value!!, total = analyticsViewModel.observableCalorieGoal.value!!)
                ProgressBar(title = "Protein", progress = analyticsViewModel.observableProteinTotal.value!!, total = analyticsViewModel.observableProteinGoal.value!!)
                MPAndroidChartComponent(pieEntres, "Macro proportions")
            }
        }
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
                return NavigationUI.onNavDestinationSelected(
                    menuItem,
                    requireView().findNavController()
                )
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun render() {

        //fragBinding.analyticsvm = analyticsViewModel
        addPieEntries()
        render = true
        //pieChartSetup()
    }

    fun addPieEntries() {
        if (!analyticsViewModel.observableMacroList.value.isNullOrEmpty()) {

            pieEntres.add(
                PieEntry(
                    analyticsViewModel.observableProteinTotal.value?.toFloat()!!,
                    "Protein"
                )
            )
            pieEntres.add(
                PieEntry(
                    analyticsViewModel.observableCarbsTotal.value?.toFloat()!!,
                    "Carbs"
                )
            )
            pieEntres.add(
                PieEntry(
                    analyticsViewModel.observableFatTotal.value?.toFloat()!!,
                    "Fat"
                )
            )

            Timber.i("macroTotals: $pieEntres")
        }  else {
            Timber.i("userMacros is null or empty: $userMacros")
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalyticsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}