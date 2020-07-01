package com.swadharmaa.family.personal

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.R
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.family.FamilyData
import com.swadharmaa.family.PersonalInfo
import com.swadharmaa.general.*
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import com.swadharmaa.user.DialogChoosePhoto
import com.swadharmaa.user.ErrorMsgDto
import kotlinx.android.synthetic.main.act_add_personal.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddPersonal : AppCompatActivity() {

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var dob: String = ""
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internet: InternetDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_add_personal)

        txt_title.text = getString(R.string.personal_information)
        txt_edit.visibility = View.GONE
        im_back.setOnClickListener {
            onBackPressed()
        }

        internet = InternetDetector.getInstance(this@AddPersonal)
        LocalBroadcastManager.getInstance(applicationContext!!).registerReceiver(
            fileLocationReceiver,
            IntentFilter(Constants.fileLocation)
        )

        try {
            val data = intent.getStringExtra(getString(R.string.data))
            if (data != null) {
                val jsonAdapter: JsonAdapter<FamilyData> =
                    moshi.adapter(FamilyData::class.java)
                val familyData: FamilyData? = jsonAdapter.fromJson(data.toString())
                println(familyData)
                edt_name.setText(familyData?.personalInfo?.name)
                edt_sharma.setText(familyData?.personalInfo?.sharma)
                edt_dob.setText(familyData?.personalInfo?.dateOfBirth)
                edt_time.setText(familyData?.personalInfo?.timeOfBirth)
                edt_place.setText(familyData?.personalInfo?.placeOfBirth)
                edt_rashi.setText(familyData?.personalInfo?.rashi)
                edt_nakshathram.setText(familyData?.personalInfo?.nakshathram)
                edt_padham.setText(familyData?.personalInfo?.padham)
                edt_city.setText(familyData?.personalInfo?.city)
                edt_mobile.setText(familyData?.personalInfo?.mobileNumber)
                edt_gender.setText(familyData?.personalInfo?.gender)
                edt_email.setText(familyData?.personalInfo?.email)
                edt_status.setText(familyData?.personalInfo?.maritalStatus)
            }
        } catch (exception: Exception) {
            Log.e("Exception", exception.toString())
        }


        img_profile.setOnClickListener {
            if (!hasPermissions(
                    applicationContext,
                    *Constants.PERMISSIONS
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    Constants.PERMISSIONS,
                    Constants.PERMISSION_ALL
                )
            } else {
                val builder = AlertDialog.Builder(this@AddPersonal)
                builder.setTitle("Profile Pic:")
                    .setMessage(
                        "This is your photo ID, Please upload a formal pic: " + " " + getEmoji(
                            0x1F60A
                        )
                    )
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id ->
                        dialog.dismiss()
                        val permissionResult =
                            checkPermission(this@AddPersonal)
                        if (permissionResult) {
                            val bottomSheetDialog = DialogChoosePhoto.instance
                            bottomSheetDialog.setStyle(
                                DialogFragment.STYLE_NORMAL,
                                R.style.CustomBottomSheetDialogTheme
                            )
                            this@AddPersonal.supportFragmentManager.let { it1 ->
                                bottomSheetDialog.show(
                                    it1,
                                    "Custom Bottom Sheet"
                                )
                            }
                        } else {
                            showErrorMessage(
                                lay_root,
                                "Permission denied, Please grant permission to access camera!"
                            )
                        }
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog, id -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
            }
        }

        edt_rashi.setOnClickListener {
            val rashi: ArrayList<String> = arrayListOf()
            rashi.addAll(resources.getStringArray(R.array.rashi))
            val spinnerDialog = SpinnerDialog(
                this@AddPersonal,
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
                this@AddPersonal,
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
                this@AddPersonal,
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


        edt_gender.setOnClickListener {
            val gender: ArrayList<String> = arrayListOf()
            gender.addAll(resources.getStringArray(R.array.gender))
            val spinnerDialog = SpinnerDialog(
                this@AddPersonal,
                gender,
                "Select gender.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_gender.setText(item)
            }
            spinnerDialog.showSpinerDialog()
        }

        edt_status.setOnClickListener {
            val status: ArrayList<String> = arrayListOf()
            status.addAll(resources.getStringArray(R.array.status))
            val spinnerDialog = SpinnerDialog(
                this@AddPersonal,
                status,
                "Select Marital status.",
                R.style.DialogAnimations_SmileWindow, // For slide animation
                "Cancel"
            )
            spinnerDialog.setCancellable(true) // for cancellable
            spinnerDialog.setShowKeyboard(false)// for open keyboard by default
            spinnerDialog.bindOnSpinerListener { item, position ->
                edt_status.setText(item)
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
                this@AddPersonal,
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

        edt_time.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime[Calendar.HOUR_OF_DAY]
            val minute = currentTime[Calendar.MINUTE]
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this@AddPersonal,
                OnTimeSetListener { _, selectedHour, selectedMinute ->
                    edt_time.setText(
                        "$selectedHour:$selectedMinute"
                    )
                }, hour, minute, true
            )
            mTimePicker.setTitle("")
            mTimePicker.show()
        }

        btn_update.setOnClickListener {
            update()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(
                        this@AddPersonal,
                        "Permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver(fileLocationReceiver)
    }

    private val fileLocationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //TODO extract extras from intent
            if (intent.extras != null) {
                val uri: Uri = intent.getParcelableExtra("file_location")!!
                val file = File(uri.path!!)
                val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
                val part: MultipartBody.Part =
                    MultipartBody.Part.createFormData("dp", file.name, fileReqBody)
//                Log.e("fileLocationReceiver", uri.toString())
//                if (internet?.checkMobileInternetConn(context)!!) {
//                    val uploadProfile = RetrofitClient.instanceClient.updateDp(part)
//                    uploadProfile.enqueue(
//                        RetrofitWithBar(this@AddPersonal, object : Callback<ProfileDto> {
//                            @SuppressLint("SimpleDateFormat")
//                            @RequiresApi(Build.VERSION_CODES.O)
//                            override fun onResponse(
//                                call: Call<ProfileDto>,
//                                response: Response<ProfileDto>
//                            ) {
//                                Log.e("onResponse", response.toString())
//                                if (response.code() == 200) {
//                                    when (response.body()?.status) {
//                                        200 -> {
//                                            showMessage(
//                                                lay_root,
//                                                response.body()!!.message
//                                            )
//                                            Picasso.get()
//                                                .load(
//                                                    getData(
//                                                        "rootPath",
//                                                        this@AddPersonal
//                                                    ) + Enums.Dp.value + response.body()!!.data.dp
//                                                )
//                                                .error(R.drawable.ic_dummy_profile)
//                                                .placeholder(R.drawable.ic_dummy_profile)
//                                                .into(img_profile)
//                                        }
//                                        else -> {
//                                            showErrorMessage(
//                                                lay_root,
//                                                response.message()
//                                            )
//                                        }
//                                    }
//
//                                } else if (response.code() == 422 || response.code() == 400) {
//                                    try {
//                                        val moshi: Moshi = Moshi.Builder().build()
//                                        val adapter: JsonAdapter<ErrorMsgDto> =
//                                            moshi.adapter(ErrorMsgDto::class.java)
//                                        val errorResponse =
//                                            adapter.fromJson(response.errorBody()!!.string())
//                                        if (errorResponse != null) {
//                                            if (errorResponse.status == 400) {
//                                                showErrorMessage(
//                                                    lay_root,
//                                                    errorResponse.message
//                                                )
//                                            } else {
//                                                showErrorMessage(
//                                                    lay_root,
//                                                    errorResponse.message
//                                                )
//                                            }
//
//                                        } else {
//                                            showErrorMessage(
//                                                lay_root,
//                                                getString(R.string.msg_something_wrong)
//                                            )
//                                            Log.e(
//                                                "Response",
//                                                response.body()!!.toString()
//                                            )
//                                        }
//                                    } catch (e: Exception) {
//                                        showErrorMessage(
//                                            lay_root,
//                                            getString(R.string.msg_something_wrong)
//                                        )
//                                        Log.e("Exception", e.toString())
//                                    }
//
//                                } else if (response.code() == 401) {
//                                    sessionExpired(
//                                        this@AddPersonal
//                                    )
//                                } else {
//                                    showErrorMessage(
//                                        lay_root,
//                                        response.message()
//                                    )
//                                }
//                            }
//
//                            override fun onFailure(call: Call<ProfileDto>, t: Throwable) {
//                                Log.e("onResponse", t.message.toString())
//                                showErrorMessage(
//                                    lay_root,
//                                    getString(R.string.msg_something_wrong)
//                                )
//                            }
//                        })
//                    )
//                } else {
//                    showErrorMessage(
//                        lay_root,
//                        getString(R.string.msg_no_internet)
//                    )
//                }
            }
        }
    }

    private fun update() {
        lay_name.error = null
//        lay_sharma.error = null
//        lay_dob.error = null
//        lay_time.error = null
//        lay_place.error = null
//        lay_rashi.error = null
//        lay_nakshathram.error = null
//        lay_padham.error = null
//        lay_city.error = null
//        lay_mobile.error = null
//        lay_gender.error = null
//        lay_email.error = null
//        lay_status.error = null
//        lay_status.error = null

        when {
            edt_name.length() < 3 -> {
                lay_name.error = "AddName's minimum character is 3."
            }
//            edt_sharma.length() < 3 -> {
//                lay_sharma.error = "Sharma's minimum character is 3."
//            }
//            edt_dob.length() < 1 -> {
//                lay_dob.error = "Date of birth is required."
//            }
//            edt_time.length() < 1 -> {
//                lay_time.error = "Time of birth is required."
//            }
//            edt_place.length() < 2 -> {
//                lay_place.error = "Place's minimum character is 2."
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
//            edt_city.length() < 3 -> {
//                lay_city.error = "City's minimum character is 3."
//            }
//            edt_mobile.length() != 10 -> {
//                lay_mobile.error = "Enter the valid mobile number."
//            }
//            edt_gender.length() < 1 -> {
//                lay_gender.error = "Gender is required."
//            }
//            !isValidEmail(edt_email.text) -> {
//                lay_email.error = "Email address is not a valid one."
//            }
//            edt_status.length() < 1 -> {
//                lay_status.error = "Marital status is required."
//            }
            else -> {
//                val map: HashMap<String, HashMap<String, Any>> = HashMap()
//                val mapData: HashMap<String, Any> = HashMap()
//                mapData["name"] = edt_name.text.toString().toLowerCase(Locale.getDefault())
//                mapData["sharma"] = edt_sharma.text.toString().toLowerCase(Locale.getDefault())
//                mapData["dateOfBirth"] = edt_dob.text.toString().toLowerCase(Locale.getDefault())
//                mapData["timeOfBirth"] = edt_time.text.toString().toLowerCase(Locale.getDefault())
//                mapData["placeOfBirth"] = edt_place.text.toString().toLowerCase(Locale.getDefault())
//                mapData["rashi"] = edt_rashi.text.toString().toLowerCase(Locale.getDefault())
//                mapData["nakshathram"] =
//                    edt_nakshathram.text.toString().toLowerCase(Locale.getDefault())
//                mapData["padham"] = edt_padham.text.toString().toLowerCase(Locale.getDefault())
//                mapData["city"] = edt_city.text.toString().toLowerCase(Locale.getDefault())
//                mapData["mobileNumber"] =
//                    edt_mobile.text.toString().toLowerCase(Locale.getDefault())
//                mapData["gender"] = edt_gender.text.toString().toLowerCase(Locale.getDefault())
//                mapData["email"] = edt_email.text.toString().toLowerCase(Locale.getDefault())
//                mapData["maritalStatus"] =
//                    edt_status.text.toString().toLowerCase(Locale.getDefault())
//                map["personalInfo"] = mapData

                val personalInfo = PersonalInfo(
                    name = edt_name.text.toString().toLowerCase(Locale.getDefault()),
                    sharma = edt_sharma.text.toString().toLowerCase(Locale.getDefault()),
                    dateOfBirth = edt_dob.text.toString().toLowerCase(Locale.getDefault()),
                    timeOfBirth = edt_time.text.toString().toLowerCase(Locale.getDefault()),
                    placeOfBirth = edt_time.text.toString().toLowerCase(Locale.getDefault()),
                    rashi = edt_rashi.text.toString().toLowerCase(Locale.getDefault()),
                    nakshathram = edt_nakshathram.text.toString().toLowerCase(Locale.getDefault()),
                    padham = edt_padham.text.toString().toLowerCase(Locale.getDefault()),
                    city = edt_city.text.toString().toLowerCase(Locale.getDefault()),
                    mobileNumber = edt_mobile.text.toString().toLowerCase(Locale.getDefault()),
                    gender = edt_gender.text.toString().toLowerCase(Locale.getDefault()),
                    email = edt_email.text.toString().toLowerCase(Locale.getDefault()),
                    maritalStatus = edt_status.text.toString().toLowerCase(Locale.getDefault())
                )
                personal(personalInfo)
            }
        }
    }

    private fun personal(data: PersonalInfo) {
        if (internet!!.checkMobileInternetConn(this@AddPersonal)) {
            try {
                Log.e("body", data.toString())
                val familyCreate = RetrofitClient.instanceClient.personal(data)
                familyCreate.enqueue(
                    RetrofitWithBar(this@AddPersonal, object : Callback<FamilyDto> {
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
                                    this@AddPersonal
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
}