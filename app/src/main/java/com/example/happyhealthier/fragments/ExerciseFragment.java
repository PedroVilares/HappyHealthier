package com.example.happyhealthier.fragments;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyhealthier.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Objects;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.getSystemServiceName;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment implements AdapterView.OnItemSelectedListener,SensorEventListener{

    public ExerciseFragment() {
        // Required empty public constructor
    }

    MapView mMapView;
    private GoogleMap mGoogleMap;

    Spinner spinnerExercises;
    String exerciseChosen;
    ImageButton playButton;
    CardView card1,card2,card3;
    Chronometer chronometer;
    boolean isExercising = false;
    TextView steps,kms;


    //TODO: Sacar o icone de stop
    //TODO: Configurar os botões
    //TODO: Botão da música

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_exercise, container, false);

        //Maps//
        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMyLocationEnabled(true);
            }
        });

        //Spinner//
        spinnerExercises = v.findViewById(R.id.spinnerExercises);
        String[] exercises = getResources().getStringArray(R.array.exercises);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(),android.R.layout.simple_spinner_item,exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercises.setAdapter(adapter);
        spinnerExercises.setOnItemSelectedListener(this);

        //StartExercise//
        playButton = v.findViewById(R.id.playButton);
        card1 = v.findViewById(R.id.cardView1);
        card2 = v.findViewById(R.id.cardView2);
        chronometer = v.findViewById(R.id.time);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity().getApplicationContext(),"Run bitch!",Toast.LENGTH_SHORT).show();
                isExercising = true;
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.setFormat("%s");
                chronometer.start();
            }
        });

        //StepCounter//
        SensorManager sensorManager = (SensorManager) this.requireActivity().getSystemService(Activity.SENSOR_SERVICE);
        steps = v.findViewById(R.id.stepsText);
        kms = v.findViewById(R.id.distanceText);
        assert sensorManager != null;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor != null) {
            sensorManager.registerListener((SensorEventListener) this,stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(requireActivity().getApplicationContext(),"Sensor not found!",Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerExercises) {
            exerciseChosen = parent.getItemAtPosition(position).toString();
            //Toast.makeText(getActivity().getApplicationContext(),exerciseChosen,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isExercising){
            steps.setText(String.valueOf(event.values[0]));
            kms.setText(String.valueOf(event.values[0]/1312.34));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
