package com.bgwebviewtest.pirtest

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bgwebviewtest.pirtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindViews() {
        viewBinding.webview.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    // Load the URL within the WebView
                    view?.loadUrl(request?.url.toString())
                    // Return true to indicate we've handled the URL loading
                    return true
                }
            }
            settings.javaScriptEnabled = true
            loadUrl("https://www.google.com/")
        }

        viewBinding.launchMultiple.setOnClickListener {
            startService(Intent(this, MultipleHiddenWebViewBackgroundService::class.java))
        }

        viewBinding.launchFillFormSubmit.setOnClickListener {
            startService(Intent(this, FillFormHiddenWebViewBackgroundService::class.java))
        }

        viewBinding.launchDropdown.setOnClickListener {
            startService(Intent(this, DropdownHiddenWebviewBackgroundService::class.java))
        }

        viewBinding.killDifferentProcess.setOnClickListener {
            stopService(Intent(this, FillFormHiddenWebViewBackgroundService::class.java))
            stopService(Intent(this, MultipleHiddenWebViewBackgroundService::class.java))
            stopService(Intent(this, DropdownHiddenWebviewBackgroundService::class.java))
        }
    }
}