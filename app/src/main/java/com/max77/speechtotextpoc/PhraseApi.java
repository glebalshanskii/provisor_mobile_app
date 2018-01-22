package com.max77.speechtotextpoc;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * SpeechToTextPOC project
 * Created by max77 on 20180117.
 */

public interface PhraseApi {
    @GET("/api/lorem/p-1/20-35")
    Call<PhraseModel> getPhrase();
}
