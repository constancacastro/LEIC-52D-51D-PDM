package com.example.chelasreversi.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.preferences.domain.UserInfoRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * A user information repository implementation supported in shared preferences
 */
open class UserInfoRepository(private val dataStore: DataStore<Preferences>) : UserInfoRepository {

    private val userNickKey = stringPreferencesKey("Nickname")

    override var userInfo: UserInfo?
        get() = runBlocking {
            dataStore.data.map { preferences ->
                preferences[userNickKey]?.let { UserInfo(it) }
            }.firstOrNull()
        }
        set(value) {
            runBlocking {
                dataStore.edit { preferences ->
                    if (value == null) {
                        preferences.remove(userNickKey)
                    } else {
                        preferences[userNickKey] = value.nickname
                    }
                }
            }
        }
}