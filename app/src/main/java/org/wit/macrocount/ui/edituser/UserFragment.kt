package org.wit.macrocount.ui.edituser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentUserBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import java.time.LocalDate

class UserFragment : Fragment() {

    //lateinit var app: MainApp
    //private var user: UserModel? = null
    private lateinit var userRepo: UserRepo
    private lateinit var editUserViewModel: EditUserViewModel
    //var signup = false
    private var _fragBinding: FragmentUserBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("starting user profile fragment")
        //app = activity?.application as MainApp
        userRepo = UserRepo(requireActivity().applicationContext)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentUserBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_user)

        editUserViewModel = ViewModelProvider(requireActivity()).get(EditUserViewModel::class.java)
        if (userRepo.userId != null) {
            editUserViewModel.getUser(userRepo.userId!!.toLong())
        }
        val user = editUserViewModel.observableUser.value
        val userVm = editUserViewModel
//        editUserViewModel.observableUser.observe(viewLifecycleOwner, Observer { render() })
//

        //Weight goal radio buttons

        fragBinding.goalRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.goalRadioButtonOption1 -> {
                    userVm.setGoal("Lose")
                }
                R.id.goalRadioButtonOption2 -> {
                    userVm.setGoal("Gain")
                }
            }
        }

        val preSelectedGoalRadio = when (user?.goal) {
            "Lose" -> R.id.goalRadioButtonOption1
            "Gain" -> R.id.goalRadioButtonOption2
            else -> -1
        }

        fragBinding.goalRadioGroup.check(preSelectedGoalRadio)

        //Gender radio buttons

        val radioGroup = fragBinding.genderRadioGroup

        fragBinding.genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radioButtonOption1 -> {
                    userVm.setGender("male")
                }
                R.id.radioButtonOption2 -> {
                    userVm.setGender("female")
                }
            }
        }

        val preSelectedRadio = when (user?.gender) {
            "male" -> R.id.radioButtonOption1
            "female" -> R.id.radioButtonOption2
            else -> -1
        }

        radioGroup.check(preSelectedRadio)

        // Age number picker

        val numberPickerAge = fragBinding.numberPickerAge
        numberPickerAge.minValue = 0
        numberPickerAge.maxValue = 120
        numberPickerAge.value = user?.age?.toInt() ?: 18

        numberPickerAge.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            userVm.setAge(newVal.toString())
        }

        // Height number picker

        val numberPickerHeight = fragBinding.numberPickerHeight
        numberPickerHeight.minValue = 0
        numberPickerHeight.maxValue = 250
        numberPickerHeight.value = user?.height?.toInt() ?: 170

        numberPickerHeight.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            userVm.setHeight(newVal.toString())
        }

        // Weight number picker

        val numberPickerWeight = fragBinding.numberPickerWeight
        numberPickerWeight.minValue = 0
        numberPickerWeight.maxValue = 150
        numberPickerWeight.value = user?.weight?.toInt() ?: 70

        numberPickerWeight.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            userVm.setWeight(newVal.toString())
        }

        var day: String = ""
        var month: String = ""
        var year: String = ""

        var dob = ""
        dob = if (user != null && user?.dob != "") {
            user!!.dob
        } else {
            "01/01/2000"
        }
        var presetDob = dob.split("/")

        Timber.i("split dob: $presetDob")
        Timber.i("split dob 0: ${presetDob[0]}")
        Timber.i("split dob 1: ${presetDob[1]}")
        Timber.i("split dob 2: ${presetDob[2]}")


        day = presetDob[0]
        month = presetDob[1]
        year = presetDob[2]


        val numberPickerDay = fragBinding.numberPickerDay
        numberPickerDay.minValue = 1
        numberPickerDay.maxValue = 31
        numberPickerDay.value = presetDob[0].toInt()
        numberPickerDay.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            day = newVal.toString()
        }

        val numberPickerMonth = fragBinding.numberPickerMonth
        numberPickerMonth.minValue = 1
        numberPickerMonth.maxValue = 12
        numberPickerMonth.value = presetDob[1].toInt()
        numberPickerMonth.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            month = newVal.toString()
        }

        val numberPickerYear = fragBinding.numberPickerYear
        numberPickerYear.minValue = 1920
        numberPickerYear.maxValue = LocalDate.now().year
        numberPickerYear.value = presetDob[2]?.toInt() ?: 2000
        numberPickerYear.setOnValueChangedListener{ picker, oldVal, newVal ->
            Timber.i("{newVal}")
            year = newVal.toString()
        }

        fragBinding.btnSave.setOnClickListener() {
            userVm.setName(fragBinding.userName.text.toString())
            val userDob = day.toString() + "/" + month.toString() + "/" + year.toString()
            userVm.setDob(userDob)

            userVm.saveUser()

            Timber.i("userProfile saved: $user")

            fragBinding.btnSave.text = getString(R.string.btn_saved_user)
            fragBinding.btnSave.setBackgroundResource(R.color.color_green)
            //app.users.update(user!!.copy())

        }

        return root
    }

//    private fun render() {
//        val vmUser = editUserViewModel.observableUser.value
//        if (vmUser != null) {
//            fragBinding.uservm = editUserViewModel
//        } else {
//            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
//        }
//    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
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