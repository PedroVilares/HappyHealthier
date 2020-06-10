package com.example.happyhealthier;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.happyhealthier.MainActivity;
import com.example.happyhealthier.main_fragments.ExerciseFragment;

public class ExerciseService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String exerciseDistance = intent.getStringExtra("distance");

        RemoteViews collapsedView = new RemoteViews(getPackageName(),R.layout.exercise_service_collpased);
        collapsedView.setTextViewText(R.id.serviceDistanceText,String.format("%.2f km",Double.parseDouble(exerciseDistance)));
        collapsedView.setChronometer(R.id.serviceChronometer, SystemClock.elapsedRealtime(),"%s",true);


        Notification notification = new NotificationCompat.Builder(this,"servico_exercicio")
                .setCustomContentView(collapsedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setSmallIcon(R.drawable.ic_exercise)
                .build();

        startForeground(1,notification);

        return START_REDELIVER_INTENT;
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
