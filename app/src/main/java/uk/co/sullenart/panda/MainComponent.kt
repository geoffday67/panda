package uk.co.sullenart.panda

import dagger.Component
import uk.co.sullenart.panda.hobby.HobbyFragment
import uk.co.sullenart.panda.kettle.KettleFragment
import uk.co.sullenart.panda.kitchen_lights.KitchenLightsFragment
import uk.co.sullenart.panda.summoner.SummonerFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [MainModule::class])
interface MainComponent {
    fun inject(fragment: SummonerFragment)
    fun inject(fragment: KitchenLightsFragment)
    fun inject(fragment: KettleFragment)
    fun inject(fragment: HobbyFragment)
    fun inject(activity: MainActivity)
}
