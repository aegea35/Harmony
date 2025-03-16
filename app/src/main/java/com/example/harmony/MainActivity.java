package com.example.harmony;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harmony.RetrofitClient;
import com.example.harmony.SpotifyService;
import com.example.harmony.SpotifyResponse;

import com.example.harmony.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SPOTIFY_CLIENT_ID = "YOUR_CLIENT_ID";
    private static final String SPOTIFY_REDIRECT_URI = "YOUR_REDIRECT_URI";
    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com/authorize?client_id=" + SPOTIFY_CLIENT_ID +
            "&response_type=code&redirect_uri=" + SPOTIFY_REDIRECT_URI + "&scope=user-library-read";


    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView textView;

    private Button spotifyLinkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        spotifyLinkButton = findViewById(R.id.spotifyLinkButton);
        spotifyLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkSpotifyAccount();
            }
        });

        // Google Sign-In yapılandırması
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("93499832362-4s4rss14omfg5todeok8amlm4rpsear4.apps.googleusercontent.com") // google-services.json içindeki client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.buttonGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String displayName = account.getDisplayName();
                String email = account.getEmail();
                textView.setText("Signed in as: " + displayName + "\nEmail: " + email);
            }
        } catch (ApiException e) {
            Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
            textView.setText("Sign-In Failed");
        }
    }

    private void linkSpotifyAccount() {
        // Start the Spotify authorization process
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPOTIFY_AUTH_URL));
        startActivity(browserIntent);
    }

    private void fetchSpotifyData() {
        // Simulate getting an access token for Spotify (this is just an example, you need to implement the actual token request)
        String token = "Bearer " + getSpotifyAccessToken();  // Replace with actual token

        SpotifyService spotifyService = RetrofitClient.getClient().create(SpotifyService.class);

        // Example of fetching top tracks from Spotify
        spotifyService.getTopTracks(token).enqueue(new Callback<SpotifyResponse>() {
            @Override
            public void onResponse(Call<SpotifyResponse> call, Response<SpotifyResponse> response) {
                if (response.isSuccessful()) {
                    SpotifyResponse spotifyResponse = response.body();
                    // Handle the Spotify data (e.g., display top tracks)
                    StringBuilder trackNames = new StringBuilder("Top Tracks:\n");
                    for (SpotifyResponse.Track track : spotifyResponse.getItems()) {
                        trackNames.append(track.getName()).append("\n");
                    }
                    textView.setText(trackNames.toString());
                } else {
                    textView.setText("Failed to fetch Spotify data.");
                }
            }

            @Override
            public void onFailure(Call<SpotifyResponse> call, Throwable t) {
                textView.setText("Error fetching data: " + t.getMessage());
            }
        });
    }

    // Simulate getting the Spotify Access Token (you need to implement real OAuth logic here)
    private String getSpotifyAccessToken() {
        // This should be replaced with actual code to get an access token from Spotify
        return "YOUR_ACCESS_TOKEN";
    }
}