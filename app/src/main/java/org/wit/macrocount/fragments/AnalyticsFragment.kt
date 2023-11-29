package org.wit.macrocount.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.github.mikephil.charting.charts.PieChart
import org.wit.macrocount.R
import org.wit.macrocount.databinding.ActivityChartsBinding
import org.wit.macrocount.databinding.FragmentAnalyticsBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo


class AnalyticsFragment : Fragment() {

    lateinit var app : MainApp
    private lateinit var caloriesProgressBar: ProgressBar
    private lateinit var proteinProgressBar: ProgressBar
//    private lateinit var userRepo: UserRepo
//    private var user: UserModel? = null
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
//        userRepo = UserRepo(requireActivity().applicationContext)
//        val currentUserId = userRepo.userId
//        if (currentUserId != null) {
//            user = app.users.findById(currentUserId.toLong())
//        }
        userMacros = app.macroCounts.findAll()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_analytics)


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