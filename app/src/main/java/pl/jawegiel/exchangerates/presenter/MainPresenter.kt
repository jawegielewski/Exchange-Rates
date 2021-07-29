package pl.jawegiel.exchangerates.presenter

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import pl.jawegiel.exchangerates.interfaces.CurrencyListFragmentContract
import pl.jawegiel.exchangerates.model.ApiResponse
import pl.jawegiel.exchangerates.model.Currency
import pl.jawegiel.exchangerates.model.SharedPreferencesModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

// @formatter:off
class MainPresenter(
    private val view: CurrencyListFragmentContract.View,
    private val restModel: CurrencyListFragmentContract.Model.RestModel,
    private val spModel: SharedPreferencesModel
    ) : CurrencyListFragmentContract.Presenter {

    companion object {
        const val NUMBER_OF_MORE_ELEMENTS = 1
        const val DELAY_TO_SHOW_OF_MY_PROGRESS_BAR = 1000L
        const val DIRECTION_TO_DOWN = 1
        const val NO_ELEMENTS = 0
        const val ONE_ELEMENT = 1
        const val PROPER_DATE_FORMAT = "yyyy-MM-dd"
        const val POLISH_LOCALE = "pl"
    }

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun makeACall(date: String?) {
        date?.let { restModel.fetchApiResponse(this, it) }
    }

    override fun loadMoreDays() {
        view.setRecyclerViewStateToLoading()
        var numberOfDays = getNumberOfMinusDays()
        numberOfDays++
        saveNumberOfMinusDaysIntoSp(numberOfDays)
        val dateMinusXDays = currentDateMinusXDaysToStr(numberOfDays)
        val nextLimit = view.getApiResponseList().size + NUMBER_OF_MORE_ELEMENTS
        for (i in view.getApiResponseList().size until nextLimit) {
            if (checkIfSuchDateExistsInSp(dateMinusXDays)) {
                view.showLogAboutExistingDateInSp(dateMinusXDays)
                handler.postDelayed({
                    processDateWithoutMakingACall(dateMinusXDays)
                }, DELAY_TO_SHOW_OF_MY_PROGRESS_BAR)
            } else {
                view.showLogAboutNotExistingDateInSp(dateMinusXDays)

                makeACall(dateMinusXDays)
            }
        }
        view.notifyChangedItemAdapter()
    }

    override fun processRvItemOnScroll(isLoading: Boolean, rvItem: RecyclerView, newState: Int): Boolean {
        var _isLoading = isLoading
        if (!_isLoading) {
            if (!rvItem.canScrollVertically(DIRECTION_TO_DOWN) && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                loadMoreDays()
                _isLoading = true
            }
        }
        return _isLoading
    }

    override fun checkIfSetRecyclerViewStateToLoading(apiResponseSize: Int) {
        if (apiResponseSize > NO_ELEMENTS)
            view.setRecyclerViewStateToLoading()
    }

    override fun checkIfRemoveRecyclerViewStateOfLoading(apiResponseSize: Int) {
        if (apiResponseSize > ONE_ELEMENT)
            view.removeRecyclerViewStetOfLoading()

    }

    override fun passErrorResponseMessageToView(errorResponse: String) {
        view.showErrorResponseToast(errorResponse)
    }

    fun processDateWithoutMakingACall(date: String) {
        val apiResponse = processRawJson(spModel.getRawJson(date))
        passResponseToView(apiResponse)
    }

    override fun convertCurrentDate(): String {
        return SimpleDateFormat(PROPER_DATE_FORMAT, Locale(POLISH_LOCALE)).format(Date())
    }

    private fun currentDateMinusXDaysToStr(minusDays: Int): String {
        var date: Date? = null
        try {
            date = Date()
            val c = Calendar.getInstance()
            c.time = date
            c.add(Calendar.DATE, -minusDays)
            date = c.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return SimpleDateFormat(PROPER_DATE_FORMAT, Locale(POLISH_LOCALE)).format(date!!)
    }

    override fun passResponseToView(apiResponse: ApiResponse?) {
        checkIfRemoveRecyclerViewStateOfLoading(view.getApiResponseList().size)
        view.assignResponseToRecyclerview(apiResponse)
    }

    fun checkIfSuchDateExistsInSp(strDate: String?): Boolean{
        return spModel.checkIfSuchDateExists(strDate)
    }

    fun saveNumberOfMinusDaysIntoSp(howManyDays: Int) {
        spModel.saveNumberOfMinusDays(howManyDays)
    }

    private fun getNumberOfMinusDays(): Int {
        return spModel.getNumberOfMinusDays()
    }

    override fun processRawJson(rawJson: String): ApiResponse {
        val parser = JsonParser()
        val rootObj = parser.parse(rawJson).asJsonObject
        val ratesArrayList: ArrayList<Currency> = ArrayList()
        val rootKeys = rootObj.keySet()
        var baseValue = ""
        var dateValue = ""
        var ratesObj = JsonObject()
        var ratesKeys: Set<String> = HashSet()
        for (key in rootKeys) {
            if (key == "base")
                baseValue = rootObj.get(key).asString
            if (key == "date")
                dateValue = rootObj.get(key).asString
            if (key == "rates") {
                ratesObj = rootObj.get(key).asJsonObject
                ratesKeys = ratesObj.keySet()

            }
        }
        for (key2 in ratesKeys) {
            ratesArrayList.add(Currency(key2, ratesObj.get(key2).asFloat))
        }
        if(!spModel.checkIfSuchDateExists(dateValue))
            spModel.saveRawJson(rawJson, dateValue)
        return ApiResponse(baseValue, dateValue, ratesArrayList, false)
    }
}