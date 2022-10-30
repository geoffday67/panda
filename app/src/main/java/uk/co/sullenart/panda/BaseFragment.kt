package uk.co.sullenart.panda

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    protected lateinit var safeContext: Context

    override fun onAttach(context: Context) {
        safeContext = context

        super.onAttach(context)
    }
}
