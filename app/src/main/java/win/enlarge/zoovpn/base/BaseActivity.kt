package win.enlarge.zoovpn.base

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialExListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.utils.*

abstract class BaseActivity(layoutId: Int) : AppCompatActivity(layoutId) {

    private var isBackground = false
    private var lovinInterstitialAd: MaxInterstitialAd? = null
    private var topOnInterstitialAd: ATInterstitial? = null
    private var openAd: ATSplashAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //用Lovin的插屏
        createLovinInterstitialAd()
        //用TopOn的插屏
//        createTopOnInterstitialAd()
        createOpenAd()
        onConvert()
    }

    abstract fun onConvert()

    open fun onInterstitialAdHidden() {}

    open fun onSplashAdHidden() {}

    fun registerEventBus() {
        object : BaseLifeCycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                EventBus.getDefault().register(this@BaseActivity)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                EventBus.getDefault().unregister(this@BaseActivity)
            }
        }.bindWithLifecycle(this)
    }

    //要不要闪屏
    override fun onStop() {
        super.onStop()
        isBackground = isInBackground()
    }

    override fun onResume() {
        super.onResume()
        if (isBackground) {
            isBackground = false
            val content = findViewById<ViewGroup>(android.R.id.content)
            (content.getTag(R.id.open_ad_view_id) as? FrameLayout)?.let {
                showOpenAd(it)
            } ?: kotlin.run {
                FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    content.addView(this)
                    content.setTag(R.id.open_ad_view_id, this)
                    showOpenAd(this)
                }
            }
        }
    }

    private fun createOpenAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
            withContext(Dispatchers.Main) {
                openAd?.onDestory()
                openAd = openAdCreator()
            }
        }
    }

    private fun createLovinInterstitialAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
            withContext(Dispatchers.Main) {
                lovinInterstitialAd?.destroy()
                lovinInterstitialAd = lovinInterstitialAdCreator()
            }
        }
    }

    private fun lovinInterstitialAdCreator() =
        MaxInterstitialAd(getString(R.string.lovin_insert_ad_id), lovinSdk, this).apply {
            "MaxInterstitialAd lovinInterstitialAdCreator".loge()
            setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    "MaxInterstitialAd onAdLoaded".loge()
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    "MaxInterstitialAd onAdDisplayed".loge()
                }

                override fun onAdHidden(ad: MaxAd?) {
                    "MaxInterstitialAd onAdHidden".loge()
                    adLastTime = System.currentTimeMillis()
                    createLovinInterstitialAd()
                    onInterstitialAdHidden()
                }

                override fun onAdClicked(ad: MaxAd?) {
                    "MaxInterstitialAd onAdClicked".loge()
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    "MaxInterstitialAd onAdLoadFailed $adUnitId $error".loge()
                    createLovinInterstitialAd(3000)
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    "MaxInterstitialAd onAdDisplayFailed $ad $error".loge()
                    createLovinInterstitialAd(3000)
                }
            })
            loadAd()
        }

    private fun createTopOnInterstitialAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
//            withContext(Dispatchers.Main) {
//                topOnInterstitialAd = topOnInterstitialAdCreator()
//            }
        }
    }

