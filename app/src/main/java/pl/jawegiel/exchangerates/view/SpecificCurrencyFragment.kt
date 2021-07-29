package pl.jawegiel.exchangerates.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.specific_currency_fragment.*
import pl.jawegiel.exchangerates.R

// @formatter:off
class SpecificCurrencyFragment : Fragment() {

    companion object {
        const val MAP_DATA_RECEIVE = "map_data_receive"
        const val DAY_DATA_RECEIVE = "day_data_receive"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.specific_currency_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        if (args != null) {
            val receivedHashMap = args.getSerializable(MAP_DATA_RECEIVE) as HashMap<*, *>
            val key = receivedHashMap.keys.toTypedArray()[0]
            tv_currency_name.text = key as CharSequence?
            tv_currency_value.text = receivedHashMap[key] as CharSequence?
            tv_currency_day.text = java.lang.String.format(getString(R.string.day_x), args.getString(DAY_DATA_RECEIVE))
        }
    }
}