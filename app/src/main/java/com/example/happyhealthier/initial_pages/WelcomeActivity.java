package com.example.happyhealthier.initial_pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.happyhealthier.MainActivity;
import com.example.happyhealthier.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        startActivity(new Intent(this, MainActivity.class));

    }
}