package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.util.Locale

private lateinit var binding: ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress = 0

    private var exerciseRestTimer: CountDownTimer? = null
    private var exerciseRestProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null

    private var player:MediaPlayer? = null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarExerciseActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolBarExerciseActivity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        binding.llExerciseView.visibility = View.GONE

        tts = TextToSpeech(this, this)



        exerciseList = Constants.defaultExerciseList()

        setupRestView()

        setupExerciseStatusRecyclerView()

    }

    override fun onDestroy() {
        super.onDestroy()
        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        if(exerciseRestTimer!=null){
            exerciseRestTimer!!.cancel()
            exerciseRestProgress = 0
        }

        if(tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player != null){
            player!!.stop()
        }

    }

    private fun setRestProgressBar(){
        binding.progressBar.progress = restProgress
        restTimer = object: CountDownTimer(1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding.progressBar.progress = 10-restProgress
                binding.tvTimer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()

                setupExerciseView()
            }
        }.start()
    }
    private fun setupRestView(){

        try {
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player!!.isLooping = false
            player!!.start()

        }catch (e: Exception){
            e.printStackTrace()
        }

        binding.llRestView.visibility = View.VISIBLE
        binding.llExerciseView.visibility = View.GONE

        if(restTimer != null){
            restTimer!!.cancel()
            restProgress = 0
        }

        binding.tvNextExerciseName.text = exerciseList!![currentExercisePosition+1].getName()

        setRestProgressBar()

    }

    private fun setExerciseRestProgressBar(){
        binding.exerciseProgressBar.progress = exerciseRestProgress
        exerciseRestTimer = object : CountDownTimer(1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseRestProgress++
                binding.exerciseProgressBar.progress = 30 - exerciseRestProgress
                binding.tvExerciseTimer.text = (30 - exerciseRestProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1){

                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                }else{
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)

                }
            }
        }.start()
    }
    private fun setupExerciseView(){

        binding.llRestView.visibility = View.GONE
        binding.llExerciseView.visibility = View.VISIBLE

        if(exerciseRestTimer != null){
            exerciseRestTimer!!.cancel()
            exerciseRestProgress = 0
        }

        speackOut(exerciseList!![currentExercisePosition].getName())

        setExerciseRestProgressBar()

        binding.ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding.tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {

        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "The language speciffied is not supported")
            }
        }else{
            Log.e("TTS", "Initialization failed")
        }

    }

    private fun speackOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun setupExerciseStatusRecyclerView(){
        binding.rvExerciseStatus.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)

        binding.rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton(){
        val customDialog = Dialog(this)

        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)

        //var tv_yes = customDialog.findViewById<TextView>(R.id.tvYes)
        //var tv_no = customDialog.findViewById<TextView>(R.id.tvNo)

        customDialog.findViewById<TextView>(R.id.tvYes).setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.findViewById<TextView>(R.id.tvNo).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

}