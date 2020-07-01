package com.swadharmaa.family.shraddha.thithi

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.family.ThithiData
import com.swadharmaa.general.inputDateFormat
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.general.showMessage
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import com.swadharmaa.user.ErrorMsgDto
import com.swadharmaa.user.ResDto
import kotlinx.android.synthetic.main.act_add_thithi.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddThithi : AppCompatActivity() {

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
        setContentView(R.layout.act_add_thithi)

        internet = InternetDetector.getInstance(applicationContext)
        txt_title.text = getString(R.string.thithi)
        txt_edit.visibility = View.GONE
        im_back.setOnClickListener {
            onBackPressed()
        }

        try {
            val data = intent.getStringExtra(getString(R.string.data))
            if (data != null) {
                val jsonAdapter: JsonAdapter<com.swadharmaa.family.Thithi> =
                    moshi.adapter(com.swadharmaa.family.Thithi::class.java)
                val thithiData: com.swadharmaa.family.Thithi? =
                    jsonAdapter.fromJson(data.toString())
                println(thithiData)
                id = thithiData?._id.toString()
                edt_relationship.setText(thithiData?.relationship)
                edt_name.setText(thithiData?.name)
                edt_masam_sauramanam.setText(thithiData?.masamSauramanam)
                edt_masam_chandramanam.setText(thithiData?.masamChandramanam)
                edt_paksham.setText(thithiData?.paksham)
                edt_thithi.setText(thithiData?.thithi)
                edt_date.setText(thithiData?.date)
                edt_time.setText(thithiData?.time)
            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.toString())
        }

        edt_relationship.setOnClickListener {
            val relationship: ArrayList<String> = arrayListOf()
            relationship.addAll(resources.getStringArray(R.array.thithi_relationship))
            val spinnerDialog = SpinnerDialog(
                this@AddThithi,
                relationship,
                "Relationship.",
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

        edt_masam_sauramanam.setOnClickListener {
            val sauramanam: ArrayList<String> = arrayListOf()
            sauramanam.addAll(resources.getStringArray(R.array.masam_sauramanam))
            val spinnerDialog = SpinnerDialog(
                this@AddThithi,
                sauramanam,
                "Masam (sauramanam).",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_masam_sauramanam.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_masam_chandramanam.setOnClickListener {
            val chandramanam: ArrayList<String> = arrayListOf()
            chandramanam.addAll(resources.getStringArray(R.array.masam_chandramanam))
            val spinnerDialog = SpinnerDialog(
                this@AddThithi,
                chandramanam,
                "Masam (chandramanam).",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_masam_chandramanam.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_paksham.setOnClickListener {
            val paksham: ArrayList<String> = arrayListOf()
            paksham.addAll(resources.getStringArray(R.array.paksham))
            val spinnerDialog = SpinnerDialog(
                this@AddThithi,
                paksham,
                "Paksham.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_paksham.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_thithi.setOnClickListener {
            val thithi: ArrayList<String> = arrayListOf()
            thithi.addAll(resources.getStringArray(R.array.thithi))
            val spinnerDialog = SpinnerDialog(
                this@AddThithi,
                thithi,
                "AddThithi.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_thithi.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_date.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this@AddThithi,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    edt_date.text = Editable.Factory.getInstance().newEditable(
                        String.format("%02d", dayOfMonth) + "/" +
                                String.format("%02d", monthOfYear + 1) + "/" + "$year"
                    )
                    try {
                        val format = SimpleDateFormat("dd/MM/yyyy")
                        val date: Date? = format.parse(edt_date.text.toString())
                        dob = inputDateFormat.format(date!!)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }, mYear, mMonth, mDay
            )
            val min = System.currentTimeMillis() - 1000
            val max = System.currentTimeMillis() + 15552000000
            datePickerDialog.datePicker.minDate = min
            datePickerDialog.datePicker.maxDate = max
            datePickerDialog.setTitle("")
            datePickerDialog.show()
        }

        edt_time.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime[Calendar.HOUR_OF_DAY]
            val minute = currentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@AddThithi,
                TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                    edt_time.setText(
                        "$selectedHour:$selectedMinute"
                    )
                }, hour, minute, true
            )
            mTimePicker.setTitle("")
            mTimePicker.show()
        }


        btn_update.setOnClickListener {
            thithi()
        }
    }

    private fun thithi() {
//        lay_relationship.error = null
        lay_name.error = null
//        lay_masam_sauramanam.error = null
//        lay_masam_chandramanam.error = null
//        lay_paksham.error = null
//        lay_thithi.error = null
//        lay_date.error = null
//        lay_time.error = null

        when {
//            edt_relationship.length() < 3 -> {
//                lay_relationship.error = "Relationship's minimum character is 3."
//            }
            edt_name.length() < 3 -> {
                lay_name.error = "AddName's minimum character is 3."
            }
//            edt_masam_sauramanam.length() < 1 -> {
//                lay_masam_sauramanam.error = "Masam (sauramanam) is required."
//            }
//            edt_masam_chandramanam.length() < 1 -> {
//                lay_masam_chandramanam.error = "Masam (chandramanam) is required."
//            }
//            edt_paksham.length() < 1 -> {
//                lay_paksham.error = "Paksham is required."
//            }
//            edt_thithi.length() < 1 -> {
//                lay_thithi.error = "AddThithi is required."
//            }
//            edt_date.length() < 1 -> {
//                lay_date.error = "Date is required."
//            }
//            edt_time.length() < 1 -> {
//                lay_time.error = "Time is required."
//            }
            else -> {
//                val map: HashMap<String, HashMap<String, HashMap<String, Any>>> = HashMap()
//                val mapThithi: HashMap<String, HashMap<String, Any>> = HashMap()
//                val mapData: HashMap<String, Any> = HashMap()
//                mapData["relationship"] =
//                    edt_relationship.text.toString().toLowerCase(Locale.getDefault())
//                mapData["name"] = edt_name.text.toString().toLowerCase(Locale.getDefault())
//                mapData["masamSauramanam"] =
//                    edt_masam_sauramanam.text.toString().toLowerCase(Locale.getDefault())
//                mapData["masamChandramanam"] =
//                    edt_masam_chandramanam.text.toString().toLowerCase(Locale.getDefault())
//                mapData["paksham"] = edt_paksham.text.toString().toLowerCase(Locale.getDefault())
//                mapData["thithi"] = edt_thithi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["date"] = edt_date.text.toString().toLowerCase(Locale.getDefault())
//                mapData["time"] = edt_time.text.toString().toLowerCase(Locale.getDefault())
//                mapThithi["thithi"] = mapData
//                map["shraardhaInfo"] = mapThithi

                val thithiData = ThithiData(
                    relationship = edt_relationship.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    name = edt_name.text.toString().toLowerCase(Locale.getDefault()),
                    masamSauramanam = edt_masam_sauramanam.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    masamChandramanam = edt_masam_chandramanam.text.toString()
                        .toLowerCase(Locale.getDefault()),
                    paksham = edt_paksham.text.toString().toLowerCase(Locale.getDefault()),
                    thithi = edt_thithi.text.toString().toLowerCase(Locale.getDefault()),
                    date = edt_date.text.toString().toLowerCase(Locale.getDefault()),
                    time = edt_time.text.toString().toLowerCase(Locale.getDefault())
                )
                if (internet!!.checkMobileInternetConn(this@AddThithi)) {
                    if (id.isNullOrEmpty()) {
                        create(thithiData)
                    } else {
                        update(id = id.toString(), data = thithiData)
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

    private fun create(data: ThithiData) {
        if (internet!!.checkMobileInternetConn(this@AddThithi)) {
            try {
                Log.e("body", data.toString())
                val familyCreate = RetrofitClient.instanceClient.thithi(data)
                familyCreate.enqueue(
                    RetrofitWithBar(this@AddThithi, object : Callback<FamilyDto> {
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
                                    this@AddThithi
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

    private fun update(id: String, data: ThithiData) {
        if (internet!!.checkMobileInternetConn(this@AddThithi)) {
            try {
                Log.e("body", "$id $data")
                val familyCreate = RetrofitClient.instanceClient.thithi(id = id, thithiData = data)
                familyCreate.enqueue(
                    RetrofitWithBar(this@AddThithi, object : Callback<ResDto> {
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
                                    this@AddThithi
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

    companion object {
        fun newInstance(): AddThithi =
            AddThithi()
    }
}

