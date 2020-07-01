package com.swadharmaa.family.shraddha.name

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
import kotlinx.android.synthetic.main.act_add_name.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AddName : AppCompatActivity() {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internet: InternetDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_add_name)

        internet = InternetDetector.getInstance(this@AddName)
        txt_title.text = getString(R.string.name)
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
                edt_pithru.setText(
                    familyData?.shraardhaInfo!!.name?.pithru.toString()
                )
                edt_pithamaha.setText(
                    familyData.shraardhaInfo!!.name?.pithamaha.toString()
                )
                edt_prapithamaha.setText(
                    familyData.shraardhaInfo!!.name?.prapithamaha.toString()
                )
                edt_mathru.setText(
                    familyData.shraardhaInfo!!.name?.mathru.toString()
                )
                edt_pithamahi.setText(
                    familyData.shraardhaInfo!!.name?.pithamahi.toString()
                )
                edt_prapithamahi.setText(
                    familyData.shraardhaInfo!!.name?.prapithamahi.toString()
                )
                edt_mathamaha.setText(
                    familyData.shraardhaInfo!!.name?.mathamaha.toString()
                )
                edt_mathru_pithamaha.setText(
                    familyData.shraardhaInfo!!.name?.mathruPithamaha.toString()
                )
                edt_mathru_prapithamaha.setText(
                    familyData.shraardhaInfo!!.name?.mathruPrapithamaha.toString()
                )
                edt_mathamahi.setText(
                    familyData.shraardhaInfo!!.name?.mathamahi.toString()
                )
                edt_mathru_pithamahi.setText(
                    familyData.shraardhaInfo!!.name?.mathruPithamahi.toString()
                )
                edt_mathru_prapitamahi.setText(
                    familyData.shraardhaInfo!!.name?.mathruPrapitamahi.toString()
                )

            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.toString())
        }

        btn_update.setOnClickListener {
            update()
        }
    }

    private fun update() {

        lay_pithru.error = null
//        lay_pithamaha.error = null
//        lay_prapithamaha.error = null
//        lay_mathru.error = null
//        lay_pithamahi.error = null
//        lay_prapithamahi.error = null
//        lay_mathamaha.error = null
//        lay_mathru_pithamaha.error = null
//        lay_mathru_prapithamaha.error = null
//        lay_mathamahi.error = null
//        lay_mathru_pithamahi.error = null
//        lay_mathru_prapitamahi.error = null

        when {

            edt_pithru.length() < 3 -> {
                lay_pithru.error = "Pithru's minimum character is 3."
            }
//            edt_pithamaha.length() < 3 -> {
//                lay_pithamaha.error = "Pithamaha's minimum character is 3."
//            }
//            edt_prapithamaha.length() < 3 -> {
//                lay_prapithamaha.error = "Prapithamah's minimum character is 3."
//            }
//            edt_mathru.length() < 3 -> {
//                lay_mathru.error = "Mathru's minimum character is 3."
//            }
//            edt_pithamahi.length() < 3 -> {
//                lay_pithamahi.error = "Pitamahi's minimum character is 3."
//            }
//            edt_prapithamahi.length() < 3 -> {
//                lay_prapithamahi.error = "Prapitamahi's minimum character is 3."
//            }
//            edt_mathamaha.length() < 3 -> {
//                lay_mathamaha.error = "Mathamaha's minimum character is 3."
//            }
//            edt_mathru_pithamaha.length() < 3 -> {
//                lay_mathru_pithamaha.error = "Mathru pithamaha's minimum character is 3."
//            }
//            edt_mathru_prapithamaha.length() < 3 -> {
//                lay_mathru_prapithamaha.error = "Mathru prapithamaha's minimum character is 3."
//            }
//            edt_mathamahi.length() < 3 -> {
//                lay_mathamahi.error = "Mathamahi's minimum character is 3."
//            }
//            edt_mathru_pithamahi.length() < 3 -> {
//                lay_mathru_pithamahi.error = "Mathru pithamahi's minimum character is 3."
//            }
//            edt_mathru_prapitamahi.length() < 3 -> {
//                lay_mathru_prapitamahi.error = "Mathru prapitamahi's minimum character is 3."
//            }
            else -> {
//                val map: HashMap<String, HashMap<String, HashMap<String, Any>>> = HashMap()
//                val mapName: HashMap<String, HashMap<String, Any>> = HashMap()
//                val mapData: HashMap<String, Any> = HashMap()
//                mapData["pithru"] = edt_pithru.text.toString().toLowerCase(Locale.getDefault())
//                mapData["pithamaha"] =
//                    edt_pithamaha.text.toString().toLowerCase(Locale.getDefault())
//                mapData["prapithamaha"] =
//                    edt_prapithamaha.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathru"] = edt_mathru.text.toString().toLowerCase(Locale.getDefault())
//                mapData["pithamahi"] =
//                    edt_pithamahi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["prapithamahi"] =
//                    edt_prapithamahi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathamaha"] =
//                    edt_mathamaha.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathruPithamaha"] =
//                    edt_mathru_pithamaha.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathruPrapithamaha"] =
//                    edt_mathru_prapithamaha.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathamahi"] =
//                    edt_mathamahi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathruPithamahi"] =
//                    edt_mathru_pithamahi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mathruPrapitamahi"] =
//                    edt_mathru_prapitamahi.text.toString().toLowerCase(Locale.getDefault())
//                mapName["name"] = mapData
//                map["shraardhaInfo"] = mapName

                val name = com.swadharmaa.family.Name(
                    pithru = edt_pithru.text.toString().toLowerCase(Locale.getDefault()),
                    pithamaha = edt_pithamaha.text.toString().toLowerCase(Locale.getDefault()),
                    prapithamaha = edt_prapithamaha.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathru = edt_mathru.text.toString().toLowerCase(Locale.getDefault()),
                    pithamahi = edt_pithamahi.text.toString().toLowerCase(Locale.getDefault()),
                    prapithamahi = edt_prapithamahi.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathamaha = edt_mathamaha.text.toString().toLowerCase(Locale.getDefault()),
                    mathruPithamaha = edt_mathru_pithamaha.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathruPrapithamaha = edt_mathru_prapithamaha.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathamahi = edt_mathamahi.text.toString().toLowerCase(Locale.getDefault()),
                    mathruPithamahi = edt_mathru_pithamahi.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    mathruPrapitamahi = edt_mathru_prapitamahi.text.toString()
                        .toLowerCase(Locale.getDefault())
                )
                name(name)
            }
        }
    }

    private fun name(data: com.swadharmaa.family.Name) {
        if (internet!!.checkMobileInternetConn(this@AddName)) {
            try {
                Log.e("body", data.toString())
                val name = RetrofitClient.instanceClient.name(data)
                name.enqueue(
                    RetrofitWithBar(this@AddName, object : Callback<FamilyDto> {
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
                                    this@AddName
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

