package win.enlarge.zoovpn.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting_new.*
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.ui.dialog.RateDialog
import win.enlarge.zoovpn.ui.dialog.RateUsDialog
import win.enlarge.zoovpn.utils.click

class SettingActivity : BaseActivity(R.layout.activity_setting_new) ,View.OnClickListener{
    
    private val rateDialog by lazy {
        RateUsDialog(this)
    }

    override fun onConvert() {
        settingCloseIv.setOnClickListener(this)
        settingRateRl.setOnClickListener(this)
        settingAboutRl.setOnClickListener(this)
        settingShareRl.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.settingCloseIv -> {

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