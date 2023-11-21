package org.wit.macrocount.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import org.wit.macrocount.R
import org.wit.macrocount.databinding.ActivityProfileBinding
import org.wit.macrocount.main.MainApp

import org.wit.macrocount.models.UserModel
import org.wit.macrocount.models.UserRepo

import timber.log.Timber
import timber.log.Timber.Forest.i
import java.time.LocalDate

class UserProfileActivity : AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityProfileBinding
    private lateinit var userRepo: UserRepo
    private var user: UserModel? = null


    var signup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Profile"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        app = application as MainApp
        userRepo = UserRepo(applicationContext)


        val currentUserId = userRepo.userId
        if (currentUserId != null) {
            user = app.users.findById(currentUserId.toLong())
            binding.userName.setText(user!!.name)
        }

        Timber.i("on create user: $user")

        if (intent.hasExtra("user_signup")) {
            signup = true
            //user = intent.extras?.getParcelable("user_signup")!!
            user!!.weight = "75"
            user!!.height  = "150"
            user!!.age  = "18"
            user!!.dob = "1/1/1990"
            i("user intent received at profile activity: $user")
        }

        //Weight goal radio buttons

        val goalRadioGroup = findViewById<RadioGroup>(R.id.goalRadioGroup)

        goalRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.goalRadioButtonOption1 -> {
                    user?.goal = "Lose"
                }
                R.id.goalRadioButtonOption2 -> {
                    user?.goal = "Gain"
                }
            }
        }

        val preSelectedGoalRadio = when (user?.goal) {
            "Lose" -> R.id.goalRadioButtonOption1
            "Gain" -> R.id.goalRadioButtonOption2
            else -> -1
        }

        goalRadioGroup.check(preSelectedGoalRadio)

        //Gender radio buttons

        val radioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radioButtonOption1 -> {
                    user?.gender = "male"
                }
                R.id.radioButtonOption2 -> {
                    user?.gender = "female"
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

        val numberPickerAge = findViewById<NumberPicker>(R.id.numberPickerAge)
        numberPickerAge.minValue = 0
        numberPickerAge.maxValue = 120
        numberPickerAge.value = user?.age?.toInt()!!

        numberPickerAge.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            user!!.age = newVal.toString()
        }

        // Height number picker

        val numberPickerHeight = findViewById<NumberPicker>(R.id.numberPickerHeight)
        numberPickerHeight.minValue = 0
        numberPickerHeight.maxValue = 250
        numberPickerHeight.value = user?.height?.toInt()!!

        numberPickerHeight.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            user!!.height = newVal.toString()
        }

        // Weight number picker

        val numberPickerWeight = findViewById<NumberPicker>(R.id.numberPickerWeight)
        numberPickerWeight.minValue = 0
        numberPickerWeight.maxValue = 150
        numberPickerWeight.value = user!!.weight.toInt()

        numberPickerWeight.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            user!!.weight = newVal.toString()
        }

        var day: String = ""
        var month: String = ""
        var year: String = ""

        var presetDob = user!!.dob.split("/")
        Timber.i("split dob: $presetDob")
        Timber.i("split dob 0: $presetDob[0]")
        Timber.i("split dob 1: $presetDob[1]")
        Timber.i("split dob 2: $presetDob[2]")

        if (presetDob != null) {
            day = presetDob[0]
            month = presetDob[1]
            year = presetDob[2]
        }

        val numberPickerDay = findViewById<NumberPicker>(R.id.numberPickerDay)
        numberPickerDay.minValue = 1
        numberPickerDay.maxValue = 31
        numberPickerDay.value = presetDob[0].toInt()
        numberPickerDay.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            day = newVal.toString()
        }

        val numberPickerMonth = findViewById<NumberPicker>(R.id.numberPickerMonth)
        numberPickerMonth.minValue = 1
        numberPickerMonth.maxValue = 12
        numberPickerMonth.value = presetDob[1].toInt()
        numberPickerMonth.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            month = newVal.toString()
        }

        val numberPickerYear = findViewById<NumberPicker>(R.id.numberPickerYear)
        numberPickerYear.minValue = 1920
        numberPickerYear.maxValue = LocalDate.now().year
        numberPickerYear.value = presetDob[2].toInt()
        numberPickerYear.setOnValueChangedListener{ picker, oldVal, newVal ->
            i("{newVal}")
            year = newVal.toString()
        }

        binding.btnSave.setOnClickListener() {
            user!!.name = binding.userName.text.toString()
            user!!.dob = day.toString() + "/" + month.toString() + "/" + year.toString()

            Timber.i("userProfile saved: $user")
            app.users.update(user!!.copy())

            if (signup) {
                val intent = Intent(this, MacroCountListActivity::class.java)
                startActivity(intent)
            } else {
                setResult(RESULT_OK)
                finish()
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_macrocount, menu)
        return super.onCreateOptionsMenu(menu)
    }

}