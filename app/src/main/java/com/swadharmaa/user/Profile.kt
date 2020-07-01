package com.swadharmaa.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import com.swadharmaa.BuildConfig
import com.swadharmaa.R
import com.swadharmaa.book.AddBook
import com.swadharmaa.category.AddCategory
import com.swadharmaa.favourite.Favourite
import com.swadharmaa.general.*
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import kotlinx.android.synthetic.main.act_profile.*
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Profile : AppCompatActivity() {

    var myApp: Application? = null
    private var profile: Call<ProDto>? = null
    var profileData: MutableList<ProData> = arrayListOf()
    var internet: InternetDetector? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_profile)

        im_back.setOnClickListener {
            onBackPressed()
        }

        layout_refresh.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            layout_refresh.isRefreshing = false
        }

        myApp = application as Application
        internet = InternetDetector.getInstance(this@Profile)

        val versionName = BuildConfig.VERSION_NAME
        txt_version.text = "-v $versionName"
        img_edit.setOnClickListener {
            try {
                val jsonAdapter: JsonAdapter<ProData> =
                    moshi.adapter(ProData::class.java)
                val json: String = jsonAdapter.toJson(profileData[0])
                val intent = Intent(this@Profile, Register::class.java)
                intent.putExtra("data", json)
                this.startActivity(intent)
                this.overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            } catch (e: Exception) {
                Log.e("Exception", e.toString())
            }
        }

        lay_logout.setOnClickListener {
            val builder = AlertDialog.Builder(this@Profile)
            builder.setTitle("Sign out")
                .setMessage("Signing out of your account will clear your session on this phone.")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, id ->
                    clearSession(this@Profile)
                    myApp!!.clearApplicationData()
                    val intent = Intent(this@Profile, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    this@Profile.overridePendingTransition(
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    this@Profile.finish()
                    dialog.dismiss()
                }
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, id -> dialog.cancel() }
            val alert = builder.create()
            alert.show()
        }

        lay_bookmarks.setOnClickListener {
            startActivity(Intent(this@Profile, Favourite::class.java))
        }

        if (internet?.checkMobileInternetConn(this)!!) {
            loadProfileInfo()
        } else {
            showErrorMessage(
                layout_refresh,
                getString(R.string.msg_no_internet)
            )
        }

        initSpeedDial()
    }

    private fun loadProfileInfo(): MutableList<ProData> {
        profile = RetrofitClient.instanceClient.profile()
        profile?.enqueue(object : Callback<ProDto> {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onResponse(
                call: Call<ProDto>,
                response: Response<ProDto>
            ) {
                Log.e("onResponse", response.toString())
                when {
                    response.code() == 200 -> {
                        when (response.body()?.status) {
                            200 -> {
                                try {
                                    profileData.clear()
                                    profileData.add(response.body()?.data!!)
                                    txt_fullname.text =
                                        StringUtils.capitalize(response.body()?.data?.fname?.toLowerCase()) + " " +
                                                StringUtils.capitalize(response.body()?.data?.lname?.toLowerCase())
                                    saveData(
                                        "fullname",
                                        txt_fullname.text.toString(),
                                        this@Profile.applicationContext
                                    )

                                    txt_mobile.text = response.body()?.data?.mobile
                                    saveData(
                                        "mobile",
                                        txt_mobile.text.toString(),
                                        this@Profile.applicationContext
                                    )
                                    val email = response.body()?.data?.email
                                    if (email.isNullOrEmpty()) {
                                        txt_email.visibility = View.GONE
                                    } else {
                                        txt_email.visibility = View.VISIBLE
                                        txt_email.text = email
                                        saveData(
                                            "username",
                                            email,
                                            this@Profile.applicationContext
                                        )
                                    }

                                    Picasso.get()
                                        .load(
                                            getData(
                                                "rootPath",
                                                this@Profile
                                            ) + Enums.Dp.value + response.body()?.data?.dp
                                        )
                                        .error(R.drawable.ic_dummy_profile)
                                        .placeholder(R.drawable.ic_dummy_profile)
                                        .into(img_profile)
                                    saveData(
                                        "dp",
                                        response.body()?.data?.dp.toString(),
                                        this@Profile.applicationContext
                                    )
                                    saveData(
                                        "user_id", response.body()?.data?._id.toString(),
                                        this@Profile.applicationContext
                                    )
                                } catch (e: Exception) {
                                    Log.d("Profile", e.toString())
                                    e.printStackTrace()
                                }
                            }
                            else -> {
                                showMessage(
                                    layout_refresh,
                                    response.message()
                                )
                            }
                        }
                    }
                    response.code() == 422 || response.code() == 400 -> {
                        try {
                            val moshi: Moshi = Moshi.Builder().build()
                            val adapter: JsonAdapter<ErrorMsgDto> =
                                moshi.adapter(ErrorMsgDto::class.java)
                            val errorResponse =
                                adapter.fromJson(response.errorBody()!!.string())
                            if (errorResponse != null) {
                                if (errorResponse.status == 400) {
                                    showErrorMessage(
                                        layout_refresh,
                                        errorResponse.message
                                    )
                                } else {
                                    showErrorMessage(
                                        layout_refresh,
                                        errorResponse.message
                                    )
                                }

                            } else {
                                showErrorMessage(
                                    layout_refresh,
                                    getString(R.string.msg_something_wrong)
                                )
                                Log.e(
                                    "Response",
                                    response.body()!!.toString()
                                )
                            }
                        } catch (e: Exception) {
                            showErrorMessage(
                                layout_refresh,
                                getString(R.string.msg_something_wrong)
                            )
                            Log.e("Exception", e.toString())
                        }

                    }
                    response.code() == 401 -> {
                        sessionExpired(this@Profile)
                    }
                    else -> {
                        showMessage(
                            layout_refresh,
                            response.message()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ProDto>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
                if (!call.isCanceled) {
                    showErrorMessage(
                        layout_refresh,
                        getString(R.string.msg_something_wrong)
                    )
                }
            }
        })
        return profileData

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initSpeedDial() {

        fab_admin.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_category, R.drawable.ic_category)
                .setLabel(getString(R.string.add_category))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, theme))
                .setLabelColor(getColor(R.color.theme_dark_grey))
                .setTheme(R.style.FabTheme)
                .create()
        )

        fab_admin.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_genre, R.drawable.ic_theatre)
                .setLabel(getString(R.string.add_genre))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, theme))
                .setLabelColor(getColor(R.color.theme_dark_grey))
                .setTheme(R.style.FabTheme)
                .create()
        )


        fab_admin.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_book, R.drawable.ic_book)
                .setLabel(getString(R.string.add_book))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.white, theme))
                .setLabelColor(getColor(R.color.theme_dark_grey))
                .setTheme(R.style.FabTheme)
                .create()
        )


        fab_admin.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_category -> {
                    startActivity(Intent(this@Profile, AddCategory::class.java))
                    fab_admin.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }

                R.id.fab_book -> {
                    startActivity(Intent(this@Profile, AddBook::class.java))
                    fab_admin.close()
                    return@OnActionSelectedListener true
                }

                R.id.fab_genre -> {
                    addGenre()
                    fab_admin.close()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }

    private fun addGenre() {
        val dialog = Dialog(this@Profile, R.style.DialogTheme)
        dialog.setContentView(R.layout.dialog_add_genre)
        val layName: TextInputLayout = dialog.findViewById(R.id.lay_name)
        val edtName: TextInputEditText = dialog.findViewById(R.id.edt_name)
        val btnCreate: MaterialButton = dialog.findViewById(R.id.btn_create)
        btnCreate.setOnClickListener {
            if (internet!!.checkMobileInternetConn(this@Profile)) {
                try {
                    layName.error = null
                    if (edtName.length() < 2) {
                        layName.error = "AddName minimum character is 2."
                    } else {
                        val mapData: HashMap<String, String> = HashMap()
                        mapData["genre"] = edtName.text.toString().toLowerCase(Locale.getDefault())
                        Log.e("mapData", mapData.toString())
                        val addGenre = RetrofitClient.instanceClient.addGenre(mapData)
                        addGenre.enqueue(
                            RetrofitWithBar(this@Profile, object : Callback<ResDto> {
                                @SuppressLint("SimpleDateFormat")
                                @RequiresApi(Build.VERSION_CODES.O)
                                override fun onResponse(
                                    call: Call<ResDto>,
                                    response: Response<ResDto>
                                ) {
                                    Log.e("onResponse", response.toString())
                                    if (response.code() == 200) {
                                        when (response.body()?.status) {
                                            200 -> {
                                                showMessage(
                                                    layName,
                                                    response.body()!!.message
                                                )
                                                edtName.let { v ->
                                                    val imm =
                                                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                                                }
                                                Handler().postDelayed({
                                                    edtName.setText("")
                                                }, 200)
                                            }
                                            else -> {
                                                showErrorMessage(
                                                    layName,
                                                    response.message()
                                                )
                                            }
                                        }

                                    } else if (response.code() == 422 || response.code() == 400) {
                                        try {
                                            val adapter: JsonAdapter<ErrorMsgDto> =
                                                moshi.adapter(ErrorMsgDto::class.java)
                                            val errorResponse =
                                                adapter.fromJson(response.errorBody()!!.string())
                                            if (errorResponse != null) {
                                                if (errorResponse.status == 400) {
                                                    showErrorMessage(
                                                        layName,
                                                        errorResponse.message
                                                    )
                                                } else {
                                                    showErrorMessage(
                                                        layName,
                                                        errorResponse.message
                                                    )
                                                }

                                            } else {
                                                showErrorMessage(
                                                    layName,
                                                    getString(R.string.msg_something_wrong)
                                                )
                                                Log.e(
                                                    "Response",
                                                    response.body()!!.toString()
                                                )
                                            }
                                        } catch (e: Exception) {
                                            showErrorMessage(
                                                layName,
                                                getString(R.string.msg_something_wrong)
                                            )
                                            Log.e("Exception", e.toString())
                                        }

                                    } else if (response.code() == 401) {
                                        sessionExpired(
                                            this@Profile
                                        )
                                    } else {
                                        showErrorMessage(
                                            layName,
                                            response.message()
                                        )
                                    }
                                }

                                override fun onFailure(call: Call<ResDto>, t: Throwable) {
                                    Log.e("onResponse", t.message.toString())
                                    showErrorMessage(
                                        layName,
                                        getString(R.string.msg_something_wrong)
                                    )
                                }
                            })
                        )

                    }

                } catch (e: Exception) {
                    Log.d("ParseException", e.toString())
                    e.printStackTrace()
                }
            } else {
                showErrorMessage(
                    layName,
                    getString(R.string.msg_no_internet)
                )
            }
        }
        dialog.show()
    }

    override fun onStop() {
        super.onStop()
        if (profile != null) {
            profile?.cancel()
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadProfileInfo()
    }

}