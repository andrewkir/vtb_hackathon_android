package ru.andrewkir.vtbmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.LoginRequest

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_login)

        closeLoginActivity.setOnClickListener {
            finish()
        }

        loginButton.setOnClickListener {
            if (emailInput.text.isNullOrEmpty() || passInput.text.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            } else {
                loading.visibility = View.VISIBLE

                val apiService = ApiClient(this).instance
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
                        with (sharedPref.edit()) {
                            putString("access_token", res.access)
                            putString("refresh_token", res.refresh)
                            apply()
                        }
                        finish()
                    }, { error ->

                        loading.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var access = sharedPref.getString("access_token","")!!
        var refresh = sharedPref.getString("refresh_token","")!!
        if(access != "" && refresh != "") finish()
    }

    fun regOnClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
