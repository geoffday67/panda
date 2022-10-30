package uk.co.sullenart.panda.hobby

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_hobby.*
import uk.co.sullenart.panda.BaseFragment
import uk.co.sullenart.panda.MainApplication
import uk.co.sullenart.panda.R
import javax.inject.Inject

class HobbyFragment : BaseFragment(), HobbyPresenter.View {
    @Inject
    lateinit var presenter: HobbyPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as MainApplication).component.inject(this)
        presenter.view = this

        return inflater.inflate(R.layout.fragment_hobby, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lights_on.setOnClickListener { presenter.lightsOnClicked() }
        lights_off.setOnClickListener { presenter.lightsOffClicked() }
        colour.addOnColorSelectedListener { presenter.colourClicked(it) }
        brightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                presenter.brightnessClicked(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun showStatus(text: String) {
        status.text = text
        status.isVisible = true
    }

    override fun clearStatus() {
        status.isVisible = false
    }
}
