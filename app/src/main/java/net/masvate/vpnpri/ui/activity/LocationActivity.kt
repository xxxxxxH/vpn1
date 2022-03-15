package net.masvate.vpnpri.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
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
import net.masvate.vpnpri.App
import net.masvate.vpnpri.R
import net.masvate.vpnpri.base.BaseActivity
import net.masvate.vpnpri.event.MessageEvent
import net.masvate.vpnpri.holder.LocationHolder
import net.masvate.vpnpri.pojo.ResourceEntity
import net.masvate.vpnpri.utils.ResourceManager
import net.masvate.vpnpri.utils.isLogin
import zhan.auto_adapter.AutoRecyclerAdapter

class LocationActivity : BaseActivity(R.layout.activity_loaction) {

    var data: ArrayList<ResourceEntity>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onConvert() {
        EventBus.getDefault().register(this)
        lifecycleScope.launch(Dispatchers.IO){
            val banner = App.instance!!.lovinBanner()
            banner.loadAd()
            withContext(Dispatchers.Main){
                if (adView.childCount == 0){
                    adView.addView(banner)
                }
            }
        }
        val adapter = AutoRecyclerAdapter()
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)
        adapter.setHolder(LocationHolder::class.java, R.layout.layout_item_location)
        data = ResourceManager.getResource()
        adapter.setDataList(LocationHolder::class.java, data).notifyDataSetChanged()
        locationCloseIv.setOnClickListener { finish() }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        if (msg[0] == "itemClick") {
            if (isLogin) {
                val entity = msg[1] as ResourceEntity
                MMKV.defaultMMKV().encode("location", entity)
                EventBus.getDefault().post(MessageEvent("location", entity))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}