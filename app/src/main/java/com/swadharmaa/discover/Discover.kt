package com.swadharmaa.discover

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import com.swadharmaa.R
import com.swadharmaa.book.BookParentAdapter
import com.swadharmaa.book.HomeData
import com.swadharmaa.book.HomeDto
import com.swadharmaa.category.CategoryAdapter
import com.swadharmaa.category.CategoryData
import com.swadharmaa.category.CategoryListDto
import com.swadharmaa.general.*
import com.swadharmaa.interfaces.IOnBackPressed
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.user.ErrorMsgDto
import com.swadharmaa.user.Login
import com.swadharmaa.user.ProDto
import com.swadharmaa.user.Profile
import kotlinx.android.synthetic.main.act_discover.*
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Discover : Fragment(), IOnBackPressed {

    private var profile: Call<ProDto>? = null
    private var category: Call<CategoryListDto>? = null
    private var books: Call<HomeDto>? = null
    var internetDetector: InternetDetector? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    var categoryData: MutableList<CategoryData> = arrayListOf()
    var homeData: MutableList<HomeData> = arrayListOf()

    val handler = Handler()
    private var timer: Timer? = Timer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.act_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        showMessage(layout_refresh,"This is the test crashlytics")

        layout_refresh.setOnRefreshListener {
            reloadFragment(
                activity?.supportFragmentManager!!,
                this@Discover
            )
            layout_refresh.isRefreshing = false
        }

        if (!lay_shimmer.isShimmerStarted) {
            lay_shimmer.startShimmer()
        }

        internetDetector = InternetDetector.getInstance(activity!!)

        img_profile.setOnClickListener {
            val isLoggedIn = getData("logged_user", requireContext())
            if (isLoggedIn == getString(R.string.skip)) {
                val intent = Intent(requireActivity(), Login::class.java)
                intent.putExtra(getString(R.string.data), getString(R.string.new_user))
                requireActivity().startActivity(intent)
            } else {
                requireActivity().startActivity(Intent(requireActivity(), Profile::class.java))
            }
        }

        val adPic = listOf(
            R.drawable.im_add_one,
            R.drawable.im_add_two,
            R.drawable.im_add_three
        )
        view_pager.adapter = ViewPagerAdapter(
            false, requireContext(),
            adPic.toMutableList()
        )
        worm_dots_indicator.setViewPager(view_pager)

        try {
            var currentPage = 0
            val update = Runnable {
                if (currentPage == adPic.size) {
                    currentPage = 0
                }
                view_pager.setCurrentItem(currentPage++, true)
            }

            timer?.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            }, 1000, 3000)
        } catch (exception: java.lang.Exception) {
            Log.e("Exception", exception.message.toString())
        }

        category()
        books()
        loadProfileInfo()

        btn_seeall.setOnClickListener {
            val intent = Intent(requireActivity(), SeeAll::class.java)
            intent.putExtra(getString(R.string.data), getString(R.string.loadAllCategory))
            startActivity(intent)
        }

        txt_search.setOnClickListener {
            val intent = Intent(requireActivity(), Search::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.bottom_up, R.anim.nothing)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        } catch (e: Exception) {
            println("Timer Ex: $e")
        }

        if (profile != null) {
            profile?.cancel()
        }
        if (category != null) {
            category?.cancel()
        }
        if (books != null) {
            books?.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            timer!!.cancel()
            timer!!.purge()
            timer = null
        } catch (e: Exception) {
            println("Timer Ex: $e")
        }

        if (profile != null) {
            profile?.cancel()
        }
        if (category != null) {
            category?.cancel()
        }
        if (books != null) {
            books?.cancel()
        }
    }

    companion object {
        fun newInstance(): Discover =
            Discover()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    private fun category() {
        if (internetDetector?.checkMobileInternetConn(activity!!)!!) {
            category = RetrofitClient.instanceClient.category()
            category?.enqueue(object : Callback<CategoryListDto> {
                @SuppressLint("DefaultLocale", "SetTextI18n")
                override fun onResponse(
                    call: Call<CategoryListDto>,
                    response: Response<CategoryListDto>
                ) {
                    Log.e("onResponse", response.toString())
                    when {
                        response.code() == 200 -> {
                            when (response.body()?.status) {
                                200 -> {
                                    categoryData = response.body()!!.data.toMutableList()
                                    lay_no_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                    lay_data.visibility = View.VISIBLE
                                    view_category?.apply {
                                        view_category?.layoutManager =
                                            LinearLayoutManager(
                                                requireContext(),
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                        view_category?.setHasFixedSize(true)
                                        val categoryAdapter =
                                            CategoryAdapter(categoryData, requireActivity())
                                        view_category?.adapter = categoryAdapter
                                    }
                                }
                                204 -> {
                                    lay_no_data.visibility = View.VISIBLE
                                    lay_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                }
                                else -> {
                                    coordinatorErrorMessage(
                                        lay_root,
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
                                        coordinatorErrorMessage(
                                            lay_root,
                                            errorResponse.message
                                        )
                                    } else {
                                        coordinatorErrorMessage(
                                            lay_root,
                                            errorResponse.message
                                        )
                                    }

                                } else {
                                    coordinatorErrorMessage(
                                        lay_root,
                                        getString(R.string.msg_something_wrong)
                                    )
                                    Log.e(
                                        "Response",
                                        response.body()!!.toString()
                                    )
                                }
                            } catch (e: Exception) {
                                coordinatorErrorMessage(
                                    lay_root,
                                    getString(R.string.msg_something_wrong)
                                )
                                Log.e("Exception", e.toString())
                            }

                        }

                        response.code() == 401 -> {
                            sessionExpired(activity!!)
                        }
                        else -> {
                            coordinatorErrorMessage(
                                lay_root,
                                response.message()
                            )
                        }
                    }
                    lay_shimmer.visibility = View.GONE
                    lay_shimmer.stopShimmer()
                }

                override fun onFailure(call: Call<CategoryListDto>, t: Throwable) {
                    Log.e("onFailure", t.message.toString())
                    if (!call.isCanceled) {
                        coordinatorErrorMessage(
                            lay_root,
                            getString(R.string.msg_something_wrong)
                        )
                        lay_shimmer.visibility = View.GONE
                        lay_shimmer.stopShimmer()
                    }
                }
            })

        } else {
            lay_shimmer.visibility = View.GONE
            lay_shimmer.stopShimmer()
            lay_no_data.visibility = View.GONE
            lay_data.visibility = View.GONE
            lay_no_internet.visibility = View.VISIBLE
        }
    }

    private fun books() {
        books = RetrofitClient.instanceClientWithoutToken.getHome()
        books?.enqueue(object : Callback<HomeDto> {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onResponse(
                call: Call<HomeDto>,
                response: Response<HomeDto>
            ) {
                Log.e("onResponse", response.toString())
                when {
                    response.code() == 200 -> {
                        when (response.body()?.status) {
                            200 -> {
                                homeData = response.body()!!.data.toMutableList()
                                view_book_parent?.apply {
                                    view_book_parent?.layoutManager =
                                        LinearLayoutManager(
                                            requireContext(),
                                            LinearLayoutManager.VERTICAL,
                                            false
                                        )
                                    view_book_parent?.setHasFixedSize(true)
                                    val bookParentAdapter =
                                        BookParentAdapter(homeData, requireActivity())
                                    view_book_parent?.adapter = bookParentAdapter
                                }
                            }
                            204 -> {
                                saveData(
                                    "default_address",
                                    "false",
                                    activity!!
                                )
                            }
                            else -> {
                                coordinatorErrorMessage(
                                    lay_root,
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
                                    coordinatorErrorMessage(
                                        lay_root,
                                        errorResponse.message
                                    )
                                } else {
                                    coordinatorErrorMessage(
                                        lay_root,
                                        errorResponse.message
                                    )
                                }

                            } else {
                                coordinatorErrorMessage(
                                    lay_root,
                                    getString(R.string.msg_something_wrong)
                                )
                                Log.e(
                                    "Response",
                                    response.body()!!.toString()
                                )
                            }
                        } catch (e: Exception) {
                            coordinatorErrorMessage(
                                lay_root,
                                getString(R.string.msg_something_wrong)
                            )
                            Log.e("Exception", e.toString())
                        }

                    }

                    response.code() == 401 -> {
                        sessionExpired(activity!!)
                    }
                    else -> {
                        coordinatorErrorMessage(
                            lay_root,
                            response.message()
                        )
                    }
                }
                lay_shimmer.visibility = View.GONE
                lay_shimmer.stopShimmer()
            }

            override fun onFailure(call: Call<HomeDto>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
                if (!call.isCanceled) {
                    coordinatorErrorMessage(
                        lay_root,
                        getString(R.string.msg_something_wrong)
                    )
                    lay_shimmer.visibility = View.GONE
                    lay_shimmer.stopShimmer()
                }
            }
        })
    }

    private fun loadProfileInfo() {
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
                                    saveData(
                                        "fullname",
                                        StringUtils.capitalize(response.body()?.data?.fname?.toLowerCase()) + " " + StringUtils.capitalize(
                                            response.body()?.data?.lname?.toLowerCase()
                                        ),
                                        requireContext()
                                    )
                                    saveData(
                                        "mobile",
                                        response.body()?.data?.mobile.toString(),
                                        requireContext()
                                    )
                                    saveData(
                                        "username",
                                        response.body()?.data?.email.toString(),
                                        requireContext()
                                    )
                                    saveData(
                                        "dp",
                                        response.body()?.data?.dp.toString(),
                                        requireContext()
                                    )
                                    saveData(
                                        "user_id",
                                        response.body()?.data?._id.toString(),
                                        requireContext()
                                    )

                                    Picasso.get()
                                        .load(
                                            getData(
                                                "rootPath",
                                                requireContext()
                                            ) + Enums.Dp.value + response.body()?.data?.dp
                                        )
                                        .error(R.drawable.ic_dummy_profile)
                                        .placeholder(R.drawable.ic_dummy_profile)
                                        .into(img_profile)
                                } catch (e: Exception) {
                                    Log.d("Profile", e.toString())
                                    e.printStackTrace()
                                }
                            }
                            else -> {
                                Log.e("Response", response.message())
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
                                    Log.e("Response", errorResponse.message)
                                } else {
                                    Log.e("Response", errorResponse.message)
                                }

                            } else {
                                Log.e("Response", response.body()!!.toString())
                            }
                        } catch (e: Exception) {
                            Log.e("Exception", e.toString())
                        }
                    }
                    response.code() == 401 -> {
                        sessionExpired(requireActivity())
                    }
                    else -> {
                        Log.e("Response", response.message().toString())
                    }
                }
            }

            override fun onFailure(call: Call<ProDto>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
            }
        })
    }

}