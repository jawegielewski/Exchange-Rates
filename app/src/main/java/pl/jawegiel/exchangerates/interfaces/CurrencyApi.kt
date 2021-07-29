package pl.jawegiel.exchangerates.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// @formatter:off
interface CurrencyApi {

    @GET("{date}")
    suspend fun getCurrenciesForDate(@Path("date") date: String, @Query("access_key") apiKey: String): Response<String>
}