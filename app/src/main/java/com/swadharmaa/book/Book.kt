package com.swadharmaa.book

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import com.swadharmaa.R
import com.swadharmaa.general.*
import com.swadharmaa.reader.Reader
import com.swadharmaa.server.InternetDetector
import com.swadharmaa.server.RetrofitClient
import com.swadharmaa.server.RetrofitWithBar
import com.swadharmaa.user.ErrorMsgDto
import com.swadharmaa.user.Login
import com.swadharmaa.user.ResDto
import kotlinx.android.synthetic.main.act_book.*
import kotlinx.android.synthetic.main.toolbar.txt_title
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Book : AppCompatActivity() {

    private var internet: InternetDetector? = null
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private var favorite = true
    private var books: Call<BookDto>? = null
    var isLoggedIn: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_book)

        val window: Window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }

        layout_refresh.setOnRefreshListener {
            finish()
            reloadActivity(this@Book)
            layout_refresh.isRefreshing = false
        }

        internet = InternetDetector.getInstance(this@Book)
        isLoggedIn = getData("logged_user", applicationContext).toString()

        try {
            val data = intent.getStringExtra("data")
            if (data != null) {
                val book = SingleBook(
                    libraryId = data,
                    userId = getData("user_id", applicationContext).toString()
                )
                books(book)
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            showMessage(lay_root, getString(R.string.unable_to_fetch))
        }
    }

    private fun books(book: SingleBook) {
        Log.e("book", book.toString())
        books = RetrofitClient.instanceClientWithoutToken.getOneBook(book)
        books?.enqueue(object : Callback<BookDto> {
            @SuppressLint("DefaultLocale", "SetTextI18n")
            override fun onResponse(
                call: Call<BookDto>,
                response: Response<BookDto>
            ) {
                Log.e("onResponse", response.toString())
                when {
                    response.code() == 200 -> {
                        when (response.body()?.status) {
                            200 -> {
                                txt_title.text = response.body()!!.data.name
                                txt_author.text = response.body()!!.data.author
                                Picasso.get()
                                    .load(
                                        getData(
                                            "rootPath",
                                            this@Book
                                        ) + Enums.Book.value + response.body()!!.data.thumbnail
                                    )
                                    .error(R.drawable.img_placeholder)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_book)

                                val isBookmark = response.body()!!.data.isBookmark
                                if (isBookmark != null) {
                                    if (response.body()!!.data.isBookmark!!) {
                                        img_bookmark.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                this@Book,
                                                R.drawable.ic_heart_fill
                                            )
                                        )
                                        favorite = false
                                    }
                                }

                                img_bookmark.setOnClickListener {
                                    if (isLoggedIn == getString(R.string.skip)) {
                                        val intent = Intent(this@Book, Login::class.java)
                                        intent.putExtra(
                                            getString(R.string.data),
                                            getString(R.string.new_user)
                                        )
                                        startActivity(intent)
                                    } else {
                                        if (favorite) {
                                            img_bookmark.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    this@Book,
                                                    R.drawable.ic_heart_fill
                                                )
                                            )
                                            addFav(response.body()!!.data._id.toString())
                                            favorite = false
                                        } else {
                                            img_bookmark.setImageDrawable(
                                                ContextCompat.getDrawable(
                                                    this@Book,
                                                    R.drawable.ic_heart
                                                )
                                            )
                                            removeFav(response.body()!!.data._id.toString())
                                            favorite = true
                                        }
                                    }
                                }

                                btn_preview.setOnClickListener {
                                    val intent = Intent(this@Book, Reader::class.java)
                                    intent.putExtra(
                                        this@Book.getString(R.string.data),
                                        response.body()!!.data.content
                                    )
                                    startActivity(intent)
                                }
                            }
                            else -> {
                                showErrorMessage(
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

                    }

                    response.code() == 401 -> {
                        sessionExpired(this@Book)
                    }
                    else -> {
                        showErrorMessage(
                            lay_root,
                            response.message()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<BookDto>, t: Throwable) {
                Log.e("onFailure", t.message.toString())
                if (!call.isCanceled) {
                    showErrorMessage(
                        lay_root,
                        getString(R.string.msg_something_wrong)
                    )
                }
            }
        })
    }

    private fun addFav(id: String) {
        if (internet?.checkMobileInternetConn(applicationContext)!!) {
            val addFav = RetrofitClient.instanceClient.addFavourite(id)
            addFav.enqueue(
                RetrofitWithBar(this@Book, object : Callback<ResDto> {
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
                                this@Book
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

        } else {
            showErrorMessage(
                lay_root,
                getString(R.string.msg_no_internet)
            )
        }
    }

    private fun removeFav(id: String) {
        if (internet?.checkMobileInternetConn(applicationContext)!!) {
            val removeFav = RetrofitClient.instanceClient.removeFavourite(id)
            removeFav.enqueue(
                RetrofitWithBar(this@Book, object : Callback<ResDto> {
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
                                this@Book
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

        } else {
            showErrorMessage(
                lay_root,
                getString(R.string.msg_no_internet)
            )
        }
    }
}
