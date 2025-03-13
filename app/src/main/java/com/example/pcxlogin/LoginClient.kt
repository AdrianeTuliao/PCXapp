import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginClient {
    private const val BASE_URL = "http://192.168.18.127/PCXadmin/"

    val instance: LoginApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(LoginApi::class.java)
    }
}
