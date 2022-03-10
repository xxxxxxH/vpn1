package win.enlarge.zoovpn.holder

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.shehuan.niv.NiceImageView
import org.greenrobot.eventbus.EventBus
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.event.MessageEvent
import win.enlarge.zoovpn.pojo.ResourceEntity
import win.enlarge.zoovpn.utils.ResourceManager
import win.enlarge.zoovpn.utils.loadWith
import zhan.auto_adapter.AutoHolder
import kotlin.random.Random

class LocationHolder(itemView:View,dataMap: MutableMap<String, Any>):AutoHolder<ResourceEntity>(itemView,dataMap) {

    private var locationItemIv:NiceImageView = itemView.findViewById(R.id.locationItemIv)
    private var locationItemTv:TextView = itemView.findViewById(R.id.locationItemTv)
    private var locationItemSignal:ImageView = itemView.findViewById(R.id.locationItemSignal)
    private var itemRoot:RelativeLayout = itemView.findViewById(R.id.itemRoot)

    override fun bind(p0: Int, p1: ResourceEntity?) {
        locationItemIv.setBackgroundResource(p1!!.id)
        locationItemTv.text = p1.name
        val index = (0..3).random()
        locationItemSignal.loadWith(ResourceManager.signals[index])
        itemRoot.setOnClickListener {
            EventBus.getDefault().post(MessageEvent("itemClick",p1))
        }
    }
}