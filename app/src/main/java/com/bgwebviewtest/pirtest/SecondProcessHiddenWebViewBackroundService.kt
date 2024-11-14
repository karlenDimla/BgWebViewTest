package com.bgwebviewtest.pirtest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.webkit.WebView

class SecondProcessHiddenWebViewBackroundService : Service() {
    private val webViewProvider = HiddenWebViewProvider()
    private lateinit var webView: WebView
    private lateinit var webView1: WebView
    private lateinit var webView2: WebView
    private lateinit var webView3: WebView
    private lateinit var webView4: WebView

    private val urls = listOf(
        "https://www.cypress.io/",
        "https://parabank.parasoft.com/parabank/index.htm",
        "https://compendiumdev.co.uk/",
        "https://www.telerik.com/support/demos",
        "http://the-internet.herokuapp.com/challenging_dom"
    )

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TEST-PIR-SERVICE", "Starting $this")
        var wvCnt = 0
        var wvCnt1 = 0
        var wvCnt2 = 0
        var wvCnt3 = 0
        var wvCnt4 = 0

        fun attemptKill() {
            if (wvCnt >= 5 && wvCnt1 >= 5 && wvCnt2 >= 5  && wvCnt3 >= 5 && wvCnt4 >= 5) stopSelf()
        }

        webView = webViewProvider.create(this) {
            wvCnt++
            if (wvCnt < 5) {
                webView.loadUrl(urls[wvCnt])
            } else attemptKill()
        }
        webView1 = webViewProvider.create(this) {
            wvCnt1++
            if (wvCnt1 < 5) {
                webView1.loadUrl(urls[wvCnt1])
            } else attemptKill()
        }
        webView2 = webViewProvider.create(this) {
            wvCnt2++
            if (wvCnt2 < 5) {
                webView2.loadUrl(urls[wvCnt2])
            } else attemptKill()
        }
        webView3 = webViewProvider.create(this) {
            wvCnt3++
            if (wvCnt3 < 5) {
                webView3.loadUrl(urls[wvCnt3])
            } else attemptKill()
        }
        webView4 = webViewProvider.create(this) {
            wvCnt4++
            if (wvCnt4 < 5) {
                webView4.loadUrl(urls[wvCnt4])
            } else attemptKill()
        }

        webView.loadUrl(urls[wvCnt])
        webView1.loadUrl(urls[wvCnt1])
        webView2.loadUrl(urls[wvCnt2])
        webView3.loadUrl(urls[wvCnt3])
        webView4.loadUrl(urls[wvCnt4])

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
        webView1.destroy()
        webView2.destroy()
        webView3.destroy()
        webView4.destroy()
        Log.d("TEST-PIR-SERVICE-PROC", "Destroyed $this and all webviews")
    }
}