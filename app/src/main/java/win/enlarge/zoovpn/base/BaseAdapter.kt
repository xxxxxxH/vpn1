package win.enlarge.zoovpn.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import win.enlarge.zoovpn.utils.app
import win.enlarge.zoovpn.utils.click

abstract class BaseAdapter<T>(val data: List<T>) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BaseViewHolder(
            LayoutInflater.from(app).inflate(layoutId, parent, false)
        )

    override fun getItemCount() = data.size

    abstract val layoutId: Int

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val entity = data[position]
        holder.containerView.click {
            _onItemClickBlock?.invoke(entity)
        }
        onConvert(holder, entity, position)
    }

    abstract fun onConvert(holder: BaseViewHolder, item: T, position: Int)


    private var _onItemClickBlock:((T)->Unit)? = null

    fun setOnItemClickListener(onItemClickBlock:(T)->Unit) {
        _onItemClickBlock = onItemClickBlock
    }

    class BaseViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer

}
