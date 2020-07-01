package com.swadharmaa.family.familytree

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.R
import com.swadharmaa.family.FamilyTreeData
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.general.reloadActivity
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.user.ErrorMsgDto
import kotlinx.android.synthetic.main.act_family_tree.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FamilyTree : AppCompatActivity() {

    private var familyDto: Call<FamilyDto>? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internetDetector: InternetDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_family_tree)
        txt_title.text = getString(R.string.family_tree)
        txt_edit.text = getString(R.string.add)
        layout_refresh.setOnRefreshListener {
            reloadActivity(this@FamilyTree)
            layout_refresh.isRefreshing = false
        }
        im_back.setOnClickListener {
            onBackPressed()
        }

        family()
        btn_add.setOnClickListener {
            startActivity(Intent(this@FamilyTree, AddFamilyTree::class.java))
        }

        txt_edit.setOnClickListener {
            startActivity(Intent(this@FamilyTree, AddFamilyTree::class.java))
        }
    }

    private fun family() {
        internetDetector = InternetDetector.getInstance(this@FamilyTree)
        if (internetDetector?.checkMobileInternetConn(applicationContext)!!) {
            familyDto = RetrofitClient.instanceClient.listOfFamily()
            familyDto?.enqueue(object : Callback<FamilyDto> {
                @SuppressLint("DefaultLocale", "SetTextI18n")
                override fun onResponse(
                    call: Call<FamilyDto>,
                    response: Response<FamilyDto>
                ) {
                    Log.e("onResponse", response.toString())
                    when {
                        response.code() == 200 -> {
                            when (response.body()?.status) {
                                200 -> {
                                    lay_no_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                    view_familytree.visibility = View.VISIBLE

                                    val familyTreeList: MutableList<FamilyTreeData> =
                                        response.body()!!.data?.familyTree!!.toMutableList()

                                    if (familyTreeList.isNotEmpty()) {
                                        view_familytree?.apply {
                                            view_familytree?.layoutManager = LinearLayoutManager(
                                                applicationContext,
                                                LinearLayoutManager.VERTICAL,
                                                false
                                            )
                                            view_familytree?.setHasFixedSize(true)
                                            val familyTreeAdapter =
                                                FamilyTreeAdapter(
                                                    familyTreeList,
                                                    this@FamilyTree
                                                )
                                            view_familytree?.adapter = familyTreeAdapter
                                        }
                                    } else {
                                        lay_no_data.visibility = View.VISIBLE
                                        view_familytree.visibility = View.GONE
                                        lay_no_internet.visibility = View.GONE
                                    }
                                }
                                204 -> {
                                    lay_no_data.visibility = View.VISIBLE
                                    view_familytree.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                }
                                else -> {
                                    showErrorMessage(
                                        layout_refresh,
                                        response.message()
                                    )
                                }
                            }
                        }

                        response.code() == 422 || response.code() == 400 -> {
                            try {
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
                            sessionExpired(this@FamilyTree)
                        }
                        else -> {
                            showErrorMessage(
                                layout_refresh,
                                response.message()
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<FamilyDto>, t: Throwable) {
                    Log.e("onFailure", t.message.toString())
                    if (!call.isCanceled) {
                        showErrorMessage(
                            layout_refresh,
                            getString(R.string.msg_something_wrong)
                        )
                    }
                }
            })

        } else {
            lay_no_data.visibility = View.GONE
            view_familytree.visibility = View.GONE
            lay_no_internet.visibility = View.VISIBLE
        }
    }

    override fun onRestart() {
        super.onRestart()
        family()
    }

    override fun onStop() {
        if (familyDto != null) {
            familyDto?.cancel()
        }
        super.onStop()
    }
}