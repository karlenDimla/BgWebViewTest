package com.bgwebviewtest.pirtest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.webkit.WebView

class MultipleHiddenWebViewBackgroundService : Service() {
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
        val max = urls.size
        var wvCnt = 0
        var wvCnt1 = 0
        var wvCnt2 = 0
        var wvCnt3 = 0
        var wvCnt4 = 0

        fun attemptKill() {
            Log.d("KLDIMSUM", "Counting: max: $max wvCnt: $wvCnt wvCnt1: $wvCnt1 wvCnt2:$wvCnt2 wvCnt3: $wvCnt3 wvCnt4: $wvCnt4")
            if (wvCnt >= max && wvCnt1 >= max && wvCnt2 >= max && wvCnt3 >= max && wvCnt4 >= max) stopSelf()
        }

        webView = webViewProvider.create(this, TRAVERSE_DOM) {
            wvCnt++
            if (wvCnt < max) {
                Log.d("KLDIMSUM", "wvCnt: $wvCnt")
                webView.loadUrl(urls[wvCnt])
            } else attemptKill()
        }
        webView1 = webViewProvider.create(this, TRAVERSE_DOM) {
            wvCnt1++
            if (wvCnt1 < max) {
                Log.d("KLDIMSUM", "wvCnt1: $wvCnt1")
                webView1.loadUrl(urls[wvCnt1])
            } else attemptKill()
        }
        webView2 = webViewProvider.create(this, TRAVERSE_DOM) {
            wvCnt2++
            if (wvCnt2 < max) {
                Log.d("KLDIMSUM", "wvCnt2: $wvCnt2")
                webView2.loadUrl(urls[wvCnt2])
            } else attemptKill()
        }
        webView3 = webViewProvider.create(this, TRAVERSE_DOM) {
            wvCnt3++
            if (wvCnt3 < max) {
                Log.d("KLDIMSUM", "wvCnt3: $wvCnt3")
                webView3.loadUrl(urls[wvCnt3])
            } else attemptKill()
        }
        webView4 = webViewProvider.create(this, TRAVERSE_DOM) {
            wvCnt4++
            if (wvCnt4 < max) {
                Log.d("KLDIMSUM", "wvCnt4: $wvCnt4")
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