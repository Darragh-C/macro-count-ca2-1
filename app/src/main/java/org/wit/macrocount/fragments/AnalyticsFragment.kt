package org.wit.macrocount.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentAnalyticsBinding
import org.wit.macrocount.helpers.calcBmr
import org.wit.macrocount.helpers.calcProtein
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.roundToInt


class AnalyticsFragment : Fragment() {

    lateinit var app : MainApp
    private lateinit var caloriesProgressBar: ProgressBar
    private lateinit var proteinProgressBar: ProgressBar
    private lateinit var userRepo: UserRepo
    private var user: UserModel? = null
    private var calorieGoal: Int = 0
    private var proteinGoal: Int = 0
    private var dailyCalories: Int = 0
    private var dailyProtein: Int = 0
    private var userMacros: List<MacroCountModel>? = null
    private var caloriesProgress: Int = 0
    private var proteinProgress: Int = 0
    lateinit var pieChart: PieChart

    private var _fragBinding: FragmentAnalyticsBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        userRepo = UserRepo(requireActivity().applicationContext)
        val currentUserId = userRepo.userId
        if (currentUserId != null) {
            user = app.users.findById(currentUserId.toLong())
        }
        Timber.i("user at charts: $user")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_analytics)

        //userMacros = app.macroCounts.findByUserId(user!!.id)
        var userMacrosToday = app.days.findByUserDate(user!!.id, LocalDate.now())
        var userMacros = listOf<MacroCountModel>()
        if (userMacrosToday != null) {
            userMacros = app.macroCounts.findByIds(userMacrosToday.userMacroIds) as List<MacroCountModel>
        }

        Timber.i("userMacros: $userMacros")

        if (user != null && userMacros.isNotEmpty()) {
            calorieGoal = calcBmr(
                user!!.weight.toInt(),
                user!!.height.toInt(),
                user!!.age.toInt(),
                user!!.goal
            )

            proteinGoal = calcProtein(
                user!!.weight.toInt(),
                user!!.goal
            )

            dailyCalories = userMacros!!.sumOf { it.calories.toInt() }
            Timber.i("dailyCalories: $dailyCalories")

            dailyProtein = userMacros!!.sumOf { it.protein.toInt() }
            Timber.i("dailyCalories: $dailyProtein")

            var calorieFraction = "$dailyCalories/$calorieGoal"
            fragBinding.caloriesProgressFraction.text = calorieFraction
            var proteinFraction = "$dailyProtein/$proteinGoal"
            fragBinding.proteinProgressFraction.text = proteinFraction

            caloriesProgress = ((dailyCalories.toDouble() / calorieGoal.toDouble()) * 100).roundToInt()
            Timber.i("caloriesProgress: $caloriesProgress")
            proteinProgress = ((dailyProtein.toDouble() / proteinGoal.toDouble()) * 100).roundToInt()
            Timber.i("caloriesProgress: $proteinProgress")

        }


        caloriesProgressBar = fragBinding.caloriesProgressBar
        caloriesProgressBar.progress = caloriesProgress

        proteinProgressBar = fragBinding.proteinProgressBar
        proteinProgressBar.progress = proteinProgress

        //pie chart

        pieChart = fragBinding.macroPieChart

        val macroTotals: ArrayList<PieEntry> = ArrayList()

        //totaling up the macros

        if (userMacros != null && userMacros!!.isNotEmpty()) {
            val totalProtein = userMacros?.sumBy { it.protein.toInt() }
            val totalCarbs = userMacros?.sumBy { it.carbs.toInt() }
            val totalFat = userMacros?.sumBy { it.fat.toInt() }
            Timber.i("totalProtein: $totalProtein")
            Timber.i("totalCarbs: $totalCarbs")
            Timber.i("totalFat: $totalFat")

            macroTotals.add(PieEntry(totalProtein!!.toFloat(), "Protein"))
            macroTotals.add(PieEntry(totalCarbs!!.toFloat(), "Carbs"))
            macroTotals.add(PieEntry(totalFat!!.toFloat(), "Fat"))

            Timber.i("macroTotals: $macroTotals")

            val pieDataSet = PieDataSet(macroTotals, "Macro proportions")

            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)

            pieDataSet.valueTextSize = 15f

            pieDataSet.valueTextColor = Color.BLACK

            val pieData = PieData(pieDataSet)

            pieChart.data = pieData

            pieChart.centerText = "Macro proportions"

            pieChart.animateY(2000)
        } else {
            Timber.i("userMacros is null or empty: $userMacros")

        }

        return root

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