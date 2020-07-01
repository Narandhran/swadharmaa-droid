package com.swadharmaa.family.shraddha.gothram

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.R
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.family.FamilyData
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.general.showMessage
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import com.swadharmaa.user.ErrorMsgDto
import kotlinx.android.synthetic.main.act_add_gothram.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class AddGothram : AppCompatActivity() {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internet: InternetDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_add_gothram)

        internet = InternetDetector.getInstance(applicationContext)
        txt_title.text = getString(R.string.gothram)
        txt_edit.visibility = View.GONE
        im_back.setOnClickListener {
            onBackPressed()
        }

        try {
            val data = intent.getStringExtra(getString(R.string.data))
            if (data != null) {
                val jsonAdapter: JsonAdapter<FamilyData> =
                    moshi.adapter(FamilyData::class.java)
                val familyData: FamilyData? = jsonAdapter.fromJson(data.toString())
                println(familyData)
                edt_pithru_gothram.setText(familyData?.shraardhaInfo?.gothram?.pithruGothram.toString())
                edt_mathru_gothram.setText(familyData?.shraardhaInfo?.gothram?.mathruGothram.toString())
            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.toString())
        }

        btn_update.setOnClickListener {
            update()
        }
    }

    private fun update() {
        lay_pithru_gothram.error = null
//        lay_mathru_gothram.error = null

        when {
            edt_pithru_gothram.length() < 3 -> {
                lay_pithru_gothram.error = "Pithru gothram's minimum character is 3."
            }
//            edt_mathru_gothram.length() < 3 -> {
//                lay_mathru_gothram.error = "Mathru gothram's minimum character is 3."
//            }
            else -> {
                val map: HashMap<String, HashMap<String, HashMap<String, Any>>> = HashMap()
//                val mapGothram: HashMap<String, HashMap<String, Any>> = HashMap()
//                val mapData: HashMap<String, Any> = HashMap()
//                mapData["pithruGothram"] =
//                    edt_pithru_gothram.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathruGothram"] =
//                    edt_mathru_gothram.text.toString().toLowerCase(Locale.getDefault())
//                mapGothram["gothram"] = mapData
//                map["shraardhaInfo"] = mapGothram
                val gothram = com.swadharmaa.family.Gothram(
                    pithruGothram = edt_pithru_gothram.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathruGothram = edt_mathru_gothram.text.toString()
                        .toLowerCase(Locale.getDefault())
                )
                gothram(gothram)
            }
        }
    }

    private fun gothram(data: com.swadharmaa.family.Gothram) {
        if (internet!!.checkMobileInternetConn(this@AddGothram)) {
            try {
                Log.e("data", data.toString())
                val gothram = RetrofitClient.instanceClient.gothram(data)
                gothram.enqueue(
                    RetrofitWithBar(this@AddGothram, object : Callback<FamilyDto> {
                        @SuppressLint("SimpleDateFormat")
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onResponse(
                            call: Call<FamilyDto>,
                            response: Response<FamilyDto>
                        ) {
                            Log.e("onResponse", response.toString())
                            if (response.code() == 200) {
                                when (response.body()?.status) {
                                    200 -> {
                                        showMessage(lay_root, response.body()!!.message.toString())
                                        Handler().postDelayed({
                                            onBackPressed()
                                            finish()
                                        }, 200)
                                    }
                                    else -> {
                                        showErrorMessage(
                                            lay_root,
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
                                                lay_root,
                                                errorResponse.message
                                            )
                                        } else {
                                            showErrorMessage(
                                                lay_root,
                                                errorResponse.message
                                            )
                                        }

                                    } else {
                                        showErrorMessage(
                                            lay_root,
                                            getString(R.string.msg_something_wrong)
                                        )
                                        Log.e(
                                            "Response",
                                            response.body()!!.toString()
                                        )
                                    }
                                } catch (e: Exception) {
                                    showErrorMessage(
                                        lay_root,
                                        getString(R.string.msg_something_wrong)
                                    )
                                    Log.e("Exception", e.toString())
                                }

                            } else if (response.code() == 401) {
                                sessionExpired(
                                    this@AddGothram
                                )
                            } else {
                                showErrorMessage(
                                    lay_root,
                                    response.message()
                                )
                            }
                        }

                        override fun onFailure(call: Call<FamilyDto>, t: Throwable) {
                            Log.e("onResponse", t.message.toString())
                            showErrorMessage(
                                lay_root,
                                getString(R.string.msg_something_wrong)
                            )
                        }
                    })
                )

            } catch (e: Exception) {
                Log.d("ParseException", e.toString())
                e.printStackTrace()
            }
        } else {
            showErrorMessage(
                lay_root,
                getString(R.string.msg_no_internet)
            )
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

