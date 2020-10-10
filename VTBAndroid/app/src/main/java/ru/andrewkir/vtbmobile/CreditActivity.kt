package ru.andrewkir.vtbmobile

import android.content.res.Resources.getSystem
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_credit.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.CreditRequest
import ru.andrewkir.vtbmobile.DataClasses.CustomerParty
import ru.andrewkir.vtbmobile.DataClasses.Person
import java.text.SimpleDateFormat
import java.util.*

class CreditActivity : AppCompatActivity() {

    var requestNum = 100000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_credit)

        date_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !s.toString()
                        .matches(Regex("[0-9]{2}.[0-9]{2}.[0-9]{4}"))
                ) {
                    textInputLayout2.isErrorEnabled = true
                    textInputLayout2.error = "Формат даты: 01.01.2000"
                } else {
                    textInputLayout2.isErrorEnabled = false
                }
            }
        })

        fioInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !s.toString()
                        .matches(Regex("[а-яА-Я]+ [а-яА-Я]+ [а-яА-Я]+"))
                ) {
                    textInputLayout.isErrorEnabled = true
                    textInputLayout.error = "Формат ФИО: Иванов Иван Иванович"
                } else {
                    textInputLayout.isErrorEnabled = false
                }
            }
        })
        genderInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !(s.toString().toLowerCase()
                        .contains("мужской") or s.toString().toLowerCase()
                        .contains("женский"))
                ) {
                    textInputLayout3.isErrorEnabled = true
                    textInputLayout3.error = "Введите мужской или женский"
                } else {
                    textInputLayout3.isErrorEnabled = false
                }
            }
        })

        closeCreditActivity.setOnClickListener {
            finish()
        }

        continueButton.setOnClickListener {
            if (fioInput.text.isNullOrEmpty() || date_input.text.isNullOrEmpty() ||
                        genderInput.text.isNullOrEmpty() || emailInput.text.isNullOrEmpty() ||
                        phoneInput.text.isNullOrEmpty() || passportInput.text.isNullOrEmpty() ||
                        cityInput.text.isNullOrEmpty() || requestNum == 0 || creditRange.text.isNullOrEmpty() ||
                        creditRate.text.isNullOrEmpty() || personIncome.text.isNullOrEmpty() ||
                        additionalComment.text.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Необходимо заполнить все поля для оформления кредита",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val currentDate: String =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(Date())

                val familyName = fioInput.text.toString().split(" ")[0]
                val firstName = fioInput.text.toString().split(" ")[1]
                val middleName = fioInput.text.toString().split(" ")[2]

                val reqBody = CreditRequest(
                    "Comment Example",
                    CustomerParty(
                        emailInput.text.toString(),
                        personIncome.text.toString().toInt(),
                        Person(
                            "${date_input.text.toString().split(".")[2]}-${date_input.text.toString().split(".")[1]}-${date_input.text.toString().split(".")[0]}",
                            cityInput.text.toString(),
                            familyName,
                            firstName,
                            middleName,
                            if(genderInput.text.toString().toLowerCase() == "мужчина") "male" else "female",
                            "RU"
                        ),
                        phoneInput.text.toString()
                    ),
                    currentDate,
                    creditRate.text.toString().removeSuffix("%").toFloat(),
                    requestNum,
                    creditRange.text.toString().toInt(),
                    creditAuto.text.toString(),
                    carCost.text.toString().replace(" ", "").toInt()
                )

                val apiService = ApiClient.instance
                apiService.createNewCredit(
                    reqBody
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ res ->
                        Toast.makeText(this, res.toString(), Toast.LENGTH_SHORT).show()
                    }, { error ->
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })
            }
        }
        scrollView.post {
            if (scrollLinear.height - continueButton.height - (continueButton.layoutParams as ViewGroup.MarginLayoutParams).topMargin - (continueButton.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin > windowManager.defaultDisplay.height) {
                var params = continueButton.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = continueButton.height * 2
                continueButton.layoutParams = params
            } else {
                var params = continueButton.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin =
                    windowManager.defaultDisplay.height - scrollLinear.height + params.bottomMargin * 2
                continueButton.layoutParams = params
            }
        }

        main_slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                creditSumText.text = "Сумма кредита : ${i}00000"
                requestNum = i
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        creditRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                when {
                    creditRate.text.isNullOrEmpty() -> return
                    Regex("\\d+\\%").matches(creditRate.text.toString()) -> return
                    creditRate.text.toString() == "%" -> creditRate.setText("")
                    creditRate.text.toString().length == 1 -> {
                        creditRate.setText(creditRate.text.toString() + "%")
                        creditRate.setSelection(1)
                    }
                    creditRate.text.toString().endsWith("%")
                        .not() -> creditRate.setText(creditRate.text.toString() + "%")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()

    val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()
}
