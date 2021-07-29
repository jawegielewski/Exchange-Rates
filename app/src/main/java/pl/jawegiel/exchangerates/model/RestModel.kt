package pl.jawegiel.exchangerates.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.*
import pl.jawegiel.exchangerates.CurrencyService
import pl.jawegiel.exchangerates.interfaces.CurrencyListFragmentContract
import pl.jawegiel.exchangerates.presenter.CurrencyListFragmentPresenter
import java.lang.ref.WeakReference

// @formatter:off
class RestModel(private val contextWeakReference: WeakReference<Context>) : CurrencyListFragmentContract.Model.RestModel {

    private val userService = CurrencyService().getCurrencyService()
    private var job: Job? = null

    fun getApiKey(): String {
        val ai: ApplicationInfo = contextWeakReference.get()!!.packageManager.getApplicationInfo(
            contextWeakReference.get()!!.packageName,
            PackageManager.GET_META_DATA
        )
        return ai.metaData["API_KEY"].toString()
    }

    override fun fetchApiResponse(presenter: CurrencyListFragmentPresenter, date: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = userService.getCurrenciesForDate(date, getApiKey())
            withContext(Dispatchers.Main) {
                try {
                    val apiResponse = presenter.processRawJson(response.body()!!)
                    presenter.passResponseToView(apiResponse)
                }
                catch (ex: Exception) {
                    presenter.passErrorResponseMessageToView(ex.message.toString())
                }
            }
        }
    }

    fun cancelJob() {
        job?.cancel()
    }
}