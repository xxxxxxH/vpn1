package win.enlarge.zoovpn.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_unconnect.*
import org.greenrobot.eventbus.EventBus
import win.enlarge.zoovpn.BusUnConnectedEvent
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseDialog
import win.enlarge.zoovpn.utils.app
import win.enlarge.zoovpn.utils.click

class UnConnectDialog: BaseDialog() {

    companion object {
        fun newInstance() = UnConnectDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(app).inflate(R.layout.dialog_unconnect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogUnConnectedIvCancel.click {
            dismiss()
        }
        dialogUnConnectedIvSure.click {
            EventBus.getDefault().post(BusUnConnectedEvent)
            dismiss()
        }
    }

}