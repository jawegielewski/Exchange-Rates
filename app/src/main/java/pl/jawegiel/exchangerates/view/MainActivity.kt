package pl.jawegiel.exchangerates.view

import android.content.res.Resources
import android.os.Bundle
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item.*
import kotlinx.android.synthetic.main.main_activity.*
import pl.jawegiel.exchangerates.R
import pl.jawegiel.exchangerates.interfaces.OnChangeFragment
import pl.jawegiel.exchangerates.model.ChangeFragmentData

// @formatter:off
class MainActivity : AppCompatActivity(), OnChangeFragment {

    companion object {
        const val SAVED_INSTANCE_STATE_CURRENT_FRAGMENT = "instance_state_fragment"
        const val SAVED_INSTANCE_STATE_CHANGE_FRAGMENT_DATA = "instance_state_change_fragment_data"
        const val DELAY_TO_ENSURE_REFRESHING_VIEW = 100L
    }

    private var motionLayoutProgress: Int = 0
    private var llToolbarY: Int = 0
    private lateinit var currentFragment: String
    private lateinit var changeFragmentDataFromChangeFragmentMethod: ChangeFragmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.main_activity)

        initToolbarTitleListener()
        initMotionLayoutListener()

        changeFragment(CurrencyListFragment(), ChangeFragmentData())

        if(savedInstanceState?.get(SAVED_INSTANCE_STATE_CURRENT_FRAGMENT) != null) {
            when(savedInstanceState.get(SAVED_INSTANCE_STATE_CURRENT_FRAGMENT)) {
                "SpecificCurrencyFragment" -> changeFragment(SpecificCurrencyFragment(), savedInstanceState.getSerializable(SAVED_INSTANCE_STATE_CHANGE_FRAGMENT_DATA) as ChangeFragmentData)
                "AboutFragment" -> changeFragment(AboutFragment(), ChangeFragmentData())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initToolbarListener()
    }

    override fun changeFragment(fragment: Fragment, changeFragmentData: ChangeFragmentData) {
        if (fragment is SpecificCurrencyFragment) {
            this.changeFragmentDataFromChangeFragmentMethod = changeFragmentData
            val args = Bundle()
            args.putSerializable(SpecificCurrencyFragment.MAP_DATA_RECEIVE, changeFragmentData.mapOfCurrency)
            args.putString(SpecificCurrencyFragment.DAY_DATA_RECEIVE, changeFragmentData.day)
            fragment.setArguments(args)
        }
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
            commit()
        }
        when (fragment) {
            is CurrencyListFragment -> {
                changeToolbarSubtitle("CurrencyListFragment.kt")
                currentFragment = "CurrencyListFragment"
            }
            is AboutFragment -> {
                changeToolbarSubtitle("AboutFragment.kt")
                currentFragment = "AboutFragment"
            }
            is SpecificCurrencyFragment -> {
                changeToolbarSubtitle("SpecificCurrencyFragment.kt")
                currentFragment = "SpecificCurrencyFragment"
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_INSTANCE_STATE_CURRENT_FRAGMENT, currentFragment)
        if(currentFragment == "SpecificCurrencyFragment")
            outState.putSerializable(SAVED_INSTANCE_STATE_CHANGE_FRAGMENT_DATA, changeFragmentDataFromChangeFragmentMethod)
}

    private fun initToolbarTitleListener() {
        findViewById<TextView>(R.id.toolbar_title).setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> changeFragment(
                    CurrencyListFragment(),
                    ChangeFragmentData()
                )
            }

            return@setOnTouchListener true
        }
    }

    private fun initMotionLayoutListener() {
        motion_layout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                motionLayoutProgress = p0?.progress?.toInt()!!
                if (motionLayoutProgress == 1) findViewById<TextView>(R.id.about).setOnClickListener {
                    changeFragment(
                        AboutFragment(),
                        ChangeFragmentData()
                    )
                }
                else findViewById<TextView>(R.id.about).setOnClickListener { }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

            override fun onTransitionChange(p0: MotionLayout?, startId: Int, endId: Int, progress: Float) {
            }
        })
    }

    private fun getHeightOfScreenWithoutBars(): Int {
        val  resourceStatusBarId = resources.getIdentifier("status_bar_height", "dimen", "android")
        var statusBarHeight = 0
        if (resourceStatusBarId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceStatusBarId)
        }
        return Resources.getSystem().displayMetrics.heightPixels - statusBarHeight
    }

    private fun initToolbarListener() {
        ll_toolbar.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            llToolbarY = view.y.toInt()

            fragment_container.postDelayed({
                val params = fragment_container.layoutParams as RelativeLayout.LayoutParams
                params.height = getHeightOfScreenWithoutBars() - llToolbarY - ll_toolbar.height
                fragment_container.layoutParams = params
            }, DELAY_TO_ENSURE_REFRESHING_VIEW)
        }
    }

    private fun changeToolbarSubtitle(subtitle: String) {
        toolbar_subtitle.text = subtitle
    }
}