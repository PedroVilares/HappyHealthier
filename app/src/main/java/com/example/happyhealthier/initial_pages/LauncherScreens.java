package com.example.happyhealthier.initial_pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.happyhealthier.MainActivity;
import com.example.happyhealthier.R;

import java.util.ArrayList;
import java.util.List;

public class LauncherScreens extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout linearLayoutOnboardingIndicators;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_screens);

        setupOnboardingItems();

        final ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        linearLayoutOnboardingIndicators = findViewById(R.id.onboardingIndicators);

        setupIndicators();

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        nextButton = findViewById(R.id.buttonOnboardingAction);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onboardingViewPager.getCurrentItem()+1<onboardingAdapter.getItemCount()){
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem()+1);

                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }


            }
        });

    }

    public void setupOnboardingItems(){
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        OnboardingItem itemExercicio = new OnboardingItem();
        itemExercicio.setTitle("Faça Exercício!");
        itemExercicio.setDescription("A HappyHealthier permite-lhe registar as suas caminhadas, corridas e voltas de bicicleta!");
        itemExercicio.setImage(R.drawable.exercise_logo);

        OnboardingItem itemMedidas = new OnboardingItem();
        itemMedidas.setTitle("Medidas");
        itemMedidas.setDescription("Mantenha a sua saúde em dia com a HappyHealthier!");
        itemMedidas.setImage(R.drawable.measurements_logo);

        onboardingItems.add(itemExercicio);
        onboardingItems.add(itemMedidas);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    public void setupIndicators () {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i<indicators.length;i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.onboard_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            linearLayoutOnboardingIndicators.addView(indicators[i]);
        };
    }

    public void setCurrentIndicator(int position) {
        int childCount = linearLayoutOnboardingIndicators.getChildCount();
        for (int i = 0; i<childCount;i++) {
            ImageView imageView = (ImageView) linearLayoutOnboardingIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboard_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.onboard_indicator_inactive));
            }
        }
        if (position == onboardingAdapter.getItemCount()-1){
            nextButton.setText("Começar");
            nextButton.setTextColor(Color.WHITE);
        } else {
            nextButton.setText("Próximo");
            nextButton.setTextColor(Color.WHITE);
        }
    }
}
