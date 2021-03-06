package net.masvate.vpnpri.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_setting_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.masvate.vpnpri.App
import net.masvate.vpnpri.R
import net.masvate.vpnpri.base.BaseActivity
import net.masvate.vpnpri.ui.dialog.RateUsDialog

class SettingActivity : BaseActivity(R.layout.activity_setting_new) ,View.OnClickListener{
    
    private val rateDialog by lazy {
        RateUsDialog(this)
    }

    override fun onConvert() {
        lifecycleScope.launch(Dispatchers.IO) {
            val banner = App.instance!!.lovinBanner()
            banner.loadAd()
            withContext(Dispatchers.Main){
                if (adView.childCount == 0){
                    adView.addView(banner)
                }
            }
        }
        settingCloseIv.setOnClickListener(this)
        settingRateRl.setOnClickListener(this)
        settingAboutRl.setOnClickListener(this)
        settingShareRl.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.settingCloseIv -> {
                finish()
            }
            R.id.settingRateRl -> {
                rateDialog.show()
            }
            R.id.settingAboutRl -> {

            }
            R.id.settingShareRl -> {
                sendEmil("Life is a fking moving")
            }
        }
    }

    private fun sendEmil(text: String) {
        val reveiverString = arrayOf("nuclearvpnp@outlook.com")
        val ccString = arrayOf<String>()
        val subjectString = "feedBook"
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, reveiverString)
        intent.putExtra(Intent.EXTRA_CC, ccString)
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectString)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, "Choose Email Client"))
    }

}