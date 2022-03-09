package win.enlarge.zoovpn.view.lovin

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.utils.app
import win.enlarge.zoovpn.utils.loge
import win.enlarge.zoovpn.utils.lovinSdk

class LovinNativeAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        MaxNativeAdLoader(app.getString(R.string.lovin_native_ad_id), lovinSdk, context).apply {
            setNativeAdListener(object : MaxNativeAdListener() {
                override fun onNativeAdLoadFailed(p0: String?, p1: MaxError?) {
                    super.onNativeAdLoadFailed(p0, p1)
                    "MaxNativeAdLoader onNativeAdLoadFailed $p0 $p1".loge()
                }

                override fun onNativeAdLoaded(p0: MaxNativeAdView?, p1: MaxAd?) {
                    "MaxNativeAdLoader onNativeAdLoaded".loge()
                    super.onNativeAdLoaded(p0, p1)
                    p0?.let {
                        removeAllViews()
                        addView(it)
                    }
                }
            })
            loadAd()
        }
    }
}