package com.swadharmaa.family

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.swadharmaa.R
import com.swadharmaa.family.familyInfo.FamilyInfo
import com.swadharmaa.family.familytree.FamilyTree
import com.swadharmaa.family.personal.Personal
import com.swadharmaa.family.shraddha.Shraddha
import com.swadharmaa.general.reloadFragment
import com.swadharmaa.interfaces.IOnBackPressed
import kotlinx.android.synthetic.main.frag_family.*
import kotlinx.android.synthetic.main.toolbar.*

class Family : Fragment(), IOnBackPressed {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_family, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layout_refresh.setOnRefreshListener {
            reloadFragment(
                activity?.supportFragmentManager!!,
                this@Family
            )
            layout_refresh.isRefreshing = false
        }

        txt_title.text = getString(R.string.family)
        im_back.visibility = View.GONE
        txt_edit.visibility = View.GONE

        lay_personal.setOnClickListener {
            startActivity(Intent(requireActivity(), Personal::class.java))
        }

        lay_family.setOnClickListener {
            startActivity(Intent(requireActivity(), FamilyInfo::class.java))
        }

        lay_family_tree.setOnClickListener {
            startActivity(Intent(requireActivity(), FamilyTree::class.java))
        }

        lay_shraddha.setOnClickListener {
            startActivity(Intent(requireActivity(), Shraddha::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object {
        fun newInstance(): Family = Family()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}