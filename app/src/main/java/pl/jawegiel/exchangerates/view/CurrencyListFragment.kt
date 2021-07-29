package pl.jawegiel.exchangerates.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.currency_list_fragment.*
import pl.jawegiel.exchangerates.R
import pl.jawegiel.exchangerates.adapter.ItemAdapter
import pl.jawegiel.exchangerates.interfaces.CurrencyListFragmentContract
import pl.jawegiel.exchangerates.model.ApiResponse
import pl.jawegiel.exchangerates.model.Currency
import pl.jawegiel.exchangerates.model.RestModel
import pl.jawegiel.exchangerates.model.SharedPreferencesModel
import pl.jawegiel.exchangerates.presenter.CurrencyListFragmentPresenter
import java.lang.ref.WeakReference

// @formatter:off
class CurrencyListFragment : Fragment(), CurrencyListFragmentContract.View {

    companion object {
        private val TAG = CurrencyListFragment::class.qualifiedName
        const val START_VALUE = 0
        const val LOADING_BASE = ""
        const val LOADING_DATE = ""
        const val LOADING_CURRENCY_NAME = ""
        const val LOADING_CURRENCY_VALUE = 0f
        const val IT_IS_LOADING = true
        const val NUMBER_OF_INSERTED_ELEMENTS = 1
        const val NUMBER_OF_REMOVED_ELEMENTS = 1
    }

    private lateinit var restModel: RestModel
    private lateinit var clfPresenter: CurrencyListFragmentPresenter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var _layoutManager: LinearLayoutManager
    private lateinit var currentDate: String
    private var isLoading: Boolean = false
    private var apiResponseList: MutableList<ApiResponse> = arrayListOf()
    private var listSize: Int = 0

    override fun onAttach(context: Context){
        super.onAttach(context)
        restModel = RestModel(WeakReference(activity))
}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.currency_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _layoutManager = LinearLayoutManager(activity)
        clfPresenter = CurrencyListFragmentPresenter(this, restModel, SharedPreferencesModel(activity as Activity))
        currentDate = clfPresenter.convertCurrentDate()
        if (clfPresenter.checkIfSuchDateExistsInSp(currentDate)) {
            Log.i(TAG, "Date $currentDate already exists in SharedPreferences (first element)")
            clfPresenter.processDateWithoutMakingACall(currentDate)
        } else {
            Log.i(TAG, "Date $currentDate does not exist in SharedPreferences. Retrofit call will be made (first element)")
            clfPresenter.makeACall(currentDate)
        }
        clfPresenter.saveNumberOfMinusDaysIntoSp(START_VALUE)
        addScrollerListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        restModel.cancelJob()
    }

    override fun setRecyclerViewStateToLoading() {
        apiResponseList.add(ApiResponse(LOADING_BASE, LOADING_DATE, listOf(Currency(LOADING_CURRENCY_NAME, LOADING_CURRENCY_VALUE)), IT_IS_LOADING))
        itemAdapter.notifyItemInserted(apiResponseList.size - NUMBER_OF_INSERTED_ELEMENTS)
    }

    override fun removeRecyclerViewStetOfLoading() {
        apiResponseList.removeAt(apiResponseList.size - NUMBER_OF_REMOVED_ELEMENTS)
        listSize = apiResponseList.size
        itemAdapter.notifyItemRemoved(listSize)
    }

    override fun getApiResponseList(): List<ApiResponse> {
        return apiResponseList
    }

    override fun showLogAboutExistingDateInSp(date: String) {
        Log.i(TAG, "Date $date already exists in SharedPreferences (new element)")
    }

    override fun showLogAboutNotExistingDateInSp(date: String) {
        Log.i(TAG, "Date $date does not exist in SharedPreferences. Retrofit call made (new element)")
    }

    override fun showErrorResponseToast(errorResponse: String) {
        Toast.makeText(activity, getString(R.string.error_during_api_call, errorResponse), Toast.LENGTH_SHORT).show()
    }

    override fun assignResponseToRecyclerview(apiResponse: ApiResponse?) {
        rv_item?.apply {
            layoutManager = _layoutManager
            apiResponseList.add(apiResponse!!)
            itemAdapter = activity?.let { ItemAdapter(apiResponseList, it) }!!
            adapter = itemAdapter
        }
    }

    private fun addScrollerListener() {
        rv_item.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(rvItem: RecyclerView, newState: Int) {
                super.onScrollStateChanged(rvItem, newState)
                clfPresenter.processRvItemOnScroll(isLoading, rvItem, newState)
            }
        })
    }

    override fun notifyChangedItemAdapter() {
        itemAdapter.notifyDataSetChanged()
    }
}
