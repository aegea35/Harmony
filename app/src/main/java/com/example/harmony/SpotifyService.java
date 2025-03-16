package com.example.harmony;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SpotifyService {

    @GET("v1/me/top/artists")
    Call<SpotifyResponse> getTopArtists(@Header("Authorization") String token);

    @GET("v1/me/top/tracks")
    Call<SpotifyResponse> getTopTracks(@Header("Authorization") String token);

}
