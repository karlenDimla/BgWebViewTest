package com.bgwebviewtest.pirtest.simulation

import android.R
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.webkit.WebView
import androidx.core.app.NotificationCompat
import com.bgwebviewtest.pirtest.HiddenWebViewProvider
import com.bgwebviewtest.pirtest.MainActivity
import com.bgwebviewtest.pirtest.TRAVERSE_DOM_WITH_PRIME_COMPUTE
import com.bgwebviewtest.pirtest.db.SimulationDatabase
import com.bgwebviewtest.pirtest.db.SimulationUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class ForegroundHighLoadSimulationService : Service() {
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val database = SimulationDatabase.getInstance(this)
    private val urlsDao = database.urlsDao()
    private val webViewProvider = HiddenWebViewProvider()
    private val hiddenWebViews: MutableMap<WebView, HiddenWebViewState> = mutableMapOf()
    private var completedCount = 0
    private var maxWebViewCount = 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = createNotification()

        // Start the service in the foreground, providing the notification
        startForeground(1, notification)

        Log.d("TEST-PIR-SERVICE-PROC", "Starting $this")
        maxWebViewCount = getNumberOfCores().run {
            if (this == 0) {
                1
            } else {
                this
            }
        }

        runBlocking {
            withContext(Dispatchers.IO) {
                navigation_urls.forEach {
                    urlsDao.insert(
                        SimulationUrl(
                            url = it,
                            checked = false,
                            checkedTimeInMillis = 0L,
                            completed = false,
                            completedTimeInMillis = 0L,
                        )
                    )
                }
            }
        }

        val urlCountPerWebView: Int = navigation_urls.size / maxWebViewCount

        var createCount = 0
        while (createCount != maxWebViewCount) {
            val start = createCount * urlCountPerWebView
            val end = if (createCount == maxWebViewCount - 1) {
                navigation_urls.size - 1
            } else {
                start + (urlCountPerWebView - 1)
            }
            setupHiddenWebView(start, end)
            createCount += 1
        }

        hiddenWebViews.forEach {
            it.key.loadUrl(navigation_urls[it.value.current])
        }

        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(
            this,
            MainActivity::class.java
        )
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "BgServiceChannel")
            .setContentTitle("Service Running")
            .setContentText("This service is running in the foreground")
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun getNumberOfCores(): Int {
        return try {
            // Get the directory containing CPU info
            val cpuDir = File("/sys/devices/system/cpu/")
            // Filter folders matching the pattern "cpu[0-9]+"
            val cpuFiles = cpuDir.listFiles { file -> file.name.matches(Regex("cpu[0-9]+")) }
            cpuFiles?.size ?: Runtime.getRuntime().availableProcessors()
        } catch (e: Exception) {
            // In case of an error, fall back to availableProcessors
            Runtime.getRuntime().availableProcessors()
        }
    }

    override fun onDestroy() {
        if (hiddenWebViews.isNotEmpty()) {
            hiddenWebViews.forEach {
                it.key.destroy()
            }

            Log.d("TEST-PIR-SERVICE-PROC", "Destroyed $this and all webviews")
        }
    }

    private fun setupHiddenWebView(start: Int, end: Int) {
        webViewProvider.create(this, TRAVERSE_DOM_WITH_PRIME_COMPUTE) { webView, url ->
            if (navigation_urls.contains(url)) {
                completedCount += 1
                coroutineScope.launch(Dispatchers.IO) {
                    Log.d("TEST-DEBUG-DB", "Updating DB for url: $url $completedCount")
                    urlsDao.getUrl(url)
                        ?.copy(checked = true, checkedTimeInMillis = System.currentTimeMillis())
                        ?.also {
                            urlsDao.insert(it)
                        }
                }

                Log.d("TEST-DEBUG", "Url: $url Completed count: $completedCount")
                val lastStat = hiddenWebViews[webView]!!
                val newStat = lastStat.copy(current = lastStat.current + 1)
                hiddenWebViews[webView] = newStat
                if (newStat.current <= newStat.end) {
                    Log.d("TEST-DEBUG", "Webview[$webView] current: ${newStat.current}")
                    webView.loadUrl(navigation_urls[newStat.current])
                } else attemptKill()
            }
        }.also {
            Log.d("TEST-DEBUG", "Webview[$it] start: $start end: $end")
            hiddenWebViews[it] = HiddenWebViewState(
                start = start,
                end = end,
                current = start,
            )
        }
    }

    private fun attemptKill() {
        if (completedCount >= navigation_urls.size) {
            stopSelf()
        }
    }

    data class HiddenWebViewState(
        val start: Int,
        val end: Int,
        val current: Int,
    )
}

