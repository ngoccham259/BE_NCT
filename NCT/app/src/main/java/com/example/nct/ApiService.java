package com.example.nct;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/songs")
    Call<List<MusicFiles>> getOnlineSongs();

    @POST("api/songs")
    Call<MusicFiles> addSong(@Body MusicFiles song);

    @PUT("api/songs/{id}")
    Call<MusicFiles> updateSong(@Path("id") Long id, @Body MusicFiles song);

    @DELETE("api/songs/{id}")
    Call<Void> deleteSong(@Path("id") Long id);
}