package uk.co.sullenart.panda.summoner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_summoner.*
import uk.co.sullenart.panda.BaseFragment
import uk.co.sullenart.panda.MainApplication
import uk.co.sullenart.panda.R
import javax.inject.Inject

class SummonerFragment : BaseFragment(), SummonerPresenter.View {
    @Inject
    lateinit var presenter: SummonerPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as MainApplication).component.inject(this)
        presenter.view = this

        return inflater.inflate(R.layout.fragment_summoner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coffee.setOnClickListener { presenter.coffeeClicked() }
        lunch.setOnClickListener { presenter.lunchClicked() }
        dinner.setOnClickListener { presenter.dinnerClicked() }
        tea.setOnClickListener { presenter.teaClicked() }
    }

    override fun onResume() {
        super.onResume()

        presenter.start()
    }

    override fun onPause() {
        super.onPause()

        presenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.stop()
    }

    override fun showStatus(text: String) {
        status.text = text
        status.isVisible = true
    }

    override fun clearStatus() {
        status.isVisible = false
    }

    override fun isTestMode() =
        test_mode.isChecked
}
