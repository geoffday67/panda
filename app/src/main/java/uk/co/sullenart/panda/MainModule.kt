package uk.co.sullenart.panda

import android.content.Context
import com.google.gson.Gson
import com.polidea.rxandroidble2.RxBleClient
import dagger.Module
import dagger.Provides
import uk.co.sullenart.panda.kettle.RemoteApi
import uk.co.sullenart.panda.mqtt.RxMqttClient
import javax.inject.Singleton

@Module
class MainModule(private val context: Context) {
    @Singleton
    @Provides
    fun provideGson() = Gson()

    @Singleton
    @Provides
    fun provideMqttClient(): RxMqttClient = RxMqttClient()

    @Singleton
    @Provides
    fun provideBleClient() = RxBleClient.create(context)

    @Singleton
    @Provides
    fun provideRemoteApi() = RemoteApi()
}
