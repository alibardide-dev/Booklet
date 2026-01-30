package com.phoenix.booklet.utils

import android.content.Context
import com.phoenix.booklet.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

data class UpdateState(
    val isCheckingUpdate: Boolean = false,
    val isCheckSuccessful: Boolean? = null,
    val isUpdateAvailable: Boolean = false,
    val nextUpdateVersion: String = ""
)

class UpdateStateHolder(val context: Context) {

    private val _updateState = MutableStateFlow(UpdateState())
    val updateState = _updateState.asStateFlow()

    private val prefs =
        context.getSharedPreferences(Constants.PREFS_SETTING, Context.MODE_PRIVATE)

    init {
        val isUpdateAvailable =
            prefs.getBoolean(Constants.SETTING_UPDATE_AVAILABLE, false)
        val nextUpdateVersion =
            prefs.getString(Constants.SETTING_UPDATE_VERSION, BuildConfig.VERSION_NAME)

        setUpdateState(
            isCheckingUpdate = false,
            isCheckSuccessful = null,
            isUpdateAvailable = isUpdateAvailable,
            nextUpdateVersion = nextUpdateVersion
        )
    }

    suspend fun checkForUpdates() {
        _updateState.update { it.copy(isCheckingUpdate = true) }
        val result = requestLatestVersion()
        _updateState.update { it.copy(isCheckingUpdate = false) }
        when(result) {
            is UpdateResult.Error -> {
                _updateState.update { it.copy(isCheckSuccessful = false) }
            }
            is UpdateResult.Success -> {
                setUpdateState(
                    isCheckingUpdate = false,
                    isCheckSuccessful = true,
                    isUpdateAvailable = result.isUpdateAvailable,
                    nextUpdateVersion = result.nextUpdateVersion
                )
            }
        }
    }

    fun setUpdateState(
        isCheckingUpdate: Boolean,
        isCheckSuccessful: Boolean?,
        isUpdateAvailable: Boolean,
        nextUpdateVersion: String?
    ) {
        _updateState.update {
            it.copy(
                isCheckingUpdate = isCheckingUpdate,
                isCheckSuccessful = isCheckSuccessful,
                isUpdateAvailable = isUpdateAvailable,
                nextUpdateVersion = nextUpdateVersion ?: ""
            )
        }
    }

    fun cleanUpdateState() {
        _updateState.update { UpdateState() }
    }

}


suspend fun requestLatestVersion(): UpdateResult = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("https://api.github.com/repos/alibardide-dev/Booklet/releases/latest")
        .build()

    val response = OkHttpClient().newCall(request).execute()
    if (response.code != 200)
        return@withContext UpdateResult.Error("Request Failed: ${response.code}")

    val json = response.body?.string()
    val version = JSONObject(json).getString("name")

    return@withContext UpdateResult.Success(
        isUpdateAvailable = BuildConfig.VERSION_NAME < version,
        nextUpdateVersion = version
    )
}