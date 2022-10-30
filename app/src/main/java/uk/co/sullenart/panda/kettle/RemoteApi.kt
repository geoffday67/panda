package uk.co.sullenart.panda.kettle

import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import uk.co.sullenart.panda.BuildConfig

class RemoteApi {
    companion object {
        const val USERNAME = "wario"
        const val PASSWORD = "mansion1"
    }

    interface RemoteService {
        @FormUrlEncoded
        @POST("interface")
        fun getDeviceList(@Field("operation") operation: String = "get_devices"): Single<DeviceList>

        @FormUrlEncoded
        @POST("interface")
        fun sendCommand(
            @Field("channel") channel: Int,
            @Field("command") command: String,
            @Field("operation") operation: String = "433"
        ): Single<CommandResult>
    }

    abstract class ApiResult {
        val result: String = ""
    }

    data class DeviceList(
        val devices: List<Device>
    ) : ApiResult()

    data class Device(
        val id: Int = 0,
        val name: String = ""
    )

    class CommandResult : ApiResult()

    class AuthInterceptor(username: String, password: String) : Interceptor {
        private val authString = Credentials.basic(username, password)

        override fun intercept(chain: Interceptor.Chain): Response {
            val authRequest = chain.request().newBuilder()
                .addHeader("Authorization", authString)
                .build()
            return chain.proceed(authRequest)
        }
    }

    private val httpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .addInterceptor(AuthInterceptor(USERNAME, PASSWORD))
            .build()

    private val remoteService = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(BuildConfig.AUTOMATION_SERVER)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RemoteService::class.java)

    fun getDevices(): Single<List<Device>> =
        remoteService.getDeviceList()
            .flatMap { if (it.result != "ok") Single.error(Exception("Error from server")) else Single.just(it.devices) }

    fun turnOn3(): Completable =
        remoteService.sendCommand(3, "on")
            .flatMapCompletable { if (it.result != "ok") Completable.error(Exception("Error from server")) else Completable.complete() }

    fun turnOff3(): Completable =
        remoteService.sendCommand(3, "off")
            .flatMapCompletable { if (it.result != "ok") Completable.error(Exception("Error from server")) else Completable.complete() }
}