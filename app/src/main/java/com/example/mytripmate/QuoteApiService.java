package com.example.mytripmate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("api/random")
    Call<List<QuoteResponse>> getRandomQuote();
}
