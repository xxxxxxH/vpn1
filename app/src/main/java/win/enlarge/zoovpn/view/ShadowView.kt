package win.enlarge.zoovpn.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.view_shadow.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import win.enlarge.zoovpn.R
import win.enlarge.zoovpn.pojo.ServerPojo
import win.enlarge.zoovpn.utils.click
import win.enlarge.zoovpn.utils.serverList
import win.enlarge.zoovpn.utils.timeOffset
import win.enlarge.zoovpn.utils.toPatternString

class ShadowView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {


    private var lifecycleOwner: LifecycleOwner? = null

    var currentStatus: ShadowStatus? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_shadow, this, true)
        lifecycleOwner = context as? LifecycleOwner
        startAnim()
    }

    fun bindClick(block: (View) -> Unit) {
        viewShadowIvMain.click {
            block(it)
        }
    }

    private fun startAnim() {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(viewShadowVBg, "alpha", 1F, 0F).apply {
                    repeatCount = ValueAnimator.INFINITE
                },
                ObjectAnimator.ofFloat(viewShadowVBg, "scaleX", 0F, 1F).apply {
                    repeatCount = ValueAnimator.INFINITE
                },
                ObjectAnimator.ofFloat(viewShadowVBg, "scaleY", 0F, 1F).apply {
                    repeatCount = ValueAnimator.INFINITE
                }
            )
            duration = 2600
            start()
        }
    }

    sealed class ShadowStatus

    object UnConnectShadowStatus : ShadowStatus()
    object ConnectingShadowStatus : ShadowStatus()
    object ConnectedShadowStatus : ShadowStatus()

    fun bindStatus(shadowStatus: ShadowStatus, serverPojo: ServerPojo? = null) {
        viewShadowIvStatus.rotation = 0F
        when (shadowStatus) {
            is UnConnectShadowStatus -> {
                rotationAnimator?.cancel()
                countDownJob?.cancel()
                viewShadowIvMain.setImageResource(R.drawable.activity_main_start)
                viewShadowIvStatus.setImageResource(R.drawable.view_shadow_start_status)
                viewShadowTvMain.text = "SELECT LOCATION"
            }
            is ConnectingShadowStatus -> {
                countDownJob?.cancel()
                viewShadowIvMain.setImageResource(R.drawable.activity_main_stop)
                viewShadowIvStatus.setImageResource(R.drawable.view_shadow_stop_status)
                viewShadowTvMain.text = "CONNECTING..."
                startRotation()
            }
            is ConnectedShadowStatus -> {
                rotationAnimator?.cancel()
                viewShadowIvMain.setImageResource(R.drawable.activity_main_off)
                viewShadowIvStatus.setImageResource(R.drawable.view_shadow_off_status)
                startCountDown()
            }
        }
        serverPojo?.let {
            viewShadowIvIcon.setImageResource(it.icon)
        }
        currentStatus = shadowStatus
    }

    private var countDownJob: Job? = null

    private fun startCountDown() {
        countDownJob?.cancel()
        connectedTime = System.currentTimeMillis()
        countDownJob = lifecycleOwner?.lifecycleScope?.launch(Dispatchers.IO) {
            repeat(Int.MAX_VALUE) {
                (System.currentTimeMillis() - connectedTime)
                    .timeOffset()
                    .toPatternString()
                    .let {
                        withContext(Dispatchers.Main) {
                            viewShadowTvMain.text = it
                        }
                    }
            }
        }
    }

    private var connectedTime = -1L

    private var rotationAnimator: Animator? = null

    private fun startRotation() {
        rotationAnimator?.cancel()
        rotationAnimator = ObjectAnimator.ofFloat(viewShadowIvStatus, "rotation", 0F, 360F).apply {
            interpolator = LinearInterpolator()
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            start()
        }
    }
}