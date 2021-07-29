package pl.jawegiel.exchangerates.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.subitem.view.*
import pl.jawegiel.exchangerates.R
import pl.jawegiel.exchangerates.interfaces.OnChangeFragment
import pl.jawegiel.exchangerates.model.ChangeFragmentData
import pl.jawegiel.exchangerates.model.Currency
import pl.jawegiel.exchangerates.view.SpecificCurrencyFragment

// @formatter:off
class SubitemAdapter(
    private val subitems: List<Currency>,
    private val day: String,
    private val activity: Activity,
) : RecyclerView.Adapter<SubitemAdapter.SubitemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SubitemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subitem, parent, false)
        return SubitemViewHolder(view, day, activity)
    }

    override fun onBindViewHolder(holder: SubitemViewHolder, position: Int) {
        holder.bind(subitems[position])
    }

    override fun getItemCount() = subitems.size

    class SubitemViewHolder(view: View, private val day: String, private val activity: Activity) : RecyclerView.ViewHolder(view) {
        private val subitemRootView = view.subitem_root
        private val tvCurrencyName = view.tv_currency_name
        private val tvCurrencyValue = view.tv_currency_value

        fun bind(currency: Currency) {
            subitemRootView.setOnClickListener {
                activity as OnChangeFragment
                activity.changeFragment(
                    SpecificCurrencyFragment(),
                    ChangeFragmentData(hashMapOf(currency.currencyName to currency.currencyValue.toString()), day)
                )
            }
            tvCurrencyName.text = currency.currencyName
            tvCurrencyValue.text = currency.currencyValue.toString()
        }
    }
}