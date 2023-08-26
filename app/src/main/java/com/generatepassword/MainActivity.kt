package com.generatepassword

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.generatepassword.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        position = Integer.parseInt(binding.enterLengthSeekbar.min.toString())
        binding.seekbarPosition.text = position.toString()
        binding.enterLengthSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                position = progress
                binding.seekbarPosition.text = position.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.generateBtn.setOnClickListener {
            if (checkCheckBoxes()) {
                startSecondActivity()
            } else {
                Toast.makeText(this, getString(R.string.select_check_box), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkCheckBoxes(): Boolean {
        with(binding) {
            if (upperLettersCb.isChecked || lowerLettersCb.isChecked || digitCb.isChecked || symbolsCb.isChecked) {
                return true
            }
            return false
        }
    }

    private fun startSecondActivity(){
        startActivity(Intent(this, Password::class.java).apply {
            putExtra("seekbar_value", position)
            with(binding) {
                putExtra("isUpperChecked", upperLettersCb.isChecked)
                putExtra("isLowerChecked", lowerLettersCb.isChecked)
                putExtra("isDigitChecked", digitCb.isChecked)
                putExtra("isSymbolsChecked", symbolsCb.isChecked)
                putExtra("isExcludeSimilarCharsChecked", similarCharsCb.isChecked)
            }
        })
    }

//    binding.animation.setAnimation(R.raw.successanimationlight)
//    binding.animation.loop(true)
//    binding.animation.playAnimation()
}