package net.masvate.vpnpri

import net.masvate.vpnpri.pojo.ServerPojo

sealed class BusEvent

class BusServerEvent(val serverPojo: ServerPojo) : BusEvent()

object BusConnectedEvent : BusEvent()

object BusUnConnectedEvent : BusEvent()
