package win.enlarge.zoovpn.utils

import android.net.Uri
import android.text.TextUtils
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import win.enlarge.zoovpn.AppService
import win.enlarge.zoovpn.CompletionHandlerWrapper
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.pojo.ConfigPojo
import win.enlarge.zoovpn.pojo.UpdatePojo
import java.io.File
import java.util.concurrent.TimeUnit

val devkitService by lazy {
    devkitServiceCreator()
}

fun CoroutineScope.requestConfig(block: () -> Unit) {
    launch(Dispatchers.IO) {
        val params = RequestParams(app.getString(R.string.base_url) + "config")
        x.http().get(params,object : Callback.CommonCallback<String>{
            override fun onSuccess(result: String?) {
                if (!TextUtils.isEmpty(result)){
                    val s1 = StringBuffer(result!!).replace(1, 2, "").toString()
                    s1.loge("xxxxxxHs1")
                    var s2:String? = null
                    if (s1.isBase64()){
                        s2 = s1.toByteArray().fromBase64().decodeToString()
                        s2.loge("xxxxxxHs2")
                    }
                    if (!TextUtils.isEmpty(s2)){
                        configEntity = Gson().fromJson(s2,ConfigPojo::class.java)
                        configEntity.loge("xxxxxxHconfigEntity")
                    }
                    if (configEntity!=null){
                        if (configEntity.insertAdInvokeTime() != adInvokeTime || configEntity.insertAdRealTime() != adRealTime) {
                            adInvokeTime = configEntity.insertAdInvokeTime()
                            adRealTime = configEntity.insertAdRealTime()
                            adShownIndex = 0
                            adLastTime = 0
                            adShownList = mutableListOf<Boolean>().apply {
                                if (adInvokeTime >= adRealTime) {
                                    (0 until adInvokeTime).forEach { _ ->
                                        add(false)
                                    }
                                    (0 until adRealTime).forEach { index ->
                                        set(index, true)
                                    }
                                }
                            }
                        }
                    }
                    if (configEntity.faceBookId().isNotBlank()) {
                        initFaceBook()
                    }
                    var info:String?=null
                    if (configEntity.info != null){
                        if (configEntity.info!!.isBase64()){
                            info = configEntity.info!!.toByteArray().fromBase64().decodeToString()
                            info.loge("xxxxxxHinfo")
                        }
                        if (!TextUtils.isEmpty(info)){
                            updateEntity = Gson().fromJson(info,UpdatePojo::class.java)
                            updateEntity.loge("xxxxxxHupdateEntity")
                        }
                    }
                }
                /*result?.let {
                        "xxxxxxH -> $it".loge()
                        try {
                            StringBuffer(it).replace(1, 2, "").toString()
                        } catch (e: Exception) {
                            e.fillInStackTrace()
                            null
                        }
                    }?.let {
                        "xxxxxxH2 -> $it".loge()
                        "requestConfig origin-> $it".loge()
                        if (it.isBase64()) {
                            "requestConfig isBase64".loge()
                            it.toByteArray().fromBase64().decodeToString()
                        } else {
                            "requestConfig notBase64".loge()
                            null
                        }
                    }?.let {
                        "xxxxxxH3 -> $it".loge()
                        gson.fromJson(it, ConfigPojo::class.java)
                    }?.let {
                        "xxxxxxH4 -> $it".loge()
                        configEntity = it

                        if (configEntity.faceBookId().isNotBlank()) {
                            initFaceBook()
                        }
                        "requestConfig configEntity-> $configEntity".loge()
                        it.info
                    }?.let {
                        if (it.isBase64()) {
                            it.toByteArray().fromBase64().decodeToString()
                        } else {
                            null
                        }
                    }?.let {
                        gson.fromJson(it, UpdatePojo::class.java)
                    }?.let {
                        updateEntity = it
                        "requestConfig updateEntity-> $updateEntity".loge()
                    }*/
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                "xxxxxxHonError -> ${ex.toString()}".loge()
            }

            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onFinished() {

            }

        })

        withContext(Dispatchers.Main) {
            block()
        }
    }
}


fun initFaceBook() {
    FacebookSdk.apply {
        setApplicationId(configEntity.faceBookId())
        sdkInitialize(app)
        setAdvertiserIDCollectionEnabled(true)
        setAutoLogAppEventsEnabled(true)
        fullyInitialize()
    }
}

fun BaseActivity.fetchAppLink(key: String, callback: (Uri?) -> Unit) {
    AppLinkData.fetchDeferredAppLinkData(this, key, object : CompletionHandlerWrapper() {
        override fun onInvoke(appLinkData: AppLinkData?) {
            callback(appLinkData?.targetUri)
        }
    })
}

suspend fun <T> doSuspendOrNull(block: suspend () -> T) =
    try {
        block()
    } catch (e: Exception) {
        "doOrNull ->$e".loge()
        null
    }

private fun clientCreator(block: OkHttpClient.Builder.() -> OkHttpClient.Builder = { this }) =
    OkHttpClient.Builder()
        .cache(Cache(File(app.cacheDir, "cache"), 1024 * 1024 * 100))
        .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(app)))
        .readTimeout(15000, TimeUnit.MILLISECONDS)
        .writeTimeout(15000, TimeUnit.MILLISECONDS)
        .connectTimeout(15000, TimeUnit.MILLISECONDS)
        .block()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

private fun devkitServiceCreator() =
    Retrofit
        .Builder()
        .client(clientCreator())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(app.getString(R.string.base_url))
        .build()
        .create(AppService::class.java)