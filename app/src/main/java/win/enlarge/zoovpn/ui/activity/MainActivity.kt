package win.enlarge.zoovpn.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_content.*
import kotlinx.android.synthetic.main.layout_main_top.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import win.enlarge.zoovpn.App
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.base.BaseActivity
import win.enlarge.zoovpn.event.MessageEvent
import win.enlarge.zoovpn.pojo.ResourceEntity
import win.enlarge.zoovpn.ui.dialog.DisconnectDialog
import win.enlarge.zoovpn.ui.dialog.ExitDialog
import win.enlarge.zoovpn.utils.requestPermission
import win.enlarge.zoovpn.utils.timeOffset
import win.enlarge.zoovpn.utils.toPatternString

class MainActivity : BaseActivity(R.layout.activity_main), View.OnClickListener {

    private val entity by lazy {
        MMKV.defaultMMKV().decodeParcelable("location", ResourceEntity::class.java)
    }

    private val exitDialog by lazy {
        ExitDialog(this, this)
    }

    private val disconnectDialog by lazy {
        DisconnectDialog(this,this)
    }

    private var countDownJob: Job? = null
    private var connectedTime = -1L

    override fun onConvert() {
        EventBus.getDefault().register(this)
        mainLocationRl.setOnClickListener(this)
        mainTopOption.setOnClickListener(this)
        requestPermission {
            lifecycleScope.launch(Dispatchers.IO){
                getLovinNativeAdView()
                val banner = App.instance!!.lovinBanner()
                banner.loadAd()
                withContext(Dispatchers.Main){
                    if (lovinBannerAdViewFl.childCount == 0){
                        lovinBannerAdViewFl.addView(banner)
                    }
                }
            }
            setStatus(STATUS.DEFAULT)
            entity?.let {
                setLocation(it)
            }
        }
    }

    override fun onBackPressed() {
        exitDialog.show()
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setStatus(status: STATUS) {
        mainContentStatus.removeAllViews()
        var view: View? = null
        when (status) {
            STATUS.DEFAULT -> {
                view = layoutInflater.inflate(R.layout.layout_main_content_status_default, null)
                view.findViewById<RelativeLayout>(R.id.mainOptionRl).setOnClickListener(this)
                mainStatusCurrentTv.text = " Not Connected"
            }
            STATUS.ING -> {
                view = layoutInflater.inflate(R.layout.layout_main_content_status_ing, null)
                val progress = view.findViewById<ProgressBar>(R.id.mainProgressBar)
                progress.max = 3
                mainStatusCurrentTv.text = " Connecting"
                countDownCoroutines(
                    3,
                    { progress.progress = it },
                    { setStatus(STATUS.CONNECTED) },
                    lifecycleScope
                )
            }
            STATUS.CONNECTED -> {
                view = layoutInflater.inflate(R.layout.layout_main_content_status_connected, null)
                val time = view.findViewById<TextView>(R.id.mainStatusTimeTv)
                view.findViewById<ImageView>(R.id.mainStatusIcon).setOnClickListener(this)
                mainStatusCurrentTv.text = " Connected"
                startCountDown(time)
            }
        }
        view?.let {
            mainContentStatus.addView(it)
        }
    }

    private fun setLocation(entity: ResourceEntity) {
        entity.apply {
            mainLocationTv.text = name
            mainLocationFlagIv.visibility = View.VISIBLE
            mainLocationFlagIv.setImageResource(id)
        }
    }

    @SuppressLint("CheckResult")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mainOptionRl -> {
                val a = showInsertAd()
                if (!a) {
                    if (entity == null) {
                        Toasty.error(this, "please select a node").show()
                    } else {
                        setStatus(STATUS.ING)
                    }
                }
            }
            R.id.mainStatusIcon -> {
                disconnectDialog.show()
            }
            R.id.mainLocationRl -> {
                startActivity(Intent(this, ServerActivity::class.java))
            }
            R.id.mainTopOption -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
    }

    private fun countDownCoroutines(
        total: Int, onTick: (Int) -> Unit, onFinish: () -> Unit,
        scope: CoroutineScope = GlobalScope
    ): Job {
        return flow {
            for (i in 0..total) {
                emit(i)
                delay(1000)
            }
        }.flowOn(Dispatchers.Default)
            .onCompletion { onFinish.invoke() }
            .onEach { onTick.invoke(it) }
            .flowOn(Dispatchers.Main)
            .launchIn(scope)
    }

    private fun startCountDown(tv: TextView) {
        countDownJob?.cancel()
        connectedTime = System.currentTimeMillis()
        countDownJob = lifecycleScope.launch(Dispatchers.IO) {
            repeat(Int.MAX_VALUE) {
                (System.currentTimeMillis() - connectedTime)
                    .timeOffset()
                    .toPatternString()
                    .let {
                        withContext(Dispatchers.Main) {
                            tv.text = it
                        }
                    }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "location" -> {
                val entity = msg[1] as ResourceEntity
                setLocation(entity)
            }
            "confirmExit" -> {
                finish()
            }
            "cancelExit" -> {
                exitDialog.dismiss()
            }
            "disconnectConfirm" -> {
                setStatus(STATUS.DEFAULT)
                disconnectDialog.dismiss()
            }
            "disconnectCancel" -> {
                disconnectDialog.dismiss()
            }
            "onNativeAdLoaded" -> {
                if (lovinNativeAdViewFl.childCount == 0){
                    lovinNativeAdViewFl.addView(msg[1] as View)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    enum class STATUS {
        DEFAULT,
        ING,
        CONNECTED
    }
}