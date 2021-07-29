package pl.jawegiel.exchangerates.model

// @formatter:off
data class ApiResponse(
    val base: String,
    val date: String,
    val rates: List<Currency>,
    val isLoading: Boolean
    )