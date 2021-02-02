package com.synerzip.editext

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var button: Button
    val dotString = "•"
    var originalPasswordText = "" // original password stored here
    var maskedPasswordText = "" // masked is stored here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.etPasword)
        button = findViewById(R.id.button)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isNotEmpty() && originalPasswordText.length < s.length) {
                        Handler().postDelayed({

                            val p: Pattern = Pattern.compile("^[0-9]$")
                            val strinToAppend: String =
                                editText.text.toString()[editText.selectionStart - 1].toString()
                            val numberMatcher: Matcher = p.matcher(
                                strinToAppend.replace(dotString, "").replace("/", "")
                            )

                            if (editText.selectionStart == editText.text.length) {
                                if (numberMatcher.matches() || strinToAppend == "/")
                                    originalPasswordText = StringBuilder(originalPasswordText)
                                        .append(strinToAppend).toString()
                            } else {
                                if (numberMatcher.matches() || strinToAppend == "/")
                                    originalPasswordText = StringBuilder(originalPasswordText)
                                        .insert(editText.selectionStart - 1, strinToAppend)
                                        .toString()
                            }

                            val m: Matcher =
                                p.matcher(it.toString().replace(dotString, "").replace("/", ""))
                            if (m.matches()) {

                                maskedPasswordText = if (it.length == 2 && !it.contains("/")) {
                                    ("$it/").redact()
                                } else {
                                    it.toString().redact()
                                }
                                editText.setText(maskedPasswordText)
                                editText.setSelection(maskedPasswordText.length)
                            }
                        }, 100)
                    }
                }
            }

        })

        button.setOnClickListener {
            Toast.makeText(this, originalPasswordText, Toast.LENGTH_SHORT).show()
        }
    }

    fun String.redact(): String {
        val charArray = toCharArray()

        charArray.withIndex()
            .filter { (_, char) -> Character.isDigit(char) }
            .take(length)
            .forEach { (index, _) -> charArray[index] = '•' }

        if (length > 3 && !charArray.contains('/')) {
            charArray[2] = '/'
        }

        return String(charArray)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode === KeyEvent.KEYCODE_DEL) {
            Handler().postDelayed({
                try {
                    val pos: Int = editText.selectionStart
                    originalPasswordText =
                        StringBuilder(originalPasswordText).deleteCharAt(pos).toString()
                    maskedPasswordText =
                        StringBuilder(maskedPasswordText).deleteCharAt(pos).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, 100)
        }
        return super.onKeyUp(keyCode, event)
    }

}