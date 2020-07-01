package com.swadharmaa.family.shraddha

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.swadharmaa.family.shraddha.gothram.Gothram
import com.swadharmaa.family.shraddha.name.Name
import com.swadharmaa.family.shraddha.samayal.Samayal
import com.swadharmaa.family.shraddha.thithi.Thithi
import com.swadharmaa.family.shraddha.vazhakkam.Vazhakkam

@Suppress("DEPRECATION")
class ShraddhaPagerAdapter(
    fm: FragmentManager,
    var tabCount: Int
) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Gothram()
            }
            1 -> {
                Name()
            }
            2 -> {
                Thithi()
            }
            3 -> {
                Samayal()
            }
            else -> {
                Vazhakkam()
            }
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}