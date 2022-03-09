package win.enlarge.zoovpn

import android.content.Context
import androidx.multidex.MultiDexApplication
import org.xutils.x


class App : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Ktx.initialize(this)
        x.Ext.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        Ktx.getInstance().initStartUp()
    }

}