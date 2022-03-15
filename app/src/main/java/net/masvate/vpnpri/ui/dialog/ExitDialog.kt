package net.masvate.vpnpri.ui.dialog

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
import net.masvate.vpnpri.R
import net.masvate.vpnpri.base.BaseActivity
import net.masvate.vpnpri.event.MessageEvent

class ExitDialog(context: Context, val activity: Activity): BaseDialog<ExitDialog>(context) {
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
            EventBus.getDefault().post(MessageEvent("confirmExit"))
        }
        no.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("cancelExit"))
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