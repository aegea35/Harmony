package com.example.harmony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "bd0de2d1fa3d49c6a36ecb0c90b0dafc";
    private static final String REDIRECT_URI = "harmony://callback";
    private static final String AUTH_URL = "https://accounts.spotify.com/authorize?client_id=" + CLIENT_ID +
            "&response_type=token" +
            "&redirect_uri=" + REDIRECT_URI +
            "&scope=user-read-email,user-read-private,user-read-playback-state" +
            "&show_dialog=false";

    private String accessToken;
    public String email;

    private final ActivityResultLauncher<Intent> authLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri data = result.getData().getData();
                    if (data != null) {
                        accessToken = extractAccessToken(data);
                        if (accessToken != null) {
                            Log.d("SpotifyLogin", "Access Token: " + accessToken);
                            getCurrentPlayingTrack(accessToken);
                        } else {
                            Log.e("SpotifyLogin", "Failed to extract access token");
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        findViewById(R.id.onSpotifyLoginClick).setOnClickListener(v -> openSpotifyLogin());
        email = getIntent().getStringExtra("EMAIL");
    }

    private void openSpotifyLogin() {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_URL));
        customTabsIntent.intent.putExtra(CustomTabsIntent.EXTRA_ENABLE_URLBAR_HIDING, true);
        customTabsIntent.intent.setData(Uri.parse(AUTH_URL));
        authLauncher.launch(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("SpotifyLogin", "onNewIntent çağrıldı!");
        setIntent(intent);
        handleAuthResponse(intent);
    }

    private void handleAuthResponse(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            accessToken = extractAccessToken(uri);
            if (accessToken != null) {
                Log.e("SpotifyLogin", "Access Token: " + accessToken);
                Toast.makeText(this, "Spotify oturumu başarıyla açıldı!", Toast.LENGTH_LONG).show();
                saveAccessToken(accessToken);

                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.putExtra("EMAIL", email);
                startActivity(mainIntent);
                finish();
            } else {
                Log.e("SpotifyLogin", "Failed to extract access token");
                Toast.makeText(this, "Spotify oturum açma başarısız!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String extractAccessToken(Uri uri) {
        String fragment = uri.getFragment();
        Log.e("SpotifyLogin", "Extract Access Token URI: " + uri.toString());
        if (fragment != null && fragment.contains("access_token")) {
            String[] params = fragment.split("&");
            for (String param : params) {
                if (param.startsWith("access_token")) {
                    return param.split("=")[1];
                }
            }
        }
        return null;
    }

    private void saveAccessToken(String token) {
        getSharedPreferences("SpotifyPrefs", MODE_PRIVATE)
                .edit()
                .putString("access_token", token)
                .apply();
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
                    JSONObject item = jsonObject.getJSONObject("item");
                    String songName = item.getString("name");
                    String artistName = item.getJSONArray("artists").getJSONObject(0).getString("name");

                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(SpotifyLoginActivity.this, "Çalan Şarkı: " + songName + " - " + artistName, Toast.LENGTH_LONG).show()
                    );

                } else if (responseCode == 204) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(SpotifyLoginActivity.this, "Şu an müzik çalmıyor!", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    Log.e("SpotifyLogin", "Hata Kodu: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("SpotifyLogin", "Hata: " + e.getMessage(), e);
            }
        }).start();
    }
}
