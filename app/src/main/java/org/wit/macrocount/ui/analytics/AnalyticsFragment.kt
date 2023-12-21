package org.wit.macrocount.ui.analytics

import android.graphics.Color
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
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.utils.hideLoader
import timber.log.Timber


class AnalyticsFragment : Fragment() {

//    lateinit var app : MainApp
//    private var currentUserId: Long = 0L
//    private lateinit var userRepo: UserRepo
    private var user: UserModel? = null
    private var userMacros: List<MacroCountModel>? = null
    lateinit var pieChart: PieChart
    private var _fragBinding: FragmentAnalyticsBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var analyticsViewModel: AnalyticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("user at charts: $user")
        analyticsViewModel = ViewModelProvider(requireActivity()).get(AnalyticsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_analytics)

        analyticsViewModel.observableUser.observe(viewLifecycleOwner, Observer {
            analyticsViewModel.observableMacroList.observe(viewLifecycleOwner, Observer {
                if (analyticsViewModel.observableUser.value != null && analyticsViewModel.observableMacroList.value?.isNotEmpty() == true) {
                    analyticsViewModel.runCalculations()
                    render()
                }
            })
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
    }

    fun pieChartSetup() {
        //pie chart
        Timber.i("setting up pie chart")

        pieChart = fragBinding.macroPieChart

        val pieEntres: ArrayList<PieEntry> = ArrayList()

        //totaling up the macros

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

            val pieDataSet = PieDataSet(pieEntres, "Macro proportions")

            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)
            pieDataSet.valueTextSize = 15f
            pieDataSet.valueTextColor = Color.BLACK
            val pieData = PieData(pieDataSet)
            pieChart.data = pieData
            pieChart.centerText = "Macro proportions"
            pieChart.animateY(2000)
        }  else {
            Timber.i("userMacros is null or empty: $userMacros")
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

        fragBinding.analyticsvm = analyticsViewModel
        pieChartSetup()
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