package pl.jawegiel.exchangerates.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import pl.jawegiel.exchangerates.interfaces.CurrencyListFragmentContract

// @formatter:off
class SharedPreferencesModel(activity: Activity) : CurrencyListFragmentContract.Model.SharedPreferencesModel {

    companion object {
        const val HOW_MANY_DAYS = "HOW_MANY_DAYS"
        const val DEFAULT_NUMBER_OF_DAYS = 0
    }
    private val sp = activity.getPreferences(Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sp.edit()

    override fun getRawJson(date: String): String {
        return sp.getString(date, "").toString()
    }

    override fun checkIfSuchDateExists(strDate: String?): Boolean {
        return sp.getString(strDate, null) != null
    }

    override fun saveRawJson(rawJson: String, date: String) {
        editor.putString(date, rawJson)
        editor.apply()
    }

    override fun saveNumberOfMinusDays(howManyDays: Int) {
        editor.putInt(HOW_MANY_DAYS, howManyDays)
        editor.apply()
    }

    override fun getNumberOfMinusDays(): Int {
        return sp.getInt(HOW_MANY_DAYS, DEFAULT_NUMBER_OF_DAYS)
    }
}