package ru.andrewkir.vtbmobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*


public class MainActivity : AppCompatActivity() {

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
                    2 -> {
                        var access = ""
                        var refresh = ""

                        val sharedPref =
                            PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                        access = sharedPref.getString("access_token", "")!!
                        refresh = sharedPref.getString("refresh_token", "")!!

                        if (access == "" && refresh == "") {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    0 -> {
                        var access = ""
                        var refresh = ""

                        val sharedPref =
                            PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                        access = sharedPref.getString("access_token", "")!!
                        refresh = sharedPref.getString("refresh_token", "")!!

                        if (access == "" && refresh == "") {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    fun getAppContext(): Context? {
        return this
    }
}
