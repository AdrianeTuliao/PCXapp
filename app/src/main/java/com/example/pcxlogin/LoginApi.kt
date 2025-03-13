import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginApi {
    @FormUrlEncoded
    @POST("login1.php")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user_id: Int? = null,
    val username: String? = null
)
