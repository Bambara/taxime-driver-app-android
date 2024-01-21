package com.ynr.taximedriver.model

data class UpdateDriverRequest (
    val driverId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val nic: String,
    val birthday: String,
    val accNumber: String,
    val mobile: String,
    val gender: String,
    val city: String,
    val district: String,
    val province: String,
    val country: String,
)
