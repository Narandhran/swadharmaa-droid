package com.swadharmaa.interfaces

import com.swadharmaa.banner.BannerData
import com.swadharmaa.family.FamilyTreeData

interface IOnBackPressed {
    fun onBackPressed(): Boolean
}

interface OnBannerClickListener {
    fun onItemClick(item: BannerData?)
}

interface OnFamilyClickListener {
    fun onItemClick(item: FamilyTreeData?)
}

interface OnThithiClickListener {
    fun onItemClick(item: com.swadharmaa.family.Thithi?)
}

interface AsyncResponse {
    fun processFinish(output: String)
}