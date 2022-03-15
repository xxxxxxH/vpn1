package net.masvate.vpnpri.base

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import net.masvate.vpnpri.App
import net.masvate.vpnpri.R
import net.masvate.vpnpri.event.MessageEvent
import net.masvate.vpnpri.utils.*

abstract class BaseActivity(layoutId: Int) : AppCompatActivity(layoutId) {

    private var isBackground = false
    private var lovinInterstitialAd: MaxInterstitialAd? = null
    private var openAd: ATSplashAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openAd = App.instance!!.openAd(openAdListener())
        openAd.loge("xxxxxxHopenAd")
        openAd!!.loadAd()

        lovinInterstitialAd = App.instance!!.lovinInterstitialAd(this)
        lovinInterstitialAd.loge("xxxxxxHlovinInsert")
        lovinInterstitialAd!!.setListener(lovinInsterListener())
        lovinInterstitialAd!!.loadAd()

        onConvert()
    }

    fun getLovinNativeAdView(){
        val lovinLoader = App.instance!!.lovinNative()
        lovinLoader.loadAd()
        lovinLoader.setNativeAdListener(object :MaxNativeAdListener(){
            override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd?) {
                super.onNativeAdLoaded(p0, p1)
                "$p0 $p1".loge("xxxxxxHonNativeAdLoaded")
                p0?.let {
                    EventBus.getDefault().post(MessageEvent("onNativeAdLoaded",it))
                }
            }

            override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                super.onNativeAdLoadFailed(p0, p1)
                "$p0 $p1".loge("xxxxxxHonNativeAdLoadFailed")
            }
        })
    }

    abstract fun onConvert()

    open fun onInterstitialAdHidden() {}

    open fun onSplashAdHidden() {}

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

    inner class openAdListener:ATSplashAdListener{
        override fun onAdLoaded() {
            "onAdLoaded".loge("xxxxxxHopenAdonAdLoaded")
        }

        override fun onNoAdError(p0: AdError?) {
            "$p0".loge("xxxxxxHopenAdonNoAdError")
            lifecycleScope.launch(Dispatchers.IO){
                delay(3000)
                openAd?.onDestory()
                openAd = App.instance!!.openAd(this@openAdListener)
                openAd?.loadAd()
            }
        }

        override fun onAdShow(p0: ATAdInfo?) {
            "onAdShow".loge("xxxxxxHopenAdonAdShow")
        }

        override fun onAdClick(p0: ATAdInfo?) {
            "onAdClick".loge("xxxxxxHopenAdonAdClick")
        }

        override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
            "onAdDismiss".loge("xxxxxxHopenAdonAdDismiss")
        }
    }

    inner class lovinInsterListener:MaxAdListener{
        override fun onAdLoaded(ad: MaxAd?) {
            "onAdLoaded".loge("xxxxxxHlovinonAdLoaded")
        }

        override fun onAdDisplayed(ad: MaxAd?) {
            "onAdDisplayed".loge("xxxxxxHlovinonAdDisplayed")
        }

        override fun onAdHidden(ad: MaxAd?) {
            "onAdHidden".loge("xxxxxxHlovinonAdHidden")
        }

        override fun onAdClicked(ad: MaxAd?) {
            "onAdClicked".loge("xxxxxxHlovinonAdClicked")
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            "onAdLoadFailed".loge("xxxxxxHlovinonAdLoadFailed")
            lifecycleScope.launch(Dispatchers.IO){
                lovinInterstitialAd?.destroy()
                delay(3000)
                lovinInterstitialAd = App.instance!!.lovinInterstitialAd(this@BaseActivity)
                lovinInterstitialAd!!.setListener(this@lovinInsterListener)
                lovinInterstitialAd!!.loadAd()
            }
        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            "lovin onAdDisplayFailed".loge("xxxxxxHonAdDisplayFailed")
            lifecycleScope.launch(Dispatchers.IO){
                lovinInterstitialAd?.destroy()
                delay(3000)
                lovinInterstitialAd = App.instance!!.lovinInterstitialAd(this@BaseActivity)
                lovinInterstitialAd!!.setListener(this@lovinInsterListener)
                lovinInterstitialAd!!.loadAd()
            }
        }
    }

}