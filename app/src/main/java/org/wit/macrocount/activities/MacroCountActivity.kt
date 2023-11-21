package org.wit.macrocount.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.wit.macrocount.R
import org.wit.macrocount.databinding.ActivityMacrocountBinding
import org.wit.macrocount.helpers.DataValUtil
import org.wit.macrocount.main.MainApp
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber.Forest.i
import org.wit.macrocount.showImagePicker
import com.squareup.picasso.Picasso
import org.wit.macrocount.models.UserRepo
import java.time.LocalDate
import java.io.File


class MacroCountActivity : AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityMacrocountBinding
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMacrocountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        app = application as MainApp

        i("MacroCount started..")

        //seekbar max and min
        val seekBarMin = 0
        val seekBarMax = 500

        userRepo = UserRepo(applicationContext)
        currentUserId = userRepo.userId!!.toLong()


        if (intent.hasExtra("macrocount_edit")) {
            editMacro = true
            macroCount = intent.extras?.getParcelable("macrocount_edit")!!

            binding.macroCountTitle.setText(macroCount.title)
            binding.macroCountDescription.setText(macroCount.description)
            calories = initData(macroCount.calories).toInt()
            protein = initData(macroCount.protein).toInt()
            carbs = initData(macroCount.carbs).toInt()
            fat = initData(macroCount.fat).toInt()

            binding.btnAdd.setText(R.string.save_macroCount)

            if (macroCount.image.toString() != "") {
                Picasso.get()
                    .load(macroCount.image)
                    .into(binding.macroCountImage)
            }
        }

        //seekbar viewers
        val calorieSeekBar = findViewById<SeekBar>(R.id.calorieSeekBar)
        val proteinSeekBar = findViewById<SeekBar>(R.id.proteinSeekBar)
        val carbsSeekBar = findViewById<SeekBar>(R.id.carbsSeekBar)
        val fatSeekBar = findViewById<SeekBar>(R.id.fatSeekBar)

        //views that contain data values
        val caloriesDataView = findViewById<TextView>(R.id.caloriesDataView)
        val proteinDataView = findViewById<TextView>(R.id.proteinDataView)
        val carbsDataView = findViewById<TextView>(R.id.carbsDataView)
        val fatDataView = findViewById<TextView>(R.id.fatDataView)


        //binding initial values to data views
        caloriesDataView.text = calories.toString()
        proteinDataView.text = protein.toString()
        carbsDataView.text = carbs.toString()
        fatDataView.text = fat.toString()

        // Set the SeekBar range
        calorieSeekBar.min = seekBarMin
        calorieSeekBar.max = seekBarMax
        proteinSeekBar.min = seekBarMin
        proteinSeekBar.max = seekBarMax
        carbsSeekBar.min = seekBarMin
        carbsSeekBar.max = seekBarMax
        fatSeekBar.min = seekBarMin
        fatSeekBar.max = seekBarMax

        //seekbar progresses
        calorieSeekBar.progress = calories
        proteinSeekBar.progress = protein
        carbsSeekBar.progress = carbs
        fatSeekBar.progress = fat

        binding.takePhoto.setOnClickListener() {
            val launcherIntent = Intent(this, CameraActivity::class.java)
            getPhotoResult.launch(launcherIntent)
        }

        binding.btnAdd.setOnClickListener() {
            macroCount.title = binding.macroCountTitle.text.toString()
            macroCount.description = binding.macroCountDescription.text.toString()

            macroCount.calories = calories.toString()
            macroCount.protein = protein.toString()
            macroCount.carbs = carbs.toString()
            macroCount.fat = fat.toString()

            if (currentUserId != null) {
                i("Before assignment: $macroCount")
                i("currentUserId at macro add: $currentUserId")
                macroCount.userId = currentUserId
                i("After assignment: $macroCount")
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
                    i("macroCount edited and saved: $macroCount")
                    app.macroCounts.update(macroCount.copy())
                } else if (copyMacro) {
                    i("copiedMacro: $copiedMacro")
                    i("macroCount after copy: $macroCount")
                    if (copiedMacro.equals(macroCount)) {
                        app.days.addMacroId(macroCount.id, macroCount.userId, LocalDate.now())
                        i("copied macroCount added to today: $macroCount")
                    } else {
                        app.macroCounts.create(macroCount.copy())
                        i("creating new macroCount from copied and edited macro: $macroCount")
                    }
                } else {
                    app.macroCounts.create(macroCount.copy())
                    i("macroCount added: $macroCount")
                }
                i("All user macros: ${app.macroCounts.findByUserId(currentUserId)}")
                i("LocalDate.now(): ${LocalDate.now()}")
                i("Today's macros: ${app.days.findByUserDate(currentUserId, LocalDate.now())}")

                setResult(RESULT_OK)
                finish()
            }
        }

        calorieSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(calorieSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                caloriesDataView.text = progress.toString()
                calories = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        proteinSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(proteinSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                proteinDataView.text = progress.toString()
                protein = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        carbsSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(carbsSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                carbsDataView.text = progress.toString()
                carbs = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        fatSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(fatSeekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fatDataView.text = progress.toString()
                fat = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        registerImagePickerCallback()

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)
        }

    }

    fun initData(value: String): String {
        return if (value.isNotEmpty()) value else "0"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_macrocount, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_search -> {
                val launcherIntent = Intent(this, MacroCountSearchActivity::class.java)
                launcherIntent.putExtra("search", intent)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Result ${result.data!!.data!!}")
                            macroCount.image = result.data!!.data!!
                            i("Got Result macrocount image ${macroCount.image}")
                            Picasso.get()
                                .load(macroCount.image)
                                .into(binding.macroCountImage)
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data != null) {
                    val selectedItem = data.getParcelableExtra<MacroCountModel>("macrocount_copy")
                    if (selectedItem != null) {
                        i("returned selectedItem: ${selectedItem}")

                        copyMacro = true

                        macroCount.id = selectedItem.id

                        binding.macroCountTitle.setText(selectedItem.title)
                        binding.macroCountDescription.setText(selectedItem.description)
                        calories = selectedItem.calories.toInt()
                        protein = selectedItem.protein.toInt()
                        carbs = selectedItem.carbs.toInt()
                        fat = selectedItem.fat.toInt()

                        // Update SeekBar progresses and data views
                        binding.calorieSeekBar.progress = calories
                        binding.proteinSeekBar.progress = protein
                        binding.carbsSeekBar.progress = carbs
                        binding.fatSeekBar.progress = fat
                        binding.caloriesDataView.text = calories.toString()
                        binding.proteinDataView.text = protein.toString()
                        binding.carbsDataView.text = carbs.toString()
                        binding.fatDataView.text = fat.toString()

                        copiedMacro = selectedItem.copy()
                    }
                }

            }
        }

    private val getPhotoResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.getParcelableExtra<Uri>("image_uri")

                    i("Got Result $imageUri")
                    if (imageUri != null) {
                        macroCount.image = imageUri
                    }
                        i("Got Result macrocount image ${macroCount.image}")
                        Picasso.get()
                            .load(macroCount.image)
                            .into(binding.macroCountImage)

                }
            }
}
