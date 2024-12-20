package com.example.chelasreversi.preferences.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(val nickname: String) : Parcelable {
    fun validate() {
        if (nickname.isBlank() || nickname.length > 14) {
            throw IllegalArgumentException("Invalid nickname")
        }
    }
}

/*fun userInfoOrNull(nickname: String): UserInfo? =
    if (validateUserInfoParts(nickname)) {
        UserInfo(nickname)
    } else {
        null
    }

fun validateUserInfoParts(nickname: String): Boolean {
    return nickname.isNotBlank() && nickname.length <= 20
}*/