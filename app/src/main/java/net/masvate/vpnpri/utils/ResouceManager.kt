package net.masvate.vpnpri.utils

import net.masvate.vpnpri.R
import net.masvate.vpnpri.pojo.ResourceEntity

object ResourceManager {

    val signals = arrayOf(
        R.drawable.activity_server_signal_0,
        R.drawable.activity_server_signal_1,
        R.drawable.activity_server_signal_2,
        R.drawable.activity_server_signal_3
    )

    fun getResource(): ArrayList<ResourceEntity> {
        val result = ArrayList<ResourceEntity>()
        val e1 = ResourceEntity(R.drawable.activity_server_usa, "USA-New York")
        val e2 = ResourceEntity(R.drawable.activity_server_usa, "USA-Los Angeles")
        val e3 = ResourceEntity(R.drawable.activity_server_australia, "AUS-Sydney")
        val e4 = ResourceEntity(R.drawable.activity_server_england, "US-London")
        val e5 = ResourceEntity(R.drawable.activity_server_germany, "GER-Berlin")
        val e6 = ResourceEntity(R.drawable.activity_server_italy, "ITA-Roma")
        val e7 = ResourceEntity(R.drawable.activity_server_singapore, "SG-Singapore City")
        val e8 = ResourceEntity(R.drawable.activity_server_spain, "ES-Madrid")
        result.add(e1)
        result.add(e2)
        result.add(e3)
        result.add(e4)
        result.add(e5)
        result.add(e6)
        result.add(e7)
        result.add(e8)
        return result
    }
}