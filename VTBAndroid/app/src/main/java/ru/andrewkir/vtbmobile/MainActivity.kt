package ru.andrewkir.vtbmobile

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.camerakit.CameraKitView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.CarPhoto
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_main)

        val pagerAdapter =
            PagerAdapter(supportFragmentManager) { num -> viewPager.currentItem = num }
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 1

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    2->{
                        var access = ""
                        var refresh = ""

                        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                        access = sharedPref.getString("access_token","")!!
                        refresh = sharedPref.getString("refresh_token","")!!

                        if(access == "" && refresh == "") {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }
}
