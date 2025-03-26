package com.example.harmony;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String accessToken;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTrackRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accessToken = getAccessToken();
        if (accessToken != null) {
            startTrackingCurrentTrack();
        } else {
            Toast.makeText(this, "Spotify token bulunamadı!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAccessToken() {
        SharedPreferences prefs = getSharedPreferences("SpotifyPrefs", MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }

    private void startTrackingCurrentTrack() {
        updateTrackRunnable = new Runnable() {
            @Override
            public void run() {
                getCurrentPlayingTrack(accessToken);
                handler.postDelayed(this, 5000);  // 5 saniyede bir şarkıyı kontrol et
            }
        };
        handler.post(updateTrackRunnable);  // Başlat
    }

    private void stopTrackingCurrentTrack() {
        if (updateTrackRunnable != null) {
            handler.removeCallbacks(updateTrackRunnable);  // Şarkı takibini durdur
        }
    }

    private void getCurrentPlayingTrack(String accessToken) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.spotify.com/v1/me/player/currently-playing");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    if (jsonObject.has("item")) {
                        JSONObject item = jsonObject.getJSONObject("item");
                        String songName = item.getString("name");
                        String artistName = item.getJSONArray("artists").getJSONObject(0).getString("name");

                        // Çalan şarkı bilgisini Toast ile göster
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(MainActivity.this, "Çalan Şarkı: " + songName + " - " + artistName, Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(MainActivity.this, "Şu an müzik çalmıyor!", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Hata Kodu: " + responseCode, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTrackingCurrentTrack();  // Activity durduğunda şarkı takibini durdur
    }
}
