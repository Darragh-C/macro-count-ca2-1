package org.wit.macrocount.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.wit.macrocount.R
import org.wit.macrocount.databinding.ActivityChartsBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import org.wit.macrocount.helpers.calcBmr
import org.wit.macrocount.helpers.calcProtein
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber
import kotlin.math.roundToInt

class MacroChartsActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityChartsBinding
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChartsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Macro data"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        app = application as MainApp
        userRepo = UserRepo(applicationContext)

        val currentUserId = userRepo.userId
        Timber.i("currentUserId at charts: $currentUserId")
        if (currentUserId != null) {
            user = app.users.findById(currentUserId.toLong())
        }
        Timber.i("user at charts: $user")

        userMacros = app.macroCounts.findByUserId(user!!.id)
        Timber.i("userMacros: $userMacros")

        if (user != null && userMacros != null) {
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
            binding.caloriesProgressFraction.text = calorieFraction
            var proteinFraction = "$dailyProtein/$proteinGoal"
            binding.proteinProgressFraction.text = proteinFraction

            caloriesProgress = ((dailyCalories.toDouble() / calorieGoal.toDouble()) * 100).roundToInt()
            Timber.i("caloriesProgress: $caloriesProgress")
            proteinProgress = ((dailyProtein.toDouble() / proteinGoal.toDouble()) * 100).roundToInt()
            Timber.i("caloriesProgress: $proteinProgress")

        }


        caloriesProgressBar = binding.caloriesProgressBar
        caloriesProgressBar.progress = caloriesProgress

        proteinProgressBar = binding.proteinProgressBar
        proteinProgressBar.progress = proteinProgress

        //pie chart

        pieChart = binding.macroPieChart

        val macroTotals: ArrayList<PieEntry> = ArrayList()

        macroTotals.add(PieEntry((userMacros!!.sumOf { it.protein.toInt() }).toFloat(), "Protein"))
        macroTotals.add(PieEntry((userMacros!!.sumOf { it.carbs.toInt() }).toFloat(), "Carbs"))
        macroTotals.add(PieEntry((userMacros!!.sumOf { it.fat.toInt() }).toFloat(), "Fat"))

        val pieDataSet = PieDataSet(macroTotals, "Macro proportions")

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 255)

        pieDataSet.valueTextSize = 15f

        pieDataSet.valueTextColor = Color.BLACK

        val pieData = PieData(pieDataSet)

        pieChart.data = pieData

        pieChart.centerText = "Macro proportions"

        pieChart.animateY(2000)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Timber.i("Rendering menu")
        menuInflater.inflate(R.menu.menu_macrocount, menu)
        val searchItem = menu.findItem(R.id.item_search)
        searchItem.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

}
