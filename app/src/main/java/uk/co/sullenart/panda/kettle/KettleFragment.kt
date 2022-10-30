package uk.co.sullenart.panda.kettle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_kettle.*
import uk.co.sullenart.panda.BaseFragment
import uk.co.sullenart.panda.MainApplication
import uk.co.sullenart.panda.R
import javax.inject.Inject

class KettleFragment : BaseFragment(), KettlePresenter.View {
    @Inject
    lateinit var presenter: KettlePresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as MainApplication).component.inject(this)
        presenter.view = this

        return inflater.inflate(R.layout.fragment_kettle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        on.setOnClickListener { presenter.onClicked() }
        off.setOnClickListener { presenter.offClicked() }
    }

    override fun showStatus(text: String) {
        status.text = text
        status.isVisible = true
    }

    override fun clearStatus() {
        status.isVisible = false
    }
}