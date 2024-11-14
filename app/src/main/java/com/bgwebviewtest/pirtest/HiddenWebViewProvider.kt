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
    fun create(context: Context, onJavascriptCompleted: () -> Unit): WebView {
        return WebView(context).apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    request?.url?.also {
                        view?.loadUrl(this.toString())
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
                        "(function() {\n" +
                                "    function traverseDOM(element) {\n" +
                                "        // Gather details about the current element\n" +
                                "        let details = {\n" +
                                "            tagName: element.tagName,\n" +
                                "            attributes: {},\n" +
                                "            textContent: element.textContent.trim().substring(0, 30) // Preview first 30 chars\n" +
                                "        };\n" +
                                "\n" +
                                "        // Extract attributes\n" +
                                "        for (let attr of element.attributes) {\n" +
                                "            details.attributes[attr.name] = attr.value;\n" +
                                "        }\n" +
                                "\n" +
                                "        // Log the details to the Android interface\n" +
                                "        Android.logMessage(JSON.stringify(details));\n" +
                                "\n" +
                                "        // Recursively traverse each child element\n" +
                                "        for (let i = 0; i < element.children.length; i++) {\n" +
                                "            traverseDOM(element.children[i]);\n" +
                                "        }\n" +
                                "    }\n" +
                                "\n" +
                                "    // Start traversing from the root element\n" +
                                "    traverseDOM(document.documentElement);\n" +
                                "})();"
                    ) {
                        Log.d(
                            "TEST-PIR-WEBVIEW",
                            "Webview[$view] Completed evaluating js for $url"
                        )
                        onJavascriptCompleted()
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