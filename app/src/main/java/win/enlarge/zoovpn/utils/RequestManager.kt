package win.enlarge.zoovpn.utils

import android.util.Log
import org.xutils.common.Callback
import org.xutils.http.RequestParams
import org.xutils.x
import win.enlarge.zoovpn.R


object RequestManager {
   suspend fun getConfig():String?{
        var r:String?=null
        val params = RequestParams(app.getString(R.string.base_url) + "config")
        x.http().get(params,object :Callback.CommonCallback<String>{
            override fun onSuccess(result: String?) {
                "xxxxxxHonSuccess -> $result".loge()
                r = result
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                "xxxxxxHonError -> ${ex.toString()}".loge()
            }

            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onFinished() {

            }

        })
        return r
    }
}