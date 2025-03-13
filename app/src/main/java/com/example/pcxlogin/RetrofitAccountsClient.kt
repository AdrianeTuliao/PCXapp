import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAccountsClient {
    private const val BASE_URL = "http://192.168.18.127/PCXadmin/"

    val instance: AccountsApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(AccountsApi::class.java)
    }
}
