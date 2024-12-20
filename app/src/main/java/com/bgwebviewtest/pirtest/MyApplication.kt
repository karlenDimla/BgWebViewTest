package com.bgwebviewtest.pirtest

import android.app.ActivityManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Process
import android.util.Log
import java.io.File


class MyApplication : Application() {
    private val shortProcessName: String by lazy {
        currentProcessName?.substringAfter(
            delimiter = packageName,
            missingDelimiterValue = "UNKNOWN"
        ) ?: "UNKNOWN"
    }
    private val isMainProcessCached: Boolean by lazy { isMainProcess }

    override fun onCreate() {
        super.onCreate()
        Log.d("TEST-PIR-APP", "Main process: ${Process.myPid()}")
        if (isMainProcessCached) {
            onMainProcessCreate()
        } else {
            /**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d("TEST-PIR-APP", "Setting data directory suffix")
            WebView.setDataDirectorySuffix("pir")
            }**/
            onSecondaryProcessCreate(shortProcessName)
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define the importance level of the notification channel
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Create the NotificationChannel with a unique ID, name, and importance level
            val channel =
                NotificationChannel("BgServiceChannel", "Service Notifications", importance)
            channel.description = "Notifications for foreground service"

            // Register the channel with the system
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override fun getDir(
        name: String?,
        mode: Int,
    ): File {
        val dir = super.getDir(name, mode)
        runInSecondaryProcessNamed("pir") {
            if (name == "webview") {
                return File("${dir.absolutePath}/pir").apply {
                    Log.d("TEST-PIR-APP", ":vpn process getDir = $absolutePath")
                    if (!exists()) {
                        mkdirs()
                    }
                }
            }
        }
        return dir
    }

    private fun onMainProcessCreate() {

    }

    private fun onSecondaryProcessCreate(shortProcessName: String) {

    }
}

val Context.currentProcessName: String?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        Application.getProcessName()
    } else {
        processNameFromSystemService()
    }

private fun Context.processNameFromSystemService(): String {
    val am = this.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager?
    return am?.runningAppProcesses?.firstOrNull { it.pid == Process.myPid() }?.processName.orEmpty()
}

inline val Application.isMainProcess: Boolean
    get() = packageName == currentProcessName

inline fun Context.runInSecondaryProcessNamed(name: String, block: () -> Unit) {
    if (currentProcessName == "$packageName:$name") {
        block()
    }
}