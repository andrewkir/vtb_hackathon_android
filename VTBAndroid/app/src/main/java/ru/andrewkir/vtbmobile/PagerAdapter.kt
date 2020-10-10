package ru.andrewkir.vtbmobile

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PagerAdapter(fragmentManager: FragmentManager, func: (position: Int) -> Unit) : FragmentPagerAdapter(fragmentManager){
    override fun getItem(position: Int) =
        when (position) {
            0 -> HistoryFragment(func)
            1 -> CameraFragment.newInstance()
            2 -> ProfileFragment.newInstance()
            else -> CameraFragment.newInstance()
        }

    override fun getCount() = 3
}