package com.phoenix.booklet.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.phoenix.booklet.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(Constants.PREFS_SETTING)

class DataStoreManager(private val context: Context) {

    companion object {
        val UPDATE_AVAILABLE = booleanPreferencesKey(Constants.SETTING_UPDATE_AVAILABLE)
        val UPDATE_VERSION = stringPreferencesKey(Constants.SETTING_UPDATE_VERSION)
        val GRID_LAYOUT = booleanPreferencesKey(Constants.SETTING_GRID_LAYOUT)
    }

    suspend fun setUpdateState(available: Boolean, version: String?) {
        context.dataStore.edit { prefs ->
            prefs[UPDATE_AVAILABLE] = available
            if (version != null)
                prefs[UPDATE_VERSION] = version
        }
    }

    val isUpdateAvailable: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[UPDATE_AVAILABLE] ?: false }

    val nextUpdateVersion: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[UPDATE_VERSION] ?: "" }

    suspend fun setGridLayout(isGrid: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[GRID_LAYOUT] = isGrid
        }
    }

    val isGridLayout: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[GRID_LAYOUT] ?: false }

}