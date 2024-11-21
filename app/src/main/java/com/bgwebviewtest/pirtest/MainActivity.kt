package com.bgwebviewtest.pirtest

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bgwebviewtest.pirtest.databinding.ActivityMainBinding
import com.bgwebviewtest.pirtest.db.SimulationDatabase
import com.bgwebviewtest.pirtest.simulation.ForegroundHighLoadSimulationService
import com.bgwebviewtest.pirtest.simulation.HighLoadSimulationService
import com.bgwebviewtest.pirtest.simulation.ScanSimulationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val database = SimulationDatabase.getInstance(this)

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


        // Initialize the permission launcher
        val requestPermissionLauncher =
            registerForActivityResult<String, Boolean>(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission granted, you can display notifications
                } else {
                    // Permission denied, handle accordingly (e.g., inform the user of the limitation)
                }
            }


        // Check if the permission has already been granted, and request it if not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the POST_NOTIFICATIONS permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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

        viewBinding.startTestJob.setOnClickListener {
            lifecycleScope.launch (Dispatchers.IO){
                database.urlsDao().deleteUrls()
            }
            startService(Intent(this, ScanSimulationService::class.java))
        }

        viewBinding.startTestHighLoad.setOnClickListener {
            lifecycleScope.launch (Dispatchers.IO){
                database.urlsDao().deleteUrls()
            }
            startService(Intent(this, HighLoadSimulationService::class.java))
        }

        viewBinding.startForeground.setOnClickListener {
            lifecycleScope.launch (Dispatchers.IO){
                database.urlsDao().deleteUrls()
            }
            startForegroundService(Intent(this, ForegroundHighLoadSimulationService::class.java))
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

        viewBinding.killAll.setOnClickListener {
            stopService(Intent(this, FillFormHiddenWebViewBackgroundService::class.java))
            stopService(Intent(this, MultipleHiddenWebViewBackgroundService::class.java))
            stopService(Intent(this, DropdownHiddenWebviewBackgroundService::class.java))
            stopService(Intent(this, ScanSimulationService::class.java))
            stopService(Intent(this, HighLoadSimulationService::class.java))
            stopService(Intent(this, ForegroundHighLoadSimulationService::class.java))
            lifecycleScope.launch(Dispatchers.IO) {
                database.urlsDao().deleteUrls()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                database.urlsDao().getAllUrls().also { urls ->
                    val checkedSize = urls.filter { it.checked }.size
                    withContext(Dispatchers.Main) {
                        viewBinding.jobStatus.text =
                            getString(R.string.status_ongoing_check, checkedSize, urls.size)
                    }
                }
                delay(1000)
            }
        }
    }
}