package win.enlarge.zoovpn.ui.activity

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_server.*
import kotlinx.android.synthetic.main.item_server.*
import org.greenrobot.eventbus.EventBus
import win.enlarge.zoovpn.BusServerEvent
import win.enlarge.zoovpn.Constant
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.base.BaseAdapter
import win.enlarge.zoovpn.pojo.ServerPojo
import win.enlarge.zoovpn.utils.click
import win.enlarge.zoovpn.utils.serverList

class ServerActivity : BaseActivity(R.layout.activity_server) {


    private var currentServerPojo: ServerPojo? = null

    override fun onConvert() {
        (intent?.getSerializableExtra(Constant.KEY_COMMON_ARGUMENT) as? ServerPojo)?.let {
            currentServerPojo = it
        }
        activityServerIvBack.click {
            onBackPressed()
        }
        val data = serverList.toMutableList().apply {
            forEach {
                if (it == currentServerPojo) {
                    it.isSelect = true
                }
            }
        }
        activityServerRv.apply {
            layoutManager = LinearLayoutManager(this@ServerActivity)
            adapter = object : BaseAdapter<ServerPojo>(data) {
                override val layoutId: Int
                    get() = R.layout.item_server

                override fun onConvert(holder: BaseViewHolder, item: ServerPojo, position: Int) {
                    holder.itemServerIvDot.isInvisible = !item.isSelect
                    holder.itemServerIvIcon.setImageResource(item.icon)
                    holder.itemServerTvName.text = item.name
                    holder.itemServerIvSignal.setImageResource(item.signal)
                }
            }.apply {
                setOnItemClickListener {
                    if (!it.isSelect) {
                        selectServerPojo = it
                        serverList.forEach {
                            it.isSelect = false
                        }
                        it.isSelect = true
                        notifyDataSetChanged()
                        val a = showInsertAd()
                        if (a) {

                        } else {
                            sendEventBus()
                        }
                    }
                }
            }
        }
    }

    private var selectServerPojo: ServerPojo? = null

    private fun sendEventBus() {
        selectServerPojo?.let {
            EventBus.getDefault().post(BusServerEvent(it))
            onBackPressed()
        }
    }

    override fun onInterstitialAdHidden() {
        super.onInterstitialAdHidden()
        sendEventBus()
    }
}