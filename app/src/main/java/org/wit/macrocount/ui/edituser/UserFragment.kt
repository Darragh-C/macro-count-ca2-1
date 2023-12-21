package org.wit.macrocount.ui.edituser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.R
import org.wit.macrocount.databinding.FragmentUserBinding
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo
import timber.log.Timber
import java.time.LocalDate

class UserFragment : Fragment() {

    private lateinit var editUserViewModel: EditUserViewModel
    //var signup = false
    private var _fragBinding: FragmentUserBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("starting user profile fragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentUserBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_user)

        editUserViewModel = ViewModelProvider(requireActivity()).get(EditUserViewModel::class.java)

        editUserViewModel.observableUser.observe(viewLifecycleOwner, Observer { render() })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editUserViewModel.observableUser.observe(viewLifecycleOwner, Observer {
            //Weight goal radio buttons

            fragBinding.goalRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    R.id.goalRadioButtonOption1 -> {
                        editUserViewModel.setGoal("Lose")
                    }
                    R.id.goalRadioButtonOption2 -> {
                        editUserViewModel.setGoal("Gain")
                    }
                }
            }

            val preSelectedGoalRadio = when (editUserViewModel.observableUser.value?.goal) {
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
                        editUserViewModel.setGender("male")
                    }
                    R.id.radioButtonOption2 -> {
                        editUserViewModel.setGender("female")
                    }
                }
            }

            val preSelectedRadio = when (editUserViewModel.observableUser.value?.gender) {
                "male" -> R.id.radioButtonOption1
                "female" -> R.id.radioButtonOption2
                else -> -1
            }

            radioGroup.check(preSelectedRadio)

            // Age number picker

            val numberPickerAge = fragBinding.numberPickerAge
            numberPickerAge.minValue = 0
            numberPickerAge.maxValue = 120
            numberPickerAge.value = editUserViewModel.observableUser.value?.age?.toInt() ?: 18

            numberPickerAge.setOnValueChangedListener{ picker, oldVal, newVal ->
                Timber.i("{newVal}")
                editUserViewModel.setAge(newVal.toString())
            }

            // Height number picker

            val numberPickerHeight = fragBinding.numberPickerHeight
            numberPickerHeight.minValue = 0
            numberPickerHeight.maxValue = 250
            numberPickerHeight.value = editUserViewModel.observableUser.value?.height?.toInt() ?: 170

            numberPickerHeight.setOnValueChangedListener{ picker, oldVal, newVal ->
                Timber.i("{newVal}")
                editUserViewModel.setHeight(newVal.toString())
            }

            // Weight number picker

            val numberPickerWeight = fragBinding.numberPickerWeight
            numberPickerWeight.minValue = 0
            numberPickerWeight.maxValue = 150
            numberPickerWeight.value = editUserViewModel.observableUser.value?.weight?.toInt() ?: 70

            numberPickerWeight.setOnValueChangedListener{ picker, oldVal, newVal ->
                Timber.i("{newVal}")
                editUserViewModel.setWeight(newVal.toString())
            }

            var day: String = ""
            var month: String = ""
            var year: String = ""

            var dob = ""
            dob = if (editUserViewModel.observableUser.value != null && editUserViewModel.observableUser.value?.dob != "") {
                editUserViewModel.observableUser.value!!.dob
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
            numberPickerYear.value = presetDob[2].toInt() ?: 2000
            numberPickerYear.setOnValueChangedListener{ picker, oldVal, newVal ->
                Timber.i("{newVal}")
                year = newVal.toString()
            }

            fragBinding.btnSave.setOnClickListener() {
                editUserViewModel.setName(fragBinding.userName.text.toString())
                val userDob = day.toString() + "/" + month.toString() + "/" + year.toString()
                editUserViewModel.setDob(userDob)

                editUserViewModel.saveUser()

                Timber.i("userProfile saved")

                fragBinding.btnSave.text = getString(R.string.btn_saved_user)
                fragBinding.btnSave.setBackgroundResource(R.color.color_green)
                //app.users.update(user!!.copy())
                val action = UserFragmentDirections.actionUserFragmentToUserDetailFragment()
                findNavController().navigate(action)
            }
        })
    }

    private fun render() {
        if (editUserViewModel.observableUser.value != null) {
            fragBinding.uservm = editUserViewModel

        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }



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