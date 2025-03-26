package com.example.harmony;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service {
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, getNotification());

        runnable = new Runnable() {
            @Override
            public void run() {
                //Log.d("BackgroundService", "Arka planda çalışıyor...");
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);
    }

    private Notification getNotification() {
        String channelId = "BackgroundServiceChannel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Background Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Harmony Çalışıyor")
                .setContentText("Arka planda veri güncelleniyor...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
