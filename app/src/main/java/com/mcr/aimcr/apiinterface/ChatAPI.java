package com.mcr.aimcr.apiinterface;

import com.mcr.aimcr.apimodels.ChatAPIModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatAPI {
    @POST("chatai")
    Call<ChatAPIModel> getResponse(@Body ChatAPIModel data);
}
