package com.bgwebviewtest.pirtest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.webkit.WebView

class FillFormHiddenWebViewBackgroundService : Service() {
    private val webViewProvider = HiddenWebViewProvider()
    private lateinit var webView: WebView

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TEST-PIR-SERVICE", "Starting $this")

        webView = webViewProvider.create(this, FILL_FORM) { _, url ->
            if (url == "https://fill.dev/submit") {
                stopSelf()
            }
        }
        webView.loadUrl("https://fill.dev/form/login-simple")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
        Log.d("TEST-PIR-SERVICE-PROC", "Destroyed $this and webview")
    }
}