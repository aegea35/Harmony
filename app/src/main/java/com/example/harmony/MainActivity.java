package com.example.harmony;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String accessToken;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTrackRunnable;
    private LatLng userLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean notfirsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        accessToken = getAccessToken();
        if (accessToken != null) startTrackingCurrentTrack();
        else showToast("Spotify token bulunamadı!");
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
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(updateTrackRunnable);
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
                        String fullSongInfo = songName + " - " + artistName;

                        //showToast("Çalan Şarkı: " + fullSongInfo);
                        updateMapMarker(fullSongInfo);
                    } else {
                        showToast("Şu an müzik çalmıyor!");
                    }
                } else {
                    if ( responseCode != 204) showToast("Hata Kodu: " + responseCode);
                }
            } catch (Exception e) {
                showToast("Hata: " + e.getMessage());
            }
        }).start();
    }

    private void updateMapMarker(String songInfo) {
        if (mMap != null && userLocation != null) {
            handler.post(() -> {
                getCurrentLocation();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title(songInfo));
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        //mMap.addMarker(new MarkerOptions().position(userLocation).title("Konum Bulundu"));
                        if (notfirsttime) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            notfirsttime = false;
                        }
                    } else {
                        showToast("Konum alınamadı!");
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                showToast("Konum izni reddedildi!");
            }
        }
    }

    private void showToast(String message) {
        handler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateTrackRunnable);
    }
}
