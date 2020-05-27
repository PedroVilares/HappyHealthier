package com.example.happyhealthier.initial_pages;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.happyhealthier.R;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>{

    private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_onboarding,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position));

    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {

        private TextView textHeader;
        private TextView textDescription;
        private ImageView imageIcon;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);

            textHeader = itemView.findViewById(R.id.slideHeading);
            textDescription = itemView.findViewById(R.id.slideDescription);
            imageIcon = itemView.findViewById(R.id.slideImage);
        }

        void setOnboardingData (OnboardingItem onboardingItem) {
            textHeader.setText(onboardingItem.getTitle());
            textDescription.setText(onboardingItem.getDescription());
            imageIcon.setImageResource(onboardingItem.getImage());
        }
    }}
