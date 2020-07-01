package com.swadharmaa.discover

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.swadharmaa.Home
import com.swadharmaa.R
import com.swadharmaa.book.*
import com.swadharmaa.category.CategoryAdapter
import com.swadharmaa.category.CategoryData
import com.swadharmaa.category.CategoryListDto
import com.swadharmaa.general.GridSpacingItemDecoration
import com.swadharmaa.general.reloadActivity
import com.swadharmaa.general.sessionExpired
import com.swadharmaa.general.showErrorMessage
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.user.ErrorMsgDto
import kotlinx.android.synthetic.main.act_search.*
import kotlinx.android.synthetic.main.act_see_all.*
import kotlinx.android.synthetic.main.act_see_all.lay_data
import kotlinx.android.synthetic.main.act_see_all.lay_no_data
import kotlinx.android.synthetic.main.act_see_all.lay_no_internet
import kotlinx.android.synthetic.main.act_see_all.lay_shimmer
import kotlinx.android.synthetic.main.act_see_all.layout_refresh
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.im_back
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SeeAll : AppCompatActivity() {

    private var internet: InternetDetector? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private var loadCategory: Call<CategoryListDto>? = null
    private var loadBooks: Call<HomeDto>? = null
    private var loadBookByCategory: Call<BookListDto>? = null
    var categoryData: MutableList<CategoryData> = arrayListOf()
    var homeData: MutableList<HomeBookData> = arrayListOf()
    var bookData: MutableList<BookData> = arrayListOf()

    val spanCount = 3 //  columns
    val spacing = 15 // pixel
    val includeEdge = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_see_all)

        txt_edit.visibility = View.GONE
        layout_refresh.setOnRefreshListener {
            finish()
            reloadActivity(this@SeeAll)
            layout_refresh.isRefreshing = false
        }

        im_back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.bottom_up, R.anim.nothing)
        }

        internet = InternetDetector.getInstance(this@SeeAll)

        try {
            val data = intent.getStringExtra(getString(R.string.data))
            if (data != null) {
                when (data) {
                    getString(R.string.loadAllCategory) -> {
                        if (!lay_shimmer.isShimmerStarted) {
                            lay_shimmer.startShimmer()
                        }
                        loadCategory()
                    }
                    getString(R.string.loadBooksByGenre) -> {
                        if (!lay_shimmer.isShimmerStarted) {
                            lay_shimmer.startShimmer()
                        }
                        val position = intent.getIntExtra(getString(R.string.position), 0)
                        loadBooks(position)
                    }

                    getString(R.string.loadBooksByCategory) -> {
                        if (!lay_shimmer.isShimmerStarted) {
                            lay_shimmer.startShimmer()
                        }
                        val title = intent.getStringExtra(getString(R.string.name))
                        val id = intent.getStringExtra(getString(R.string.id))
                        loadBookByCategory(title = title, id = id)
                    }
                }
            }
        } catch (e: Exception) {
            println("Timer Ex: $e")
        }
    }

    override fun onStop() {
        super.onStop()
        if (loadCategory != null) {
            loadCategory?.cancel()
        }
        if (loadBooks != null) {
            loadBooks?.cancel()
        }
        if (loadBookByCategory != null) {
            loadBookByCategory?.cancel()
        }
    }

    private fun loadCategory() {
        if (internet?.checkMobileInternetConn(applicationContext)!!) {
            loadCategory = RetrofitClient.instanceClient.category()
            loadCategory?.enqueue(object : Callback<CategoryListDto> {
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
                                    txt_title.text = getString(R.string.category)
                                    categoryData = response.body()!!.data.toMutableList()
                                    lay_no_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                    lay_data.visibility = View.VISIBLE
                                    view_more?.apply {
                                        view_more?.layoutManager = GridLayoutManager(
                                            applicationContext,
                                            spanCount,
                                            GridLayoutManager.VERTICAL,
                                            false
                                        )
                                        view_more?.addItemDecoration(
                                            GridSpacingItemDecoration(
                                                spanCount,
                                                spacing,
                                                includeEdge
                                            )
                                        )
                                        view_more?.setHasFixedSize(true)
                                        val categoryAdapter =
                                            CategoryAdapter(categoryData, this@SeeAll)
                                        view_more?.adapter = categoryAdapter
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
                            sessionExpired(this@SeeAll)
                        }
                        else -> {
                            showErrorMessage(
                                layout_refresh,
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
                        showErrorMessage(
                            layout_refresh,
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

    private fun loadBooks(position: Int) {
        if (internet?.checkMobileInternetConn(applicationContext)!!) {
            loadBooks = RetrofitClient.instanceClient.getHome()
            loadBooks?.enqueue(object : Callback<HomeDto> {
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
                                    txt_title.text = response.body()!!.data[position].genre
                                    homeData =
                                        response.body()!!.data[position].books.toMutableList()
                                    lay_no_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                    lay_data.visibility = View.VISIBLE
                                    view_more?.apply {
                                        view_more?.layoutManager = GridLayoutManager(
                                            applicationContext,
                                            spanCount,
                                            GridLayoutManager.VERTICAL,
                                            false
                                        )
                                        view_more?.addItemDecoration(
                                            GridSpacingItemDecoration(
                                                spanCount,
                                                spacing,
                                                includeEdge
                                            )
                                        )
                                        view_more?.setHasFixedSize(true)
                                        val bookChildAdapter =
                                            BookChildAdapter(homeData, this@SeeAll)
                                        view_more?.adapter = bookChildAdapter
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
                            sessionExpired(this@SeeAll)
                        }
                        else -> {
                            showErrorMessage(
                                layout_refresh,
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
                        showErrorMessage(
                            layout_refresh,
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

    private fun loadBookByCategory(title: String, id: String) {
        if (internet?.checkMobileInternetConn(applicationContext)!!) {
            loadBookByCategory = RetrofitClient.instanceClient.getBookByCategory(id)
            loadBookByCategory?.enqueue(object : Callback<BookListDto> {
                @SuppressLint("DefaultLocale", "SetTextI18n")
                override fun onResponse(
                    call: Call<BookListDto>,
                    response: Response<BookListDto>
                ) {
                    Log.e("onResponse", response.toString())
                    when {
                        response.code() == 200 -> {
                            when (response.body()?.status) {
                                200 -> {
                                    txt_title.text = title
                                    bookData = response.body()!!.data.toMutableList()
                                    lay_no_data.visibility = View.GONE
                                    lay_no_internet.visibility = View.GONE
                                    lay_data.visibility = View.VISIBLE
                                    view_more?.apply {
                                        view_more?.layoutManager = GridLayoutManager(
                                            applicationContext,
                                            spanCount,
                                            GridLayoutManager.VERTICAL,
                                            false
                                        )
                                        view_more?.addItemDecoration(
                                            GridSpacingItemDecoration(
                                                spanCount,
                                                spacing,
                                                includeEdge
                                            )
                                        )
                                        view_more?.setHasFixedSize(true)
                                        val searchAdapter =
                                            SearchAdapter(bookData, this@SeeAll)
                                        view_more?.adapter = searchAdapter
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
                            sessionExpired(this@SeeAll)
                        }
                        else -> {
                            showErrorMessage(
                                layout_refresh,
                                response.message()
                            )
                        }
                    }
                    lay_shimmer.visibility = View.GONE
                    lay_shimmer.stopShimmer()
                }

                override fun onFailure(call: Call<BookListDto>, t: Throwable) {
                    Log.e("onFailure", t.message.toString())
                    if (!call.isCanceled) {
                        showErrorMessage(
                            layout_refresh,
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

}