//    private fun topOnInterstitialAdCreator() =
//        ATInterstitial(this, app.getString(R.string.top_on_insert_ad_id)).apply {
//            setAdListener(object : ATInterstitialExListener {
//                override fun onInterstitialAdLoaded() {
//                    "topOnInterstitialAd onInterstitialAdLoaded".loge()
//                }
//
//                override fun onInterstitialAdLoadFail(p0: AdError?) {
//                    "topOnInterstitialAd onInterstitialAdLoadFail $p0".loge()
//                    createTopOnInterstitialAd(3000)
//                }
//
//                override fun onInterstitialAdClicked(p0: ATAdInfo?) {
//                    "topOnInterstitialAd onInterstitialAdClicked $p0".loge()
//                }
//
//                override fun onInterstitialAdShow(p0: ATAdInfo?) {
//                    "topOnInterstitialAd onInterstitialAdShow $p0".loge()
//                }
//
//                override fun onInterstitialAdClose(p0: ATAdInfo?) {
//                    "topOnInterstitialAd onInterstitialAdClose $p0".loge()
//                    adLastTime = System.currentTimeMillis()
//                    createTopOnInterstitialAd()
//                    onInterstitialAdHidden()
//                }
//
//                override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
//                    "topOnInterstitialAd onInterstitialAdVideoStart $p0".loge()
//                }
//
//                override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
//                    "topOnInterstitialAd onInterstitialAdVideoEnd $p0".loge()
//                }
//
//                override fun onInterstitialAdVideoError(p0: AdError?) {
//                    "topOnInterstitialAd onInterstitialAdVideoError $p0".loge()
//                    createTopOnInterstitialAd(3000)
//                }
//
//                override fun onDeeplinkCallback(p0: ATAdInfo?, p1: Boolean) {
//                    "topOnInterstitialAd onDeeplinkCallback $p0".loge()
//                }
//            })
//            load()
//        }


    private fun openAdCreator() =
        ATSplashAd(this, getString(R.string.top_on_open_ad_id), null, object : ATSplashAdListener {
            override fun onAdLoaded() {
                Log.e("openAdCreator", "onAdLoaded")
            }

            override fun onNoAdError(p0: AdError?) {
                Log.e("openAdCreator", "onNoAdError $p0")
                createOpenAd(3000)
            }

            override fun onAdShow(p0: ATAdInfo?) {
                Log.e("openAdCreator", "onAdShow $p0")
            }

            override fun onAdClick(p0: ATAdInfo?) {
                Log.e("openAdCreator", "onAdClick")
            }

            override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
                Log.e("openAdCreator", "onAdDismiss")
                onSplashAdHidden()
                createOpenAd()

            }
        }, 5000).apply {
            setLocalExtra(
                mutableMapOf<String, Any>(
                    ATAdConst.KEY.AD_WIDTH to globalWidth,
                    ATAdConst.KEY.AD_HEIGHT to (globalHeight * 0.85).toInt()
                )
            )
            loadAd()
        }

    fun showOpenAdImpl(viewGroup: ViewGroup, tag: String = ""): Boolean {
        openAd?.let {
            if (it.isAdReady) {
                it.show(this, viewGroup)
                return true
            }
        }
        return false
    }


    private fun showInsertAdImpl(tag: String = ""): Boolean {
        lovinInterstitialAd?.let {
            if (it.isReady) {
                it.showAd(tag)
                return true
            }
        }
        topOnInterstitialAd?.let {
            if (it.isAdReady) {
                it.show(this)
                return true
            }
        }
        return false
    }


    fun showOpenAd(viewGroup: ViewGroup, tag: String = "", isForce: Boolean = false): Boolean {
        if (configEntity.isOpenAdReplacedByInsertAd()) {
            return showInsertAd(tag = tag, isForce = isForce)
        } else {
            return showOpenAdImpl(viewGroup, tag = tag)
        }
    }

    fun showInsertAd(showByPercent: Boolean = false, isForce: Boolean = false, tag: String = ""): Boolean {
        if (isForce) {
            return showInsertAdImpl(tag)
        } else {
            if (configEntity.isCanShowInsertAd()) {
                if ((showByPercent && configEntity.isCanShowByPercent()) || (!showByPercent)) {
                    if (System.currentTimeMillis() - adLastTime > configEntity.insertAdOffset() * 1000) {
                        var showInsertAd = false
                        if (adShownList.getOrNull(adShownIndex) == true) {
                            showInsertAd = showInsertAdImpl(tag)
                        }
                        adShownIndex++
                        if (adShownIndex >= adShownList.size) {
                            adShownIndex = 0
                        }
                        return showInsertAd
                    }
                }
            }
            return false
        }
    }
}