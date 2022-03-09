package win.enlarge.zoovpn

import win.enlarge.zoovpn.pojo.ServerPojo

sealed class BusEvent

class BusServerEvent(val serverPojo: ServerPojo) : BusEvent()

object BusConnectedEvent : BusEvent()

object BusUnConnectedEvent : BusEvent()
