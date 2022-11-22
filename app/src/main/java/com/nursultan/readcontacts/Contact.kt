package com.nursultan.readcontacts

data class Contact(
    val id : Int,
    val name: String,
    val phoneNumber: String?,
    val hasPhoneNumber: Boolean
)
