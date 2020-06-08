package com.example.happyhealthier.main_fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.happyhealthier.PointValue;
import com.example.happyhealthier.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HealthFragment extends Fragment {

    ImageButton shareButton;
    FirebaseUser user;
    final ArrayList<String> calorias = new ArrayList<>();
    final ArrayList<String> sono = new ArrayList<>();
    final ArrayList<String> pressaoMax = new ArrayList<>();
    final ArrayList<String> pressaoMin = new ArrayList<>();
    final ArrayList<String> imc = new ArrayList<>();
    final ArrayList<String> glicemia = new ArrayList<>();
    final ArrayList<String> batimento = new ArrayList<>();
    String finalText = "";

    public HealthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_health, container, false);

        shareButton = v.findViewById(R.id.shareButton);
        user = FirebaseAuth.getInstance().getCurrentUser();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareData();
            }
        });

        return v;
    }

    private String buildText(ArrayList<String> arrayList,String text){
        text = text.concat("\n");
        for (int position = 0; position < arrayList.size();position++){
            text = text.concat(arrayList.get(position)).concat("\n");
        }
        text = text.concat("\n");

        return text;
    }

    private void shareData() {

        //get Firebase data//
        final FirebaseDatabase[] database = {FirebaseDatabase.getInstance()};
        DatabaseReference reference = database[0].getReference(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot mDataSnapshot : dataSnapshot.getChildren()){
                    String key = mDataSnapshot.getKey();

                    if (key.equals("Calorias")){
                        for (DataSnapshot calsValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = calsValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String caloriasString = (String.format("Data: %s",date) + "\n" + String.format("%skcals", pointValues.getyValue()));
                            calorias.add(caloriasString);
                            //Log.e("cals",calorias.toString());
                        }
                    }
                    if (key.equals("Sono")){
                        for (DataSnapshot sleepValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = sleepValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String sonoString = (String.format("Data: %s",date) + "\n" + String.format("%s h.min", pointValues.getyValue()));
                            sono.add(sonoString);
                            //Log.e("sono",sono.toString());
                        }
                    }
                    if (key.equals("Pressao")){
                        for (DataSnapshot pressaoValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = pressaoValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String pressaoString = (String.format("Data: %s",date) + "\n" + String.format("%smmHg", pointValues.getyValue()));
                            pressaoMax.add(pressaoString);
                            //Log.e("pressaoMax",pressaoMax.toString());
                        }
                    }
                    if (key.equals("PressaoMin")){
                        for (DataSnapshot pressaoMinValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = pressaoMinValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String pressaoMinString = (String.format("Data: %s",date) + "\n" + String.format("%smmHg", pointValues.getyValue()));
                            pressaoMin.add(pressaoMinString);
                            //Log.e("pressaoMin",pressaoMin.toString());
                        }
                    }
                    if (key.equals("IMC")){
                        for (DataSnapshot imcValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = imcValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String imcString = (String.format("Data: %s",date) + "\n" + String.format("%.2f kg/m^2", pointValues.getyValue()));
                            imc.add(imcString);
                            //Log.e("imc",imc.toString());
                        }
                    }
                    if (key.equals("Glicémia")){
                        for (DataSnapshot glicemiaValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = glicemiaValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String glicemiaString = (String.format("Data: %s",date) + "\n" + String.format("%smg/dL", pointValues.getyValue()));
                            glicemia.add(glicemiaString);
                            //Log.e("glicemia",glicemia.toString());
                        }
                    }
                    if (key.equals("Batimento")){
                        for (DataSnapshot bpmValue : mDataSnapshot.getChildren()){
                            PointValue pointValues = bpmValue.getValue(PointValue.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy");
                            String date = formatter.format(new Date(pointValues.getxValue()));
                            String bpmString = (String.format("Data: %s",date) + "\n" + String.format("%sbpm", pointValues.getyValue()));
                            batimento.add(bpmString);
                            //Log.e("batimento",batimento.toString());
                        }
                    }
                }
                finalText = finalText.concat("Sono");
                finalText = buildText(sono,finalText);
                finalText = finalText.concat("IMC");
                finalText = buildText(imc,finalText);
                finalText = finalText.concat("Glicémia");
                finalText = buildText(glicemia,finalText);
                finalText = finalText.concat("Pressão Máxima");
                finalText = buildText(pressaoMax,finalText);
                finalText = finalText.concat("Pressão Mínima");
                finalText = buildText(pressaoMin,finalText);
                finalText = finalText.concat("Batimento");
                finalText = buildText(batimento,finalText);

                Intent mSharingIntent = new Intent(Intent.ACTION_SEND);
                mSharingIntent.setType("text/plain");
                mSharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Valores das minhas medições - " + user.getDisplayName());
                mSharingIntent.putExtra(Intent.EXTRA_TEXT,finalText);
                startActivity(Intent.createChooser(mSharingIntent,"Partilhe os seus dados"));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}