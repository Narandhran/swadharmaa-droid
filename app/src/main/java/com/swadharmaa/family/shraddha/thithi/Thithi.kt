package com.swadharmaa.family.shraddha.thithi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.R
import com.swadharmaa.family.FamilyDto
import com.swadharmaa.general.reloadFragment
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.interfaces.IOnBackPressed
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.user.ErrorMsgDto
import kotlinx.android.synthetic.main.frag_thithi.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Thithi : Fragment(), IOnBackPressed {

    private var familyDto: Call<FamilyDto>? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var internetDetector: InternetDetector? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_thithi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        internetDetector = InternetDetector.getInstance(requireContext())
        layout_refresh.setOnRefreshListener {
            reloadFragment(
                activity?.supportFragmentManager!!,
                this@Thithi
            )
            layout_refresh.isRefreshing = false
        }

        btn_add.setOnClickListener {
            startActivity(Intent(requireActivity(), AddThithi::class.java))
        }

        btn_addmore.setOnClickListener {
            startActivity(Intent(requireActivity(), AddThithi::class.java))
        }

        family()
    }

    private fun family() {
        if (internetDetector?.checkMobileInternetConn(requireContext())!!) {
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
                                    lay_data.visibility = View.VISIBLE

                                    val thihiList: MutableList<com.swadharmaa.family.Thithi> =
                                        response.body()!!.data?.shraardhaInfo!!.thithi!!.toMutableList()

                                    if (thihiList.isNotEmpty()) {
                                        view_thithi?.apply {
                                            view_thithi?.layoutManager = LinearLayoutManager(
                                                requireContext(),
                                                LinearLayoutManager.VERTICAL,
                                                false
                                            )
                                            view_thithi?.setHasFixedSize(true)
                                            val thithiAdapter =
                                                ThithiAdapter(
                                                    thihiList,
                                                    requireActivity()
                                                )
                                            view_thithi?.adapter = thithiAdapter
                                        }
                                    } else {
                                        lay_no_data.visibility = View.VISIBLE
                                        lay_data.visibility = View.GONE
                                        lay_no_internet.visibility = View.GONE
                                    }
                                }
                                204 -> {
                                    lay_no_data.visibility = View.VISIBLE
                                    lay_data.visibility = View.GONE
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
                            sessionExpired(requireActivity())
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
            lay_data.visibility = View.GONE
            lay_no_internet.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        if (familyDto != null) {
            familyDto?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object {
        fun newInstance(): Thithi =
            Thithi()
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}

