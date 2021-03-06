package com.swadharmaa

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swadharmaa.discover.Discover
import com.swadharmaa.family.Family
import com.swadharmaa.general.getData
import com.swadharmaa.interfaces.IOnBackPressed
import com.swadharmaa.user.Login
import kotlinx.android.synthetic.main.act_home.*
import kotlin.system.exitProcess

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_home)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigationView.selectedItemId = R.id.action_discover
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_discover -> {
                    val discover = Discover.newInstance()
                    openFragment(discover)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.action_family -> {
                    val isLoggedIn = getData("logged_user", applicationContext)
                    if (isLoggedIn == getString(R.string.skip)) {
                        val intent = Intent(this@Home, Login::class.java)
                        intent.putExtra(getString(R.string.data), getString(R.string.new_user))
                        startActivity(intent)
                    } else {
                        val family = Family.newInstance()
                        openFragment(family)
                        return@OnNavigationItemSelectedListener true
                    }
                }

//                R.id.action_account -> {
//                    val profile = Profile.newInstance()
//                    openFragment(profile)
//                    return@OnNavigationItemSelectedListener true
//                }
            }
            false
        }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        val fragment = this.supportFragmentManager.findFragmentById(R.id.container)
        val backPressed = (fragment as IOnBackPressed).onBackPressed()
        val selectedItemId = navigationView.selectedItemId
        if (!backPressed) {
            when {
                selectedItemId != R.id.action_discover -> {
                    navigationView.selectedItemId = R.id.action_discover
                }
                selectedItemId == R.id.action_discover -> {
                    exitApp()
                }
            }
        }
    }

    private fun exitApp() {
        val builder = AlertDialog.Builder(this@Home)
        builder.setTitle("Leave " + getString(R.string.app_name) + "?")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("YES") { dialog, which ->
            dialog.cancel()
            finish()
            exitProcess(0)
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
