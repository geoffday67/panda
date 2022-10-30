package uk.co.sullenart.panda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import uk.co.sullenart.panda.mqtt.RxMqttClient
import uk.co.sullenart.panda.hobby.HobbyFragment
import uk.co.sullenart.panda.kettle.KettleFragment
import uk.co.sullenart.panda.kitchen_lights.KitchenLightsFragment
import uk.co.sullenart.panda.summoner.SummonerFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var rxMqttClient: RxMqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MainApplication).component.inject(this)

        fragment_pager.adapter = FragmentAdapter(this)

        TabLayoutMediator(tab_layout, fragment_pager) { tab, position ->
            tab.text = when (position) {
                0 -> "Summoner"
                1 -> "Kitchen lights"
                2 -> "Kettle"
                3 -> "Hobby house"
                else -> "Unknown"
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        // Connect to MQTT broker
        // Keep status in sync
        rxMqttClient.connect(BuildConfig.MQTT_BROKER, BuildConfig.MQTT_ID, BuildConfig.MQTT_PORT)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Timber.d("MQTT connected") },
                onError = { Timber.e(it) }
            )
    }

    override fun onPause() {
        super.onPause()

        // Disconnect from MQTT
        rxMqttClient.disconnect()
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onComplete = { Timber.d("MQTT disconnected") },
                onError = { Timber.e(it) }
            )
    }
}

class FragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> SummonerFragment()
            1 -> KitchenLightsFragment()
            2 -> KettleFragment()
            3 -> HobbyFragment()
            else -> throw Exception("Invalid adapter position")
        }
}
