package net.masvate.vpnpri.holder

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.itheima.roundedimageview.RoundedImageView
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import net.masvate.vpnpri.R
import net.masvate.vpnpri.event.MessageEvent
import net.masvate.vpnpri.pojo.ResourceEntity
import net.masvate.vpnpri.ui.activity.LoginActivity
import net.masvate.vpnpri.utils.ResourceManager
import net.masvate.vpnpri.utils.isLogin
import zhan.auto_adapter.AutoHolder

class LocationHolder(itemView: View?, dataMap: MutableMap<String, Any>?) :
    AutoHolder<ResourceEntity>(itemView, dataMap) {

    private var locationItemIv: RoundedImageView = itemView!!.findViewById(R.id.locationItemIv)
    private var locationItemTv: TextView = itemView!!.findViewById(R.id.locationItemTv)
    private var locationItemSignal: ImageView = itemView!!.findViewById(R.id.locationItemSignal)
    private var itemRoot: RelativeLayout = itemView!!.findViewById(R.id.itemRoot)

    override fun bind(p0: Int, p1: ResourceEntity?) {
        locationItemIv.setImageResource(p1!!.id)
        locationItemTv.text = p1.name
        val index = (0..3).random()
        locationItemSignal.setBackgroundResource(ResourceManager.signals[index])
        itemRoot.setOnClickListener {
            if (isLogin) {
                MMKV.defaultMMKV().encode("location", p1)
                EventBus.getDefault().post(MessageEvent("location", p1))
                (itemView.context as Activity).finish()
            } else {
                itemView.context.startActivity(Intent(itemView.context, LoginActivity::class.java))
            }
        }
    }
}