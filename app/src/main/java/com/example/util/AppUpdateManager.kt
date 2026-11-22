package com.example.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.data.SupabaseNetwork
import com.example.data.model.AppUpdateInfo
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

enum class UpdateType {
    NONE, OPTIONAL, FORCE
}

data class UpdateCheckResult(
    val type: UpdateType = UpdateType.NONE,
    val info: AppUpdateInfo? = null,
    val isAvailable: Boolean = false
)

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private val _updateState = MutableStateFlow(UpdateCheckResult())
    val updateState: StateFlow<UpdateCheckResult> = _updateState.asStateFlow()

    suspend fun checkForUpdates(context: Context): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            val currentVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }

            val remoteInfo = SupabaseNetwork.client.from("app_updates")
                .select(columns = Columns.ALL)
                .decodeSingleOrNull<AppUpdateInfo>()

            if (remoteInfo != null) {
                val type = when {
                    remoteInfo.isForceUpdate && currentVersionCode < remoteInfo.minVersionCode -> UpdateType.FORCE
                    currentVersionCode < remoteInfo.latestVersionCode -> {
                        if (remoteInfo.isForceUpdate) UpdateType.FORCE else UpdateType.OPTIONAL
                    }
                    else -> UpdateType.NONE
                }
                val result = UpdateCheckResult(
                    type = type,
                    info = remoteInfo,
                    isAvailable = type != UpdateType.NONE
                )
                _updateState.value = result
                return@withContext result
            }
        } catch (e: Exception) {
            Log.w(TAG, "Update check failed: ${e.message}")
        }
        val fallback = UpdateCheckResult()
        _updateState.value = fallback
        fallback
    }
}
