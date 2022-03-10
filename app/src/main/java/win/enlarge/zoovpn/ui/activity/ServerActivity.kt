package win.enlarge.zoovpn.ui.activity

import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_loaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.event.MessageEvent
import win.enlarge.zoovpn.holder.LocationHolder
import win.enlarge.zoovpn.pojo.ResourceEntity
import win.enlarge.zoovpn.utils.ResourceManager
import win.enlarge.zoovpn.utils.mmkv
import zhan.auto_adapter.AutoRecyclerAdapter

class ServerActivity : BaseActivity(R.layout.activity_loaction) {

    var data: ArrayList<ResourceEntity>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onConvert() {
        EventBus.getDefault().register(this)
        val adapter = AutoRecyclerAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        adapter.setHolder(LocationHolder::class.java, R.layout.layout_item_location)
        lifecycleScope.launch(Dispatchers.IO) {
            data = ResourceManager.getResource()
            withContext(Dispatchers.Main) {
                adapter.setDataList(LocationHolder::class.java, data).notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        if (msg[0] == "itemClick") {
            val entity = msg[1] as ResourceEntity
            MMKV.defaultMMKV().encode("location",entity)
            EventBus.getDefault().post(MessageEvent("location", entity))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}