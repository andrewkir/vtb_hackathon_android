package ru.andrewkir.vtbmobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_credit.*

class CreditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_credit)

        closeCreditActivity.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
