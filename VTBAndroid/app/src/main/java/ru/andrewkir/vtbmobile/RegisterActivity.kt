package ru.andrewkir.vtbmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.emailInput
import kotlinx.android.synthetic.main.activity_register.textInputLayout4
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.LoginRequest
import ru.andrewkir.vtbmobile.DataClasses.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_register)

        emailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!emailInput.text.toString().matches(
                        Regex(
                            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+" +
                                    "(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                                    "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\" +
                                    ".)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\" +
                                    ".){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\" +
                                    "x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
                        )
                    )
                ) {
                    textInputLayout4.isErrorEnabled = true
                    textInputLayout4.error = "Введите валидный email"
                } else {
                    textInputLayout4.isErrorEnabled = false
                }
            }
        })

        passConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.toString() != passInput.text.toString()) {
                    textInputLayout6.isErrorEnabled = true
                    textInputLayout6.error = "Пароли должны совпадать"
                } else {
                    textInputLayout6.isErrorEnabled = false
                }
            }
        })

        registerButton.setOnClickListener {
            if (passInput.text.isNullOrEmpty() || passConfirm.text.isNullOrEmpty() || emailInput.text.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                loading.visibility = View.VISIBLE

                val apiService = ApiClient(this).instance
                apiService.register(
                    RegisterRequest(
                        username = emailInput.text.toString(),
                        password = passInput.text.toString(),
                        password_confirm = passConfirm.text.toString()
                    )
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ res ->
                        apiService.login(
                            LoginRequest(
                                emailInput.text.toString(),
                                passInput.text.toString()
                            )
                        )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ res ->

                                loading.visibility = View.INVISIBLE

                                val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
                                with(sharedPref.edit()) {
                                    putString("access_token", res.access)
                                    putString("refresh_token", res.refresh)
                                    apply()
                                }
                                apiService.saveExtraDataPOST(
                                    "Bearer " + res.access,
                                    JsonParser().parse("{email: ${emailInput.text.toString()}}")
                                )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        loading.visibility = View.INVISIBLE
                                        Toast.makeText(
                                            applicationContext,
                                            result.toString(),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        finish()
                                    }, { error ->
                                        loading.visibility = View.INVISIBLE
                                        Toast.makeText(
                                            applicationContext,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }, { error ->

                                loading.visibility = View.INVISIBLE
                                Toast.makeText(
                                    applicationContext,
                                    error.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                    }, { error ->
                        loading.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })
            }
        }

        closeRegActivity.setOnClickListener {
            finish()
        }
    }
}
