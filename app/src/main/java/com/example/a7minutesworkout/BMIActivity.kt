package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityBmiactivityBinding
import com.example.a7minutesworkout.databinding.ActivityFinishBinding
import java.math.BigDecimal
import java.math.RoundingMode

private lateinit var binding: ActivityBmiactivityBinding

class BMIActivity : AppCompatActivity() {

    val METRIC_UNIT_VIEW = "METRIC_UNIT_VIEW"
    val US_UNITS_VIEW = "US_UNIT_VIEW"

    var currentVisibleView: String = METRIC_UNIT_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarBmiActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = "Calculate BMI"
        }
        binding.toolbarBmiActivity.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnCalculate.setOnClickListener {
            if (currentVisibleView.equals(METRIC_UNIT_VIEW)){
                if(validateMetricUnits()){
                    val heightValue: Float = binding.etMetricUnitHeight.text.toString().toFloat() / 100
                    val weightValue: Float = binding.etMetricUnitWeight.text.toString().toFloat()

                    val bmi = weightValue / (heightValue * heightValue)

                    displayBmiResult(bmi)
                }else{
                    Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(validateUsUnits()){

                    val usUnitHeightValueFeet:String = binding.etUsUnitHeightFeet.text.toString()
                    val usUnitHeightInch:String = binding.etUsUnitHeightInch.text.toString()
                    val usUnitWeight:Float = binding.etUsUnitWeight.text.toString().toFloat()

                    val heightValue = usUnitHeightValueFeet.toFloat()*12 + usUnitHeightInch.toFloat()

                    val bmi = 703 * (usUnitWeight/(heightValue * heightValue))

                    displayBmiResult(bmi)

                }else{
                    Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show()
                }
            }
        }

        makeVisibleMetricUnitsView()
        binding.rgUnits.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_metric_units){
                makeVisibleMetricUnitsView()

            }else{
                makeVisibleUsUnitsView()
            }

        }

    }

    private fun makeVisibleUsUnitsView(){
        currentVisibleView =US_UNITS_VIEW
        binding.tilWeightInput.visibility = View.GONE
        binding.tilHeightInput.visibility = View.GONE

        binding.etUsUnitWeight.text!!.clear()
        binding.etUsUnitHeightFeet.text!!.clear()
        binding.etUsUnitHeightInch.text!!.clear()

        binding.tilUsWeightInput.visibility = View.VISIBLE
        binding.llUsUnitsHeight.visibility = View.VISIBLE

        binding.llDisplayBmiResult.visibility = View.INVISIBLE

    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView =METRIC_UNIT_VIEW
        binding.tilWeightInput.visibility = View.VISIBLE
        binding.tilHeightInput.visibility = View.VISIBLE

        binding.etMetricUnitWeight.text!!.clear()
        binding.etMetricUnitHeight.text!!.clear()

        binding.tilUsWeightInput.visibility = View.GONE
        binding.llUsUnitsHeight.visibility = View.GONE

        binding.llDisplayBmiResult.visibility = View.INVISIBLE

    }

    private fun displayBmiResult(bmi: Float){
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        binding.llDisplayBmiResult.visibility = View.VISIBLE
        /*
        binding.tvYourBmi.visibility = View.VISIBLE
        binding.tvBmiValue.visibility = View.VISIBLE
        binding.tvBmiType.visibility = View.VISIBLE
        binding.tvBmiDescription.visibility = View.VISIBLE
        */

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding.tvBmiValue.text = bmiValue
        binding.tvBmiType.text = bmiLabel
        binding.tvBmiDescription.text = bmiDescription
    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true

        if (binding.etMetricUnitWeight.text.toString().isEmpty()) {
            isValid = false
        }else if(binding.etMetricUnitHeight.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
    private fun validateUsUnits(): Boolean{

        var isValid = true

        if (binding.etUsUnitWeight.text.toString().isEmpty()){
            isValid = false
        }else if (binding.etUsUnitHeightFeet.text.toString().isEmpty()){
            isValid = false
        }else if (binding.etUsUnitHeightInch.text.toString().isEmpty()){
            isValid = false
        }
        return isValid
    }
}