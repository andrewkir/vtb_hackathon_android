package ru.andrewkir.vtbmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_conf_data.*
import kotlinx.android.synthetic.main.activity_profile.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.UserDataRequest
import ru.andrewkir.vtbmobile.DataClasses.UserDataResponse
import java.text.SimpleDateFormat
import java.util.*

class ConfData : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_conf_data)

        val data =
            Gson().fromJson(intent.extras!!.getString("data", ""), UserDataResponse::class.java)

        if (data.gender.contains("male")) genderInput.setText("мужской")
        if (data.gender.contains("female")) genderInput.setText("женский")

        if (data.birth_date_time != "") date_input.setText(
            "${data.birth_date_time.split('-')[2]}.${
                data.birth_date_time.split(
                    '-'
                )[1]
            }.${data.birth_date_time.split('-')[0]}"
        )
        if (data.birth_place != "") cityInput.setText(data.birth_place)
        if (data.email != "") emailInput.setText(data.email)
        if (data.first_name.isNotEmpty() && data.middle_name.isNotEmpty() && data.family_name.isNotEmpty()) fioInput.setText(
            "${data.family_name} ${data.first_name} ${data.middle_name}"
        )

        if (data.income_amount != 0) personIncome.setText(data.income_amount.toString())
        if (data.phone != null) phoneInput.setText(data.phone.toString())

        saveData()

        saveButton.setOnClickListener {
            if (fioInput.text.isNullOrEmpty() || date_input.text.isNullOrEmpty() ||
                genderInput.text.isNullOrEmpty() || emailInput.text.isNullOrEmpty() ||
                phoneInput.text.isNullOrEmpty() ||
                cityInput.text.isNullOrEmpty() || personIncome.text.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Необходимо заполнить все поля",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val currentDate: String =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(Date())

                val familyName = fioInput.text.toString().split(" ")[0]
                val firstName = fioInput.text.toString().split(" ")[1]
                val middleName = fioInput.text.toString().split(" ")[2]

                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
                val access = sharedPref.getString("access_token", "")!!

                val reqBody = UserDataRequest(
                    email = emailInput.text.toString(),
                    income_amount = personIncome.text.toString().toInt(),
                    birth_date_time = "${
                        date_input.text.toString().split(".")[2]
                    }-${date_input.text.toString().split(".")[1]}-${
                        date_input.text.toString().split(".")[0]
                    }",
                    birth_place = cityInput.text.toString(),
                    first_name = firstName,
                    family_name = familyName,
                    middle_name = middleName,
                    gender = if (genderInput.text.toString()
                            .toLowerCase() == "мужчина"
                    ) "male" else "female",
                    phone = phoneInput.text.toString(),
                    nationality_country_code = "RU"
                )

                val apiService = ApiClient(this).instance
                apiService.saveExtraData(
                    data.id,
                    "Bearer $access",
                    reqBody
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ res ->
                        saveData()
                        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show()
                        finish()
                    }, { error ->
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })
            }
        }

        closeConfActivity.setOnClickListener {
            finish()
        }
    }

    fun saveData() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPref.edit()) {
            if (genderInput.text!!.isNotEmpty() && !textInputLayout3.isErrorEnabled) putString("gender", genderInput.text.toString())
            if (date_input.text!!.isNotEmpty() && !textInputLayout2.isErrorEnabled) putString("birth_date", date_input.text.toString())
            if (cityInput.text!!.isNotEmpty() && !textInputLayout7.isErrorEnabled) putString("birth_place", cityInput.text.toString())
            if (emailInput.text!!.isNotEmpty() && !textInputLayout4.isErrorEnabled) putString("email", emailInput.text.toString())
            if (fioInput.text!!.isNotEmpty() && !textInputLayout.isErrorEnabled) putString("fio", fioInput.text.toString())
            if (personIncome.text!!.isNotEmpty() && !textInputLayout12.isErrorEnabled) putString("income", personIncome.text.toString())
            if (phoneInput.text!!.isNotEmpty() && !textInputLayout5.isErrorEnabled) putString("phone", phoneInput.text.toString())
            apply()
        }
    }
}
