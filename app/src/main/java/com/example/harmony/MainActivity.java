package com.example.harmony;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String accessToken;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTrackRunnable;
    private LatLng userLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean notFirstTime = true;
    String fullSongInfo;
    FirebaseFirestore db;
    String userId;

    List<User> users = new ArrayList<>();

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

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:962196792378:android:e59d0186ffa915111c4103")
                .setApiKey("AIzaSyCv24Y2IGWYqUnbVvKjfAhm4cRr2LLa0CI")
                .setDatabaseUrl("https://firestore.googleapis.com/v1/projects/harmony-a9c98/databases/(default)/documents")
                .setProjectId("harmony-a9c98")
                .build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(this, options);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (firebaseApp != null) {
            Log.d("Firebase", "Firebase is initialized.");
        } else {
            Log.e("Firebase", "Firebase initialization failed.");
        }
        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("EMAIL");
        Log.e("AAAA", userId);

        users.add(new User(userId,0.0, 0.0, ""));
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
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    if (jsonObject.has("item")) {
                        JSONObject item = jsonObject.getJSONObject("item");
                        String songName = item.getString("name");
                        String artistName = item.getJSONArray("artists").getJSONObject(0).getString("name");
                        fullSongInfo = songName + " - " + artistName;
                    } else {
                        showToast("Şu an müzik çalmıyor!");
                    }
                } else {
                    if (responseCode != 204) showToast("Hata Kodu: " + responseCode);
                }

                getCurrentLocation();
                users.get(0).longitude = userLocation.longitude;
                users.get(0).latitude = userLocation.latitude;
                users.get(0).songInfo = fullSongInfo;

                if (userLocation != null && userId != null) {
                    Map<String, Object> save = new HashMap<>();
                    save.put("latitude", userLocation.latitude);
                    Log.e("YAZZZZ", userLocation.latitude + "YAZZZZ");
                    save.put("longitude", userLocation.longitude);
                    save.put("song", fullSongInfo);

                    db.collection("users").document(userId)
                            .set(save)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Veri güncellendi"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Hata oluştu", e));
                }

                db.collection("users").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            if(!doc.getId().equals(userId)) {
                                Map<String, Object> save = (Map<String, Object>)(doc.getData());

                                double lat = (Double)(save.get("latitude"));
                                double lng = (Double)(save.get("longitude"));
                                String song = (String) (save.get("song"));

                                User u = doesUserExist(doc.getId());
                                if(u == null){
                                    users.add(new User(doc.getId(), lat, lng, song));
                                }
                                else{
                                    u.songInfo = song;
                                    u.latitude = lat;
                                    u.longitude = lng;
                                }
                            }
                        }
                    } else {
                        Log.e("Firestore", "Veri çekme hatası", task.getException());
                    }
                });

            } catch (Exception e) {
                Log.e("3131", "Hata:" + e.getMessage());
            }
        }).start();
        updateMapMarker();
//        mMap.clear();
//        for(User u: users){
//            LatLng loc = new LatLng(u.latitude, u.longitude);
//            if (mMap != null && loc != null) {
//                handler.post(() -> {
//                    mMap.addMarker(new MarkerOptions().position(loc).title(u.songInfo));
//                });
//            }
//        }
    }

    public User doesUserExist(String userid) {
        for (User u : users) if (u.userid.equals(userid)) return u;
        return null;
    }

    private void updateMapMarker() {
        mMap.clear();
        for(User u: users){
            LatLng loc = new LatLng(u.latitude, u.longitude);
            if (mMap != null && loc != null) {
                Log.e("SSSS", "SSSSS" + "aaa");
                handler.post(() -> {
                    mMap.addMarker(new MarkerOptions().position(loc).title(u.songInfo));
                });
            }
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
                        if (notFirstTime) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                            notFirstTime = false;
                        }
                    } else showToast("Konum alınamadı!");
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
            } else showToast("Konum izni reddedildi!");
        }
    }

    private void showToast(String message) {
        handler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateTrackRunnable);
        if (userId != null) {
            db.collection("users").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Kullanıcı verisi silindi"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Veri silme hatası", e));
        }
    }
}