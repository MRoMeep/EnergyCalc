package com.example.energycalc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var shareText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerDevice = findViewById<Spinner>(R.id.spinnerDevice)
        val layoutCustomPower = findViewById<View>(R.id.layoutCustomPower)
        val editCustomPower = findViewById<TextInputEditText>(R.id.editCustomPower)
        val seekBarHours = findViewById<SeekBar>(R.id.seekBarHours)
        val seekBarDays = findViewById<SeekBar>(R.id.seekBarDays)
        val textHoursLabel = findViewById<TextView>(R.id.textHoursLabel)
        val textDaysLabel = findViewById<TextView>(R.id.textDaysLabel)
        val editPrice = findViewById<TextInputEditText>(R.id.editPrice)
        val checkNightTariff = findViewById<CheckBox>(R.id.checkNightTariff)
        val radioGroupCurrency = findViewById<RadioGroup>(R.id.radioGroupCurrency)
        val buttonCalculate = findViewById<Button>(R.id.buttonCalculate)
        val textResults = findViewById<TextView>(R.id.textResults)
        val textMonthlyMain = findViewById<TextView>(R.id.textMonthlyMain)
        val cardResults = findViewById<MaterialCardView>(R.id.cardResults)
        val buttonShare = findViewById<Button>(R.id.buttonShare)
        val scrollView = findViewById<ScrollView>(R.id.main)

        val devices = arrayOf(
            "Pralka (700 W)", "Lodówka (150 W)", "Telewizor (120 W)",
            "Komputer PC (300 W)", "Klimatyzator (1500 W)", "Żarówka LED (10 W)",
            "Czajnik elektryczny (2000 W)", "Inne (własna moc)"
        )
        val powerValues = intArrayOf(700, 150, 120, 300, 1500, 10, 2000, 0)

        spinnerDevice.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, devices)

        spinnerDevice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 7) {
                    layoutCustomPower.animateVisibility(true, 350)
                    editCustomPower.requestFocus()
                } else {
                    layoutCustomPower.animateVisibility(false, 300)
                    editCustomPower.text?.clear()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        seekBarHours.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p: Int, p2: Boolean) {
                if (seekBarDays.progress >= 4 && p < 2) {
                    seekBarHours.progress = 2

                }
                textHoursLabel.text = "Użytkowanie: ${seekBarHours.progress} /dzień"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        seekBarDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p: Int, p2: Boolean) {
                textDaysLabel.text = "Okres: $p dni"
                if (p >= 4 && seekBarHours.progress < 2) {
                    seekBarHours.progress = 2
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        buttonCalculate.setOnClickListener {
            val selectedPos = spinnerDevice.selectedItemPosition
            val power = if (selectedPos == 7) editCustomPower.text.toString().toIntOrNull() ?: 0 else powerValues[selectedPos]
            val hours = seekBarHours.progress
            val days = seekBarDays.progress
            val basePrice = editPrice.text.toString().toDoubleOrNull() ?: 0.0

            val dailyKwh = (power / 1000.0) * hours
            val powerKw = power / 1000.0

            val dailyCostPln = if (checkNightTariff.isChecked) {
                val nightHours = if (hours > 8) 8 else hours
                val dayHours = hours - nightHours
                (nightHours * powerKw * basePrice * 0.7) + (dayHours * powerKw * basePrice)
            } else dailyKwh * basePrice

            val monthlyCostPln = dailyCostPln * days
            val yearlyCostPln = dailyCostPln * 365

            val rate = when (radioGroupCurrency.checkedRadioButtonId) {
                R.id.radioEUR -> 4.25
                R.id.radioUSD -> 3.95
                else -> 1.0
            }
            val currency = when (radioGroupCurrency.checkedRadioButtonId) {
                R.id.radioEUR -> "EUR"
                R.id.radioUSD -> "USD"
                else -> "PLN"
            }

            val dCost = dailyCostPln / rate
            val mCost = monthlyCostPln / rate
            val yCost = yearlyCostPln / rate
            val totalKwh = dailyKwh * days

            textMonthlyMain.text = String.format(Locale.getDefault(), "%.2f %s", mCost, currency)
            textResults.text = String.format(Locale.getDefault(), "• Koszt dzienny: %.2f %s\n• Koszt roczny: %.2f %s\n• Zużycie: %.2f kWh", dCost, currency, yCost, currency, totalKwh)

            shareText = "📊 RAPORT ENERGII\nMiesięcznie: ${String.format("%.2f", mCost)} $currency\nZużycie: ${String.format("%.2f", totalKwh)} kWh"

            if (cardResults.visibility == View.GONE) {
                cardResults.visibility = View.VISIBLE
                cardResults.alpha = 0f
                cardResults.animate().alpha(1f).setDuration(500).start()
            }

            scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
        }

        buttonShare.setOnClickListener {
            if (shareText.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(intent, "Udostępnij raport"))
            }
        }
    }
}