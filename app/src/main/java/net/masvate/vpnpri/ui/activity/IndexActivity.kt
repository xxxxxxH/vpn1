package net.masvate.vpnpri.ui.activity

import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.masvate.vpnpri.App
import net.masvate.vpnpri.R
import net.masvate.vpnpri.base.BaseActivity
import net.masvate.vpnpri.utils.*

class IndexActivity : BaseActivity(R.layout.activity_splash) {

    override fun onConvert() {
        lifecycleScope.launch(Dispatchers.IO) {
            val banner = App.instance!!.lovinBanner()
            banner.loadAd()
            withContext(Dispatchers.Main){
                adView.addView(banner)
            }
        }
        lifecycleScope.requestConfig {
            if (configEntity.needLogin()) {
                if (configEntity.needDeepLink() && configEntity.faceBookId().isNotBlank()) {
                    fetchAppLink(configEntity.faceBookId()) {
                        it?.let {
                            isRealDeepLink = true
                        }
                        showStepTwo()
                    }
                } else {
                    showStepTwo()
                }
            } else {
                showStepTwo()
            }
        }

        activitySplashTv.click {
            startActivity(Intent(this@IndexActivity, MainActivity::class.java))
            finish()
        }
    }

    private var isByShowStepTwo = false

    private fun showStepTwo() {
        runOnUiThread {
            if (showOpenAd(activitySplashRl, isForce = true)) {
                isByShowStepTwo = true
            } else {
                jumpByConfig()
            }
        }
    }

    private fun jumpByConfig() {
        if (configEntity.vpnStepStatus() == 1) {
            activitySplashPb.visibility = View.GONE
            activitySplashLl.visibility = View.VISIBLE
        } else {
            startActivity(Intent(this@IndexActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onSplashAdHidden() {
        super.onSplashAdHidden()
        if (isByShowStepTwo) {
            isByShowStepTwo = false
            jumpByConfig()
        }
    }

    override fun onInterstitialAdHidden() {
        super.onInterstitialAdHidden()
        if (isByShowStepTwo) {
            isByShowStepTwo = false
            jumpByConfig()
        }
    }


}