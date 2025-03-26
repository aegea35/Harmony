package com.example.harmony;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SpotifyLoginActivityTest {

    @Rule
    public ActivityTestRule<SpotifyLoginActivity> activityRule =
            new ActivityTestRule<>(SpotifyLoginActivity.class, false, false);

    private static final String MOCK_REDIRECT_URI = "harmony://callback";
    private static final String MOCK_AUTH_URL = "https://accounts.spotify.com/authorize?client_id=dc39aff95e79486db2b844b849371443"
            + "&response_type=token"
            + "&redirect_uri=" + MOCK_REDIRECT_URI
            + "&scope=user-read-email,user-read-private,user-read-playback-state"
            + "&show_dialog=false";

    @Before
    public void setUp() {
        Intents.init();
        ApplicationProvider.getApplicationContext()
                .getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testOpenSpotifyLogin() {

        activityRule.launchActivity(new Intent());
        onView(withId(R.id.onSpotifyLoginClick)).perform(click());

        intended(hasAction(Intent.ACTION_VIEW));
        intended(hasData(Uri.parse(MOCK_AUTH_URL)));
    }

    @Test
    public void testHandleAuthResponse_Success() throws InterruptedException {
        // Prepare test data
        Intent intent = new Intent();
        intent.setData(Uri.parse(MOCK_REDIRECT_URI + "#access_token=mockAccessToken"));

        // Clear SharedPreferences
        SharedPreferences prefs = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

        // Launch activity
        activityRule.launchActivity(new Intent());

        // Run on UI thread and wait
        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().handleAuthResponse(intent);
        });

        // Add small delay to allow processing
        Thread.sleep(500);

        // Verify
        assertEquals("mockAccessToken", prefs.getString("access_token", null));
    }

    @Test
    public void testHandleAuthResponse_Failure() {
        // 1. Hatalı intent oluştur
        Intent intent = new Intent();
        intent.setData(Uri.parse(MOCK_REDIRECT_URI + "#error=access_denied"));

        // 2. SharedPreferences'i temizle
        ApplicationProvider.getApplicationContext()
                .getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // 3. Activity'i başlat ve işlemi tetikle
        activityRule.launchActivity(new Intent());
        activityRule.getActivity().handleAuthResponse(intent);

        // 4. Token'ın kaydedilmediğini kontrol et
        SharedPreferences prefs = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("SpotifyPrefs", Context.MODE_PRIVATE);

        assertNull(prefs.getString("access_token", null));
    }
}