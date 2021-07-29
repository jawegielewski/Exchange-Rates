package pl.jawegiel.exchangerates.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*
import pl.jawegiel.exchangerates.R
import pl.jawegiel.exchangerates.model.ApiResponse
import kotlin.String

// @formatter:off
class ItemAdapter(private var items: MutableList<ApiResponse>, private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATA = 0
        private const val VIEW_TYPE_PROGRESS = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return when (p1) {
            VIEW_TYPE_DATA -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
                DataViewHolder(view, activity)
            }
            VIEW_TYPE_PROGRESS -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.progress_bar_layout, parent, false)
                ProgressViewHolder(view)
            }
            else -> throw IllegalArgumentException("Different View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder)
            holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        val viewType = items[position]
        return when (viewType.isLoading) {
            true -> VIEW_TYPE_PROGRESS
            false -> VIEW_TYPE_DATA
        }
    }

    class DataViewHolder(view: View, private val activity: Activity) : RecyclerView.ViewHolder(view) {
        private var isRvSubitemVisible = false
        private val tvDate = view.tv_date
        private val rvSubitem = view.rv_subitem

        fun bind(apiResponse: ApiResponse) {
            tvDate.text = String.format(itemView.context.getString(R.string.day_x), apiResponse.date)
            tvDate.setOnClickListener {
                if (isRvSubitemVisible) {
                    rvSubitem.visibility = View.GONE
                    isRvSubitemVisible = false
                } else {
                    rvSubitem.visibility = View.VISIBLE
                    isRvSubitemVisible = true
                }
            }
            rvSubitem.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = SubitemAdapter(apiResponse.rates, apiResponse.date, activity)
            }
        }
    }

    inner class ProgressViewHolder(view: View) : RecyclerView.ViewHolder(view)
}