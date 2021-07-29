package pl.jawegiel.exchangerates.model

import java.io.Serializable

// @formatter:off
data class ChangeFragmentData(
    val mapOfCurrency: HashMap<String, String> = hashMapOf(),
    val day: String = ""
): Serializable
