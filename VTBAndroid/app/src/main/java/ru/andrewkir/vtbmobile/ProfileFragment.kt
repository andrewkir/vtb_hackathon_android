package ru.andrewkir.vtbmobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.JsonArray
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*
import ru.andrewkir.vtbmobile.Api.ApiClient

class ProfileFragment(var func: (position: Int) -> Unit) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeActivity.setOnClickListener {
            func(1)
        }

        updateData()

        exitButton.setOnClickListener {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            with (sharedPref.edit()) {
                putString("access_token", "")
                putString("refresh_token", "")
                apply()
            }
            func(1)
        }

        confData.setOnClickListener {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            var access = sharedPref.getString("access_token","")!!
            var refresh = sharedPref.getString("refresh_token","")!!

            val apiService = ApiClient(activity!!.applicationContext).instance
            apiService.extraData(
                "Bearer $access"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ res ->
                    val intent = Intent(activity, ConfData::class.java)
                    intent.putExtra("data", (res as JsonArray)[0].toString())
                    startActivity(intent)
                }, { error ->
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                })
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if(menuVisible){
            updateData()
        }
    }


    fun updateData(){
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        var fio = sharedPref.getString("fio","")!!
        nameText.text = fio
    }

}
