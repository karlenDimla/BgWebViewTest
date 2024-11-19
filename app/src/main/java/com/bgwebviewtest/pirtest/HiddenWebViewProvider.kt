package com.bgwebviewtest.pirtest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Process;
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class HiddenWebViewProvider {
    @SuppressLint("SetJavaScriptEnabled")
    fun create(
        context: Context,
        scriptToRun: String,
        onJavascriptCompleted: (WebView, String) -> Unit
    ): WebView {
        return WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.also {
                        view?.loadUrl(it.toString())
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("TEST-PIR-WEBVIEW", "Webview[$view] Context is $context")
                    Log.d("TEST-PIR-WEBVIEW", "Webview[$view] Process is ${Process.myPid()}")
                    Log.d(
                        "TEST-PIR-WEBVIEW",
                        "Webview[$view] Thread is ${Thread.currentThread().name}"
                    )
                    Log.d("TEST-PIR-WEBVIEW", "Webview[$view] Starting to load $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d("TEST-PIR-WEBVIEW", "Webview[$view] Finished loading $url")

                    view?.evaluateJavascript(
                        scriptToRun
                    ) {
                        Log.d(
                            "TEST-PIR-WEBVIEW",
                            "Webview[$view] Completed evaluating js for $url"
                        )
                        onJavascriptCompleted(view, url.toString())
                    }
                }
            }

            addJavascriptInterface(WebAppInterface(this), "Android");
            settings.javaScriptEnabled = true
        }
    }

    class WebAppInterface(
        webView: WebView
    ) {
        private val webViewName: String = webView.toString()
        private val processId: String = Process.myPid().toString()
        private val threadId: String = Thread.currentThread().name

        @JavascriptInterface
        fun logMessage(msg: String?) {
            Log.d(
                "TEST-PIR-INTERFACE",
                "WebView[$webViewName] Process[$processId] Thread[$threadId]: ${msg!!}"
            )
        }
    }
}