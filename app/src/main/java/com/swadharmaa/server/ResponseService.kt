package com.swadharmaa.server

import com.swadharmaa.book.BookDto
import com.swadharmaa.book.BookListDto
import com.swadharmaa.book.HomeDto
import com.swadharmaa.book.SingleBook
import com.swadharmaa.category.CategoryDto
import com.swadharmaa.category.CategoryListDto
import com.swadharmaa.category.GenreDto
import com.swadharmaa.family.*
import com.swadharmaa.favourite.FavListDto
import com.swadharmaa.user.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ResponseService {

    @FormUrlEncoded
    @POST("user/register")
    fun register(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResDto>

    @GET("user/requestOtp/{number}")
    fun requestOtp(@Path("number") id: String): Call<ResDto>

    @POST("user/login")
    fun login(@Body loginBody: LoginBody): Call<LoginDto>

    @GET("user/my_profile")
    fun profile(): Call<ProDto>

    @Multipart
    @PUT("user/update_dp")
    fun updateDp(@Part dp: MultipartBody.Part): Call<ProfileDto>

    @FormUrlEncoded
    @PUT("user/update_profile")
    fun updateProfile(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResDto>

    @GET("category/list")
    fun category(): Call<CategoryListDto>

    @Multipart
    @POST("category/create")
    fun addCategory(
        @Part category: MultipartBody.Part,
        @Part("textField") text: RequestBody
    ): Call<CategoryDto>

    @FormUrlEncoded
    @POST("genre/add")
    fun addGenre(@FieldMap params: Map<String, @JvmSuppressWildcards Any>): Call<ResDto>

    @GET("genre/list")
    fun genres(): Call<GenreDto>

    @Multipart
    @POST("library/create")
    fun addBook(
        @Part thumbnail: MultipartBody.Part,
        @Part pdf: MultipartBody.Part,
        @Part("textField") text: RequestBody
    ): Call<ResDto>

    @GET("library/homepage")
    fun getHome(): Call<HomeDto>

    @GET("library/list")
    fun getAllBooks(): Call<BookListDto>

    @GET("library/search/{value}")
    fun searchBooks(@Path("value") value: String): Call<BookListDto>

    @GET("library/list_category/{id}")
    fun getBookByCategory(@Path("id") id: String): Call<BookListDto>

    @POST("library/get_one")
    fun getOneBook(@Body book: SingleBook): Call<BookDto>

    @POST("fav/add/{id}")
    fun addFavourite(@Path("id") id: String): Call<ResDto>

    @DELETE("fav/remove/{id}")
    fun removeFavourite(@Path("id") id: String): Call<ResDto>

    @GET("fav/list")
    fun listFavourite(): Call<FavListDto>

    @GET("family/list_by_user")
    fun listOfFamily(): Call<FamilyDto>

    @POST("family/create_update_personal_info")  // To create or update personal info
    fun personal(@Body personalInfo: PersonalInfo): Call<FamilyDto>

    @POST("family/create_family_tree")  // To create family tree
    fun familyTree(@Body bodyFamilyTree: FamilyBody): Call<FamilyDto>

    @POST("family/update_family_tree/{id}")  // To update family tree
    fun familyTree(@Path("id") id: String, @Body bodyFamilyTree: FamilyBody): Call<ResDto>

    @POST("family/create_update_family_info")  // To create or update family info
    fun family(@Body familyInfo: FamilyInfo): Call<FamilyDto>

    @POST("family/create_update_gothram")  // To create or update gothram
    fun gothram(@Body gothram: Gothram): Call<FamilyDto>

    @POST("family/create_thithi")  // To create thithi
    fun thithi(@Body thithiData: ThithiData): Call<FamilyDto>

    @POST("family/update_thithi/{id}")  // To  update thithi
    fun thithi(@Path("id") id: String, @Body thithiData: ThithiData): Call<ResDto>

    @POST("family/create_update_name")  // To create or update name
    fun name(@Body name: Name): Call<FamilyDto>

    @POST("family/create_update_samayal")  // To create or update samayal
    fun samayal(@Body samayal: Samayal): Call<FamilyDto>

    @POST("family/create_update_vazhkam")  // To create or update vazhakkam
    fun vazhakkam(@Body vazhakkam: Vazhakkam): Call<FamilyDto>

}