package com.mcr.aimcr.apiinterface;
import com.mcr.aimcr.apimodels.VoiceAPIModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VoiceAPI {
    @POST("voiceai")
    Call<VoiceAPIModel> getVoiceData(@Body VoiceAPIModel data);
}
