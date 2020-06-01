package com.example.happyhealthier;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.happyhealthier.main_fragments.ExerciseFragment;

public class ExerciseService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String exerciseDistance = intent.getStringExtra("distance");
        Log.e("service",exerciseDistance);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification = new NotificationCompat.Builder(this,"notificacao_exercicio")
                .setContentTitle("Exercício")
                .setContentText(exerciseDistance)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
