package win.enlarge.zoovpn.ui.dialog

import android.content.Context
import android.view.View
import com.flyco.dialog.widget.base.BaseDialog
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialog_exit.*
import win.enlarge.zoovpn.R

class RateUsDialog(context: Context) : BaseDialog<RateUsDialog>(context) {
    override fun onCreateView(): View {
        widthScale(0.85f)
        return View.inflate(context, R.layout.dialog_rate_us, null)
    }

    override fun setUiBeforShow() {
        setCanceledOnTouchOutside(false)
        yes.setOnClickListener {
            dismiss()
            Toasty.success(context, "Thank you").show()
        }
        no.setOnClickListener {
            dismiss()
        }
    }
}