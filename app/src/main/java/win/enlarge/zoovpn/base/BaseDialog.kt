package win.enlarge.zoovpn.base

import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

abstract class BaseDialog : DialogFragment(), DialogInterface.OnKeyListener {

    open val dispatchDialogCancel: Boolean
        get() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            it.setOnKeyListener(this)
            it.setCanceledOnTouchOutside(!dispatchDialogCancel)
            it.window?.let {
                it.decorView.setPadding(0, 0, 0, 0)
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.setLayout(
                    (Resources.getSystem().displayMetrics.widthPixels * 0.9).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            dispatchDialogCancel
        } else false
    }

}