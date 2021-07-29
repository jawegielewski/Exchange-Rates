package pl.jawegiel.exchangerates.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import pl.jawegiel.exchangerates.R


// @formatter:off
class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_fragment,container,false)
    }

    override  fun onSaveInstanceState(  outState:Bundle){
        super.onSaveInstanceState(outState)
        outState.putCharSequence("a","bc")
    }

    override  fun onViewStateRestored(@Nullable savedInstanceState:Bundle?){
        super.onViewStateRestored(savedInstanceState)
        var  savedTitle: String? = null
        if (savedInstanceState != null){
            savedTitle = savedInstanceState.getString("a")
            Log.e("xxx", savedTitle.toString())
        }
    }
}