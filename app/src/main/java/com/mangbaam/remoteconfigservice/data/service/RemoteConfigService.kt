package com.mangbaam.remoteconfigservice.data.service

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.get
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import kotlin.coroutines.resume

internal class RemoteConfigService @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {
    private val crashlytics = Firebase.crashlytics

    /**
     * ## RemoteConfig에 정의된 값을 반환하는 함수
     *
     * @param key RemoteConfig에 정의된 키
     */
    suspend fun getValue(key: String): FirebaseRemoteConfigValue? = suspendCancellableCoroutine { cont ->
        runCatching {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                logFetchTime()

                if (task.isSuccessful) {
                    cont.resume(
                        remoteConfig[key].also {
                            Log.d("[MANGBAAM]RemoteConfigService", "getValue($key): ${it.asString()}")
                        }
                    )
                } else {
                    Log.i("[MANGBAAM]RemoteConfigService", "getValue($key) fetch 실패")
                    crashlytics.recordException(Throwable(task.exception))
                    cont.resume(null)
                }
            }
        }.onFailure {
            Log.e("[MANGBAAM]RemoteConfigService", "getValue($key): fetch 에러", it)
            crashlytics.recordException(it)
            cont.resume(null)
        }
    }

    suspend fun getString(key: String): String? = getValue(key)?.asString()
    suspend fun getString(key: String, defaultValue: String): String = getValue(key)?.asString() ?: defaultValue

    suspend fun getLong(key: String): Long? = getValue(key)?.asLong()
    suspend fun getLong(key: String, defaultValue: Long): Long = getValue(key)?.asLong() ?: defaultValue

    suspend fun getBoolean(key: String): Boolean? = getValue(key)?.asBoolean()
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = getValue(key)?.asBoolean() ?: defaultValue

    suspend fun getDouble(key: String): Double? = getValue(key)?.asDouble()
    suspend fun getDouble(key: String, defaultValue: Double): Double = getValue(key)?.asDouble() ?: defaultValue

    /**
     * ## RemoteConfig에 정의된 Json 값을 객체로 변환하는 함수
     *
     * @param T **[@Serializable] 어노테이션**으로 정의된 객체
     * @param key RemoteConfig에 정의된 키
     */
    @OptIn(ExperimentalSerializationApi::class)
    suspend inline fun <reified T> getReferenceType(key: String): T? {
        val json = Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            allowStructuredMapKeys = true
        }

        return getString(key)?.let {
            runCatching { json.decodeFromString<T>(it) }.getOrNull()
        }
    }

    /**
     * ## RemoteConfig에 정의된 Json 값을 객체로 변환하는 함수
     *
     * @param T **[@Serializable] 어노테이션**으로 정의된 객체
     * @param key RemoteConfig에 정의된 키
     * @param defaultValue 주어진 [key]로 값을 가져올 수 없는 경우의 기본값
     */
    suspend inline fun <reified T> getReferenceType(key: String, defaultValue: T): T {
        return getReferenceType<T>(key) ?: defaultValue
    }

    private fun logFetchTime() = with(remoteConfig.info) {
        val fetchTime = fetchTimeMillis.let {
            if (it == -1L) {
                "Not yet"
            } else {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            }
        }

        val lastFetchStatus = when (lastFetchStatus) {
            -1 -> "LAST_FETCH_STATUS_SUCCESS"
            0 -> "LAST_FETCH_STATUS_NO_FETCH_YET"
            1 -> "LAST_FETCH_STATUS_FAILURE"
            2 -> "LAST_FETCH_STATUS_THROTTLED"
            else -> ""
        }

        Log.i(
            "[MANGBAAM]RemoteConfigService",
            "[lastFetchTime]: $fetchTime, [lastFetchStatus]: $lastFetchStatus",
        )
    }
}
