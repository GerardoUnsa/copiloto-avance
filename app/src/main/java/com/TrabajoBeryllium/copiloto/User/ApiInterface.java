package com.TrabajoBeryllium.copiloto.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/authentication/login/")
    Call<User> getUserInformation(@Field("username") String name, @Field("password") String job);
}
