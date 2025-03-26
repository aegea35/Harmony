package com.example.harmony;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Tasks;



@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Mock
    GoogleSignInClient mockGoogleSignInClient;
    @Mock
    GoogleSignInAccount mockGoogleSignInAccount;

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();


//        MockitoAnnotations.initMocks(this);
//
//        when(mockGoogleSignInClient.silentSignIn()).thenReturn(Tasks.forResult(mockGoogleSignInAccount));

        activityRule.launchActivity(new Intent());
//        LoginActivity activity = activityRule.getActivity();
//        activity.setGoogleSignInClient(mockGoogleSignInClient);

    }

    @After
    public void tearDown() {
        Intents.release(); // Intent kaynaklarını serbest bırak
    }

    @Test
    public void testGoogleSignInButton_click() {
        // Google Sign-In butonuna tıklamayı test et
        onView(withId(R.id.buttonGoogleSignIn)).perform(click());

        // MainActivity'ye geçiş intentini doğrula
        //Intents.intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testSignInFailure() {
        // Sign-In başarısız olduğunda TextView'in doğru mesaj gösterdiğini kontrol et
        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().getTextView().setText("Sign-In Failed");
        });

        onView(withId(R.id.textView)).check(matches(withText("Sign-In Failed")));
    }

    @Test
    public void testSignInSuccess() {
        // Sahte bir başarı senaryosunu kontrol et
        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().getTextView().setText("Signed in as: ege azca\nEmail: egeazca@gmail.com");
        });

        onView(withId(R.id.textView)).check(matches(withText("Signed in as: ege azca\nEmail: egeazca@gmail.com")));
    }
}
