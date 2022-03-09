package win.enlarge.zoovpn.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_rate.*
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseDialog
import win.enlarge.zoovpn.utils.app
import win.enlarge.zoovpn.utils.click

class RateDialog : BaseDialog() {

    companion object {
        fun newInstance() = RateDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutInflater.from(app).inflate(R.layout.dialog_rate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogRateIvCancel.click {
            dismiss()
        }
        dialogRateIvSure.click {
            dismiss()
        }
    }
}