package net.masvate.vpnpri.pojo

import java.io.Serializable

data class ServerPojo(
     val icon: Int = 0,
     val name: String? = "",
     val signal: Int = 0,
     var isSelect: Boolean = false
) : Ipojo,Serializable{

     override fun equals(other: Any?): Boolean {
          if (this === other) return true
          if (javaClass != other?.javaClass) return false

          other as ServerPojo

          if (icon != other.icon) return false
          if (name != other.name) return false
          if (signal != other.signal) return false

          return true
     }

     override fun hashCode(): Int {
          var result = icon
          result = 31 * result + (name?.hashCode() ?: 0)
          result = 31 * result + signal
          return result
     }
}