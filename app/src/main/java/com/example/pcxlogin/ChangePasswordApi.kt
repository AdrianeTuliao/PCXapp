package com.example.pcxlogin

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ChangePasswordApi {
    @FormUrlEncoded
    @POST("change_passwordApp.php")
    fun changePassword(
        @Field("user_id") userId: Int,
        @Field("new_password") newPassword: String
    ): Call<ResponseBody>
}
