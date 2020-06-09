package com.example.happyhealthier;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name1 = "SonoChannel";
            String description1 = "Canal do Sono";
            int importance1 = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel1 = new NotificationChannel("notificacao_sono",name1,importance1);
            channel1.setDescription(description1);
            channel1.setLightColor(Color.BLUE);
            channel1.enableLights(true);

            NotificationChannel channel2 = new NotificationChannel("notificacao_exercicio","ExercicioChannel",NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("Canal do Exercício");
            channel2.enableVibration(true);
            channel2.enableLights(true);
            channel2.setLightColor(Color.RED);

            NotificationChannel channel3 = new NotificationChannel("servico_exercicio","ExercicioService",NotificationManager.IMPORTANCE_DEFAULT);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}
