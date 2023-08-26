package com.generatepassword

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.generatepassword.databinding.ActivityPasswordBinding
import java.security.SecureRandom
import java.util.regex.Matcher
import java.util.regex.Pattern

class Password : AppCompatActivity() {

    private lateinit var binding: ActivityPasswordBinding
    private var lengthPassword = 6
    private var characters = ""
    private var isUpperChecked = true
    private var isLowerChecked = true
    private var isDigitChecked = true
    private var isSymbolsChecked = true
    private var isAllCharactersInvolved = true
    private var isExcludeSimilarCharsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        initValues(bundle)
        generatePassword()
        showExcludeSimilarCharsTv()
        binding.copyBtn.setOnClickListener { copyPassword() }
        binding.reGenerate.setOnClickListener { generatePassword() }
    }

    private fun initValues(bundle: Bundle?) {
        bundle?.let {
            lengthPassword = bundle.getInt("seekbar_value")
            isUpperChecked = bundle.getBoolean("isUpperChecked")
            isUpperChecked = bundle.getBoolean("isUpperChecked")
            isLowerChecked = bundle.getBoolean("isLowerChecked")
            isDigitChecked = bundle.getBoolean("isDigitChecked")
            isSymbolsChecked = bundle.getBoolean("isSymbolsChecked")
            isExcludeSimilarCharsChecked = bundle.getBoolean("isExcludeSimilarCharsChecked")
        }
    }

    private fun generatePassword() {
        var password = generate()
        while (!checkIsValidate(password)) {
            password = generate()
        }
        refreshUI(password)
    }

    private fun generate(): String {
        selectedCharacters()
        val sb = StringBuilder()
        val random = SecureRandom()

        for (i in 0..lengthPassword) {
            val index: Int = random.nextInt(characters.length)
            sb.append(characters[index])
        }
        return sb.toString()
    }

    private fun selectedCharacters() {
        if (isUpperChecked) characters += getString(R.string.upper_letters)
        if (isLowerChecked) characters += getString(R.string.lower_letters)
        if (isDigitChecked) characters += getString(R.string.digit_chars)
        if (isSymbolsChecked) characters += getString(R.string.symbols_chars)
        if (isExcludeSimilarCharsChecked) {
            var str = characters
            val charsToRemove = "iIl1oO0"
            charsToRemove.forEach { str = str.replace(it.toString(), "") }
            characters = str
        }
    }

    private fun checkIsValidate(password: String): Boolean {
        isAllCharactersInvolved = isUpperChecked && isLowerChecked && isDigitChecked && isSymbolsChecked
        if (isAllCharactersInvolved) {
            with(password) {
                if (hasLowerCase() && hasUpperCase() && hasDigit() && hasSymbols()) {
                    return true
                }
                return false
            }
        }
        return true
    }

    private fun CharSequence.hasLowerCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase: Matcher = pattern.matcher(this)
        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase(): Boolean {
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase: Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigit(): Boolean {
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit: Matcher = pattern.matcher(this)
        return hasDigit.find()
    }

    private fun CharSequence.hasSymbols(): Boolean {
        val pattern: Pattern = Pattern.compile("[!@#$%^&*]")
        val hasSymbols: Matcher = pattern.matcher(this)
        return hasSymbols.find()
    }

    private fun refreshUI(password: String) {
        binding.passwordResult.text = password
        binding.animation.playAnimation()
        calculateStrength(password)
        refreshCheckText()
    }

    private fun calculateStrength(password: String) {
        if (password.length in 0..5) {
            displayPasswordStrengthen(R.color.weak, "WEAK")
        } else if (password.length in 6..9) {
            displayPasswordStrengthen(R.color.medium, "MEDIUM")
        } else if (password.length in 10..16) {
            displayPasswordStrengthen(R.color.strong, "STRONG")
        } else if (password.length > 16 && isAllCharactersInvolved) {
            displayPasswordStrengthen(R.color.bulletproof, "BULLETPROOF")
        } else if (password.length > 16 && !isAllCharactersInvolved) {
            displayPasswordStrengthen(R.color.strong, "STRONG")
        }
    }

    private fun displayPasswordStrengthen(color: Int, text: String) {
        binding.passwordStrengthTv.setTextColor(resources.getColor(color))
        binding.passwordIndicator.setBackgroundColor(resources.getColor(color))
        binding.passwordStrengthTv.text = text
    }

    private fun copyPassword() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            "Edittext", binding.passwordResult.text.toString()
        )
        clipboard.setPrimaryClip(clipData)
        Toast.makeText(this, "Text copied", Toast.LENGTH_SHORT).show()
    }

    private fun refreshCheckText() {
        with(binding) {
            if (!isUpperChecked) {
                upperCaseImg.visibility = View.GONE
                upperCaseTxt.visibility = View.GONE
            }
            if (!isLowerChecked){
                lowerCaseImg.visibility = View.GONE
                lowerCaseTxt.visibility = View.GONE
            }
            if (!isDigitChecked) {
                digitImg.visibility = View.GONE
                digitTxt.visibility = View.GONE
            }
            if (!isSymbolsChecked) {
                symbolsImg.visibility = View.GONE
                symbolsTv.visibility = View.GONE
            }
        }
    }

    private fun showExcludeSimilarCharsTv(){
        binding.similarCharImg.visibility = if (isExcludeSimilarCharsChecked) View.VISIBLE else View.GONE
        binding.similarCharTv.visibility = if (isExcludeSimilarCharsChecked) View.VISIBLE else View.GONE
    }
}