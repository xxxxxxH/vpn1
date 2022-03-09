package win.enlarge.zoovpn.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV
import win.enlarge.zoovpn.BuildConfig
import win.enlarge.zoovpn.Constant
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.pojo.ConfigPojo
import win.enlarge.zoovpn.pojo.ServerPojo
import win.enlarge.zoovpn.pojo.UpdatePojo
import java.text.SimpleDateFormat
import java.util.*

val serverList by lazy {
    mutableListOf(
        ServerPojo(R.drawable.activity_server_usa, "U.S.A", R.drawable.activity_server_signal_0),
        ServerPojo(R.drawable.activity_server_germany, "Germany", R.drawable.activity_server_signal_1),
        ServerPojo(R.drawable.activity_server_italy, "Italy", R.drawable.activity_server_signal_2),
        ServerPojo(R.drawable.activity_server_england, "England", R.drawable.activity_server_signal_3),
        ServerPojo(R.drawable.activity_server_singapore, "Singapore", R.drawable.activity_server_signal_3),
        ServerPojo(R.drawable.activity_server_australia, "Australia", R.drawable.activity_server_signal_3),
        ServerPojo(R.drawable.activity_server_country, "Country", R.drawable.activity_server_signal_3),
        ServerPojo(R.drawable.activity_server_spain, "Spain", R.drawable.activity_server_signal_3),
    )
}

fun Long.timeOffset() = this - defaultOffset

val defaultOffset
    get() = TimeZone.getDefault().rawOffset

fun Long.toPatternString(
    pattern: String = "HH:mm:ss",
    locale: Locale = Locale.getDefault(),
) = Date(this).toPatternString(pattern, locale)

fun Date.toPatternString(
    pattern: String = "HH:mm:ss",
    locale: Locale = Locale.getDefault(),
) = pattern.getSimpleDateFormat(locale).format(this) ?: ""

fun String.getSimpleDateFormat(locale: Locale = Locale.getDefault()) =
    SimpleDateFormat(this, locale)

fun View.click(block: (View) -> Unit) {
    setOnClickListener {
        block(it)
    }
}


fun AppCompatActivity.requestPermission(block: () -> Unit = {}) {
    XXPermissions.with(this)
        .permission(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        )
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                if (all) {
                    block()
                } else {
                    Toast.makeText(
                        this@requestPermission,
                        "some permissions were not granted normally",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }

            override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                super.onDenied(permissions, never)
                Toast.makeText(this@requestPermission, "no permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
}

fun ImageView.loadWith(any: Any) {
    Glide.with(this).load(any).into(this)
}

fun Context.jumpToWebByDefault(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url)).let {
    startActivity(it)
}

private val globalMetrics by lazy {
    Resources.getSystem().displayMetrics
}

val globalWidth by lazy {
    globalMetrics.widthPixels
}

val globalHeight by lazy {
    globalMetrics.heightPixels
}

var account
    get() = mmkv.getString(Constant.KEY_ACCOUNT, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_ACCOUNT, value)
    }

private var config
    get() = mmkv.getString(Constant.KEY_CONFIG, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_CONFIG, value)
    }

var configEntity
    get() = (config.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, ConfigPojo::class.java)
    }
    set(value) {
        config = gson.toJson(value)
    }

var adInvokeTime
    get() = mmkv.getInt(Constant.KEY_AD_INVOKE_TIME, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_INVOKE_TIME, value)
    }

var adRealTime
    get() = mmkv.getInt(Constant.KEY_AD_REAL_TIME, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_REAL_TIME, value)
    }

private var adShown
    get() = mmkv.getString(Constant.KEY_AD_SHOWN, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_AD_SHOWN, value)
    }

var adShownList
    get() = (adShown.ifBlank {
        "{}"
    }).let {
        gson.fromJson<List<Boolean>>(it, object : TypeToken<List<Boolean>>() {}.type)
    }
    set(value) {
        adShown = gson.toJson(value)
    }

var adShownIndex
    get() = mmkv.getInt(Constant.KEY_AD_SHOWN_INDEX, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_SHOWN_INDEX, value)
    }

var adLastTime
    get() = mmkv.getLong(Constant.KEY_AD_LAST_TIME, 0)
    set(value) {
        mmkv.putLong(Constant.KEY_AD_LAST_TIME, value)
    }

private var update
    get() = mmkv.getString(Constant.KEY_UPDATE, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_UPDATE, value)
    }

var updateEntity
    get() = (update.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, UpdatePojo::class.java)
    }
    set(value) {
        update = gson.toJson(value)
    }

var password
    get() = mmkv.getString(Constant.KEY_PASSWORD, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_PASSWORD, value)
    }

var isLogin
    get() = mmkv.getBoolean(Constant.KEY_IS_LOGIN, false)
    set(value) {
        mmkv.putBoolean(Constant.KEY_IS_LOGIN, value)
    }

var isRealDeepLink
    get() = mmkv.getBoolean(Constant.KEY_IS_REAL_DEEP_LINK, false)
    set(value) {
        mmkv.putBoolean(Constant.KEY_IS_REAL_DEEP_LINK, value)
    }

val gson by lazy {
    Gson()
}

val mmkv by lazy {
    MMKV.defaultMMKV()
}

val app by lazy {
    win.enlarge.zoovpn.Ktx.getInstance().app
}

val lovinSdk by lazy {
    win.enlarge.zoovpn.Ktx.getInstance().lovinSdk
}

fun <T> T.loge(tag: String = "defaultTag") {
    if (BuildConfig.DEBUG) {
        var content = toString()
        val segmentSize = 3 * 1024
        val length = content.length.toLong()
        if (length <= segmentSize) {
            Log.e(tag, content)
        } else {
            while (content.length > segmentSize) {
                val logContent = content.substring(0, segmentSize)
                content = content.replace(logContent, "")
                Log.e(tag, logContent)
            }
            Log.e(tag, content)
        }
    }
}

fun isInBackground(): Boolean {
    val activityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager
        .runningAppProcesses
    for (appProcess in appProcesses) {
        if (appProcess.processName == app.packageName) {
            return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
    return false
}