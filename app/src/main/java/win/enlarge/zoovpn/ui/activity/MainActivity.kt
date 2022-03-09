package win.enlarge.zoovpn.ui.activity

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import win.enlarge.zoovpn.*
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.callback.IDialogCallBack
import win.enlarge.zoovpn.pojo.ServerPojo
import win.enlarge.zoovpn.ui.dialog.ContentDialog
import win.enlarge.zoovpn.ui.dialog.ConnectedDialog
import win.enlarge.zoovpn.ui.dialog.UnConnectDialog
import win.enlarge.zoovpn.utils.*
import win.enlarge.zoovpn.view.ShadowView
import kotlin.random.Random

class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onConvert() {
        requestPermission()
    }

    override fun onBackPressed() {

    }

}