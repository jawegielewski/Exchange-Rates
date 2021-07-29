package pl.jawegiel.exchangerates.interfaces

import androidx.fragment.app.Fragment
import pl.jawegiel.exchangerates.model.ChangeFragmentData

// @formatter:off
interface OnChangeFragment {

    fun changeFragment(fragment: Fragment, changeFragmentData: ChangeFragmentData)
}