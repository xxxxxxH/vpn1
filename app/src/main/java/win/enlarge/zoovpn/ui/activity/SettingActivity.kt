package win.enlarge.zoovpn.ui.activity

import kotlinx.android.synthetic.main.activity_setting.*
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.ui.dialog.RateDialog
import win.enlarge.zoovpn.utils.click

class SettingActivity : BaseActivity(R.layout.activity_setting) {

    override fun onConvert() {
        activitySettingIvBack.click {
            finish()
        }
        activitySettingFlRate.click {
            RateDialog.newInstance().show(supportFragmentManager, "")
        }
    }
}