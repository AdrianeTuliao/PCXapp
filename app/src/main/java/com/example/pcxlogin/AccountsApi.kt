import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AccountsApi {
    @FormUrlEncoded

    @POST("create_account.php")
    fun createAccount(
        @Field("username") username: String,
        @Field("contact_number") contactNum: String,
        @Field("city") city: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<CreateAccountResponse>
}

data class CreateAccountResponse(
    val success: Boolean,
    val message: String,
    val userId: Int? = null
)