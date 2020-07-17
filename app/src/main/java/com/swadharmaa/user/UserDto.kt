package com.swadharmaa.user

import com.squareup.moshi.JsonClass

// Login
data class LoginBody(
    var mobile: String,
    var otp: Int
)

data class LoginDto(
    var contentFound: Boolean,
    var `data`: LoginData,
    var message: String,
    var status: Int
)

data class LoginData(
    var rpath: String,
    var token: String,
    var role: String
)

// Registration
data class ResDto(
    var contentFound: Boolean,
    var `data`: Any,
    var message: String,
    var status: Int
)

// for error handling
@JsonClass(generateAdapter = true)
data class ErrorMsgDto(
    var error: String,
    var message: String,
    var status: Int
)

// for get profile
data class ProDto(
    var contentFound: Boolean,
    var `data`: ProData,
    var message: String,
    var status: Int
)

data class ProListDto(
    var contentFound: Boolean,
    var `data`: List<ProData>,
    var message: String,
    var status: Int
)

data class ProData(
    var _id: String,
    var dp: Any?,
    var email: String,
    var fname: String,
    var lname: String,
    var mobile: String
)

// for get update profile
data class ProfileDto(
    var contentFound: Boolean,
    var `data`: ProfileData,
    var message: String,
    var status: Int
)

data class ProfileData(
    var __v: Int,
    var _id: String,
    var active: Boolean,
    var createdAt: String,
    var dp: String,
    var email: String,
    var fname: String,
    var lname: String,
    var mobile: String,
    var role: String,
    var updatedAt: String,
    var verify: Verify
)

data class Verify(
    var expire: String,
    var otp: Int
)