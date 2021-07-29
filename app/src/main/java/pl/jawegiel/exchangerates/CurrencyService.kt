package pl.jawegiel.exchangerates

import com.google.gson.GsonBuilder
import pl.jawegiel.exchangerates.interfaces.CurrencyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

// @formatter:off
class CurrencyService {

    private val BASE_URL = "http://data.fixer.io/api/";
    fun getCurrencyService(): CurrencyApi {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}