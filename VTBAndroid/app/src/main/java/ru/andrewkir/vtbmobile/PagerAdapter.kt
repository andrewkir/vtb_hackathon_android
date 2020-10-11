package ru.andrewkir.vtbmobile

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fragmentManager: FragmentManager, var func: (position: Int) -> Unit) : FragmentPagerAdapter(fragmentManager){
    override fun getItem(position: Int) =
        when (position) {
            0 -> HistoryFragment(func)
            1 -> CameraFragment(func)
            2 -> ProfileFragment(func)
            else -> CameraFragment(func)
        }

    override fun getCount() = 3
}