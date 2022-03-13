package win.enlarge.zoovpn.ui.dialog

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.flyco.dialog.widget.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_exit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.event.MessageEvent

class DisconnectDialog(context: Context, val activity: Activity) : BaseDialog<DisconnectDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        EventBus.getDefault().register(this)
        return View.inflate(context, R.layout.dialog_exit, null)
    }

    override fun setUiBeforShow() {
        setCanceledOnTouchOutside(false)
        (activity as BaseActivity).lifecycleScope.launch(Dispatchers.IO){
            activity.getLovinNativeAdView()
        }
        yes.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("disconnectConfirm"))
        }
        no.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("disconnectCancel"))
        }
    }

    override fun onBackPressed() {

    }

    override fun dismiss() {
        super.dismiss()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "onNativeAdLoaded" -> {
                if (nad.childCount == 0){
                    nad.addView(msg[1] as View)
                }
            }
        }
    }
}