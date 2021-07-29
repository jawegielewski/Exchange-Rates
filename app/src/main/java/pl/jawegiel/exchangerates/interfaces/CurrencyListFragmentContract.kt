package pl.jawegiel.exchangerates.interfaces

import androidx.recyclerview.widget.RecyclerView
import pl.jawegiel.exchangerates.model.ApiResponse
import pl.jawegiel.exchangerates.presenter.CurrencyListFragmentPresenter

// @formatter:off
interface CurrencyListFragmentContract {

    interface Model {
        interface RestModel {
            fun fetchApiResponse(presenter: CurrencyListFragmentPresenter, date: String)
        }
        interface SharedPreferencesModel {
            fun getRawJson(date: String): String
            fun checkIfSuchDateExists(strDate: String?): Boolean
            fun saveNumberOfMinusDays(howManyDays: Int)
            fun saveRawJson(rawJson: String, date: String)
            fun getNumberOfMinusDays(): Int
        }
    }

    interface View {
        fun setRecyclerViewStateToLoading()
        fun removeRecyclerViewStetOfLoading()
        fun assignResponseToRecyclerview(apiResponse: ApiResponse?)
        fun notifyChangedItemAdapter()
        fun getApiResponseList(): List<ApiResponse>
        fun showLogAboutExistingDateInSp(date: String)
        fun showLogAboutNotExistingDateInSp(date: String)
        fun showErrorResponseToast(errorResponse: String)
    }

    interface Presenter {
        fun convertCurrentDate(): String
        fun passResponseToView(apiResponse: ApiResponse?)
        fun processRawJson(rawJson: String): ApiResponse
        fun makeACall(date: String?)
        fun loadMoreDays()
        fun processRvItemOnScroll(isLoading: Boolean, rvItem: RecyclerView, newState: Int): Boolean
        fun checkIfSetRecyclerViewStateToLoading(apiResponseSize: Int)
        fun checkIfRemoveRecyclerViewStateOfLoading(apiResponseSize: Int)
        fun passErrorResponseMessageToView(errorResponse: String)
    }
}
