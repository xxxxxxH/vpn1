package win.enlarge.zoovpn.ui.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_exit.*
import org.greenrobot.eventbus.EventBus
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.event.MessageEvent

class ExitDialog(context: Context): BaseDialog<ExitDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        return View.inflate(context, R.layout.dialog_exit, null)
    }

    override fun setUiBeforShow() {
        setCanceledOnTouchOutside(false)
        yes.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("confirmExit"))
        }
        no.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("cancelExit"))
        }
    }
}