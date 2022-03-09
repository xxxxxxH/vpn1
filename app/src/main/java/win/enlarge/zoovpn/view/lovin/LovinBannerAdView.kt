package win.enlarge.zoovpn.view.lovin

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.utils.app
import win.enlarge.zoovpn.utils.loge
import win.enlarge.zoovpn.utils.lovinSdk

class LovinBannerAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        MaxAdView(app.getString(R.string.lovin_banner_ad_id), lovinSdk, context).apply {
            this@LovinBannerAdView.addView(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setListener(object : MaxAdViewAdListener {
                override fun onAdExpanded(ad: MaxAd?) {
                    "MaxAdView onAdExpanded".loge()
                }

                override fun onAdCollapsed(ad: MaxAd?) {
                    "MaxAdView onAdCollapsed".loge()
                }

                override fun onAdLoaded(ad: MaxAd?) {
                    "MaxAdView onAdLoaded".loge()
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    "MaxAdView onAdDisplayed".loge()
                }

                override fun onAdHidden(ad: MaxAd?) {
                    "MaxAdView onAdHidden".loge()
                }

                override fun onAdClicked(ad: MaxAd?) {
                    "MaxAdView onAdClicked".loge()
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError) {
                    "MaxAdView onAdLoadFailed $adUnitId $error".loge()
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    "MaxAdView onAdDisplayFailed $ad $error".loge()
                }
            })
            loadAd()
        }
    }
}