package com.swadharmaa.family.familytree

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.R
import com.swadharmaa.family.FamilyBody
import com.swadharmaa.family.FamilyTreeData
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.general.inputDateFormat
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.general.showMessage
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import com.swadharmaa.user.ErrorMsgDto
import com.swadharmaa.user.ResDto
import kotlinx.android.synthetic.main.act_add_family_tree.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddFamilyTree : AppCompatActivity() {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var dob: String = ""
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internet: InternetDetector? = null
    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_add_family_tree)

        txt_title.text = getString(R.string.family_tree)
        txt_edit.visibility = View.GONE
        im_back.setOnClickListener {
            onBackPressed()
        }
        internet = InternetDetector.getInstance(this@AddFamilyTree)

        try {
            val data = intent.getStringExtra(getString(R.string.data))
            if (data != null) {
                val jsonAdapter: JsonAdapter<FamilyTreeData> =
                    moshi.adapter(FamilyTreeData::class.java)
                val familyTreeData: FamilyTreeData? = jsonAdapter.fromJson(data.toString())
                println(familyTreeData)
                id = familyTreeData?._id.toString()
                edt_name.setText(familyTreeData?.name)
                edt_relationship.setText(familyTreeData?.relationship)
                edt_dob.setText(familyTreeData?.dateOfBirth)
                edt_rashi.setText(familyTreeData?.rashi)
                edt_nakshathram.setText(familyTreeData?.nakshathram)
                edt_padham.setText(familyTreeData?.padham)
                edt_city.setText(familyTreeData?.city)
                edt_mobile.setText(familyTreeData?.mobileNumber)
            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.toString())
        }

        edt_relationship.setOnClickListener {
            val relationship: ArrayList<String> = arrayListOf()
            relationship.addAll(resources.getStringArray(R.array.relationship))
            val spinnerDialog = SpinnerDialog(
                this@AddFamilyTree,
                relationship,
                "Select relationship.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_relationship.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_dob.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@AddFamilyTree,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edt_dob.text = Editable.Factory.getInstance().newEditable(
                        String.format("%02d", dayOfMonth) + "/" +
                                String.format("%02d", monthOfYear + 1) + "/" + "$year"
                    )
                    try {
                        val format = SimpleDateFormat("dd/MM/yyyy")
                        val date: Date? = format.parse(edt_dob.text.toString())
                        dob = inputDateFormat.format(date!!)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }, mYear, mMonth, mDay
            )
            val min =
                System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365.25 * 75
            val max =
                System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365.25 * 18
            datePickerDialog.datePicker.minDate = min.toLong()
            datePickerDialog.datePicker.maxDate = max.toLong()
            datePickerDialog.setTitle("")
            datePickerDialog.show()
        }

        edt_rashi.setOnClickListener {
            val rashi: ArrayList<String> = arrayListOf()
            rashi.addAll(resources.getStringArray(R.array.rashi))
            val spinnerDialog = SpinnerDialog(
                this@AddFamilyTree,
                rashi,
                "Select Rashi.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_rashi.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_nakshathram.setOnClickListener {
            val nakshathram: ArrayList<String> = arrayListOf()
            nakshathram.addAll(resources.getStringArray(R.array.nakshathram))
            val spinnerDialog = SpinnerDialog(
                this@AddFamilyTree,
                nakshathram,
                "Select Nakshathram.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_nakshathram.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_padham.setOnClickListener {
            val padham: ArrayList<String> = arrayListOf()
            padham.addAll(resources.getStringArray(R.array.padham))
            val spinnerDialog = SpinnerDialog(
                this@AddFamilyTree,
                padham,
                "Select Padham.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_padham.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        btn_update.setOnClickListener {
            family()
        }
    }


    private fun family() {
        lay_name.error = null
//        lay_relationship.error = null
//        lay_dob.error = null
//        lay_rashi.error = null
//        lay_nakshathram.error = null
//        lay_padham.error = null
//        lay_city.error = null
//        lay_mobile.error = null

        when {
            edt_name.length() < 3 -> {
                lay_name.error = "AddName's minimum character is 3."
            }
//            edt_relationship.length() < 1 -> {
//                lay_relationship.error = "Relationship is required."
//            }
//            edt_dob.length() < 1 -> {
//                lay_dob.error = "Date of birth is required."
//            }
//            edt_rashi.length() < 1 -> {
//                lay_rashi.error = "Rashi is required."
//            }
//            edt_nakshathram.length() < 1 -> {
//                lay_nakshathram.error = "Nakshathram is required."
//            }
//            edt_padham.length() < 1 -> {
//                lay_padham.error = "Padham is required."
//            }
//            edt_city.length() < 2 -> {
//                lay_city.error = "City's minimum character is 2."
//            }
//            edt_mobile.length() != 10 -> {
//                lay_mobile.error = "Enter the valid mobile number."
//            }
            else -> {
//                val map: HashMap<String, HashMap<String, Any>> = HashMap()
//                val mapData: HashMap<String, Any> = HashMap()
//                mapData["name"] = edt_name.text.toString().toLowerCase(Locale.getDefault())
//                mapData["relationship"] =
//                    edt_relationship.text.toString().toLowerCase(Locale.getDefault())
//                mapData["dateOfBirth"] = edt_dob.text.toString().toLowerCase(Locale.getDefault())
//                mapData["rashi"] = edt_rashi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["nakshathram"] =
//                    edt_nakshathram.text.toString().toLowerCase(Locale.getDefault())
//                mapData["padham"] = edt_padham.text.toString().toLowerCase(Locale.getDefault())
//                mapData["city"] = edt_city.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mobileNumber"] =
//                    edt_mobile.text.toString().toLowerCase(Locale.getDefault())
//                map["familyTree"] = mapData

                val familyBody = FamilyBody(
                    name = edt_name.text.toString().toLowerCase(Locale.getDefault()),
                    relationship = edt_relationship.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    dateOfBirth = edt_dob.text.toString().toLowerCase(Locale.getDefault()),
                    rashi = edt_rashi.text.toString().toLowerCase(Locale.getDefault()),
                    nakshathram = edt_nakshathram.text.toString().toLowerCase(Locale.getDefault()),
                    padham = edt_padham.text.toString().toLowerCase(Locale.getDefault()),
                    city = edt_city.text.toString().toLowerCase(Locale.getDefault()),
                    mobileNumber = edt_mobile.text.toString().toLowerCase(Locale.getDefault())
                )

                if (internet!!.checkMobileInternetConn(this@AddFamilyTree)) {
                    if (id.isNullOrEmpty()) {
                        create(familyBody)
                    } else {
                        update(id = id.toString(), familyBody = familyBody)
                    }
                } else {
                    showErrorMessage(
                        lay_root,
                        getString(R.string.msg_no_internet)
                    )
                }
            }
        }
    }

    private fun create(familyBody: FamilyBody) {
        try {
            Log.e("body", familyBody.toString())
            val family = RetrofitClient.instanceClient.familyTree(familyBody)
            family.enqueue(
                RetrofitWithBar(this@AddFamilyTree, object : Callback<FamilyDto> {
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
                                        finish()
                                        onBackPressed()
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
                                this@AddFamilyTree
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
    }

    private fun update(id: String, familyBody: FamilyBody) {
        try {
            Log.e("body", "$id $familyBody")
            val family =
                RetrofitClient.instanceClient.familyTree(id = id, bodyFamilyTree = familyBody)
            family.enqueue(
                RetrofitWithBar(this@AddFamilyTree, object : Callback<ResDto> {
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
                                        lay_root,
                                        response.body()!!.message
                                    )
                                    Handler().postDelayed({
                                        finish()
                                        onBackPressed()
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
                                this@AddFamilyTree
                            )
                        } else {
                            showErrorMessage(
                                lay_root,
                                response.message()
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResDto>, t: Throwable) {
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
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object {
        fun newInstance(): AddFamilyTree =
            AddFamilyTree()
    }
}