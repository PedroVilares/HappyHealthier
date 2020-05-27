package com.example.happyhealthier.main_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment implements AdapterView.OnItemSelectedListener,SensorEventListener{

    public ExerciseFragment() {
        // Required empty public constructor
    }

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private Spinner spinnerExercises;
    private ImageButton playButton,musicButton,pauseButton,stopButton;
    private CardView card1,card2,card3;
    private Chronometer chronometer;
    private boolean isExercising = false;
    private String exerciseChosen;
    private TextView steps,kms,exercisePicked;

    //EXERCISE VARIABLES//
    private String exerciseTime;
    private long exerciseTimeLong;
    private int stepsTaken = 0;
    private double kmsDone;
    private int stepsInitial = 0;
    private TextView distanceFinal, timeFinal, stepsFinal, caloriesFinal, exerciseFinal;

    //Location//
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationClient;
    private Location previousLocation;

    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<LatLng> allLatLngs = new ArrayList<>();

    //TODO: integrar no Firebase


    @SuppressLint("MissingPermission")
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

        //Route//
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                PolylineOptions lineOptions = new PolylineOptions()
                        .add(new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude()))
                        .add(new LatLng(location.getLatitude(), location.getLongitude()))
                        .color(Color.BLUE)
                        .width(9);
                // add the polyline to the map
                Polyline polyline = mGoogleMap.addPolyline(lineOptions);
                // set the zindex so that the poly line stays on top of my tile overlays
                polyline.setZIndex(1000);
                // add the poly line to the array so they can all be removed if necessary
                polylines.add(polyline);
                // add the latlng from this point to the array
                allLatLngs.add(new LatLng(location.getLatitude(), location.getLongitude()));
                previousLocation = location;

                // check if the positions added is a multiple of 100, if so, redraw all of the polylines as one line (this helps with rendering the map when there are thousands of points)
                if(allLatLngs.size() % 100 == 0) {
                    // first remove all of the existing polylines
                    for(Polyline pline : polylines) {
                        pline.remove();
                    }
                    // create one new large polyline
                    Polyline routeSoFar = mGoogleMap.addPolyline(new PolylineOptions().color(Color.BLUE).width(9));
                    // draw the polyline for the route so far
                    routeSoFar.setPoints(allLatLngs);
                    // set the zindex so that the poly line stays on top of my tile overlays
                    routeSoFar.setZIndex(1000);
                    // clear the polylines array
                    polylines.clear();
                    // add the new poly line as the first element in the polylines array
                    polylines.add(routeSoFar);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Spinner//
        spinnerExercises = v.findViewById(R.id.spinnerExercises);
        final String[] exercises = getResources().getStringArray(R.array.exercises);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(),R.layout.spinner_item,exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercises.setAdapter(adapter);
        spinnerExercises.setOnItemSelectedListener(this);

        //StartExercise//
        playButton = v.findViewById(R.id.playButton);
        card1 = v.findViewById(R.id.cardView1);
        card1.setVisibility(View.VISIBLE);
        card2 = v.findViewById(R.id.cardView2);
        card2.setVisibility(View.INVISIBLE);
        chronometer = v.findViewById(R.id.time);
        exercisePicked = v.findViewById(R.id.exerciseChosenText);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(requireActivity().getApplicationContext(),"Run bitch!",Toast.LENGTH_SHORT).show();
                isExercising = true;
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.VISIBLE);
                card3.setVisibility(View.INVISIBLE);
                exercisePicked.setText(exerciseChosen);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.setFormat("%s");
                chronometer.start();
                polylines = new ArrayList<>();
                allLatLngs = new ArrayList<>();
                locationManager.requestLocationUpdates("gps",5000,0,locationListener);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                Marker iniMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Início"));
                                iniMarker.showInfoWindow();
                                previousLocation = location;
                                if (location != null) {
                                    // Logic to handle location object
                                }
                            }
                        });

            }
        });

        //PauseExercise//
        pauseButton = v.findViewById(R.id.playButton1);
        final long[] timeStopped = {0};
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExercising) {
                    pauseButton.setImageDrawable(
                            ContextCompat.getDrawable(requireActivity().getApplicationContext(),R.drawable.ic_play_arrow_black_24dp));
                    isExercising = false;
                    chronometer.stop();
                    timeStopped[0] = chronometer.getBase() - SystemClock.elapsedRealtime();

                } else {
                    pauseButton.setImageDrawable(
                            ContextCompat.getDrawable(requireActivity().getApplicationContext(),R.drawable.ic_pause));
                    chronometer.setBase(SystemClock.elapsedRealtime() + timeStopped[0]);
                    chronometer.start();
                    isExercising = true;
                }
            }
        });

        //StopExercise//
        stopButton = v.findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExercising = false;
                exerciseTimeLong = SystemClock.elapsedRealtime() - chronometer.getBase();
                exerciseTime = chronometer.getText().toString();
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                card1.setVisibility(View.VISIBLE);
                card2.setVisibility(View.INVISIBLE);
                card3.setVisibility(View.VISIBLE);
                distanceFinal.setText(String.format("%s km",String.valueOf(kmsDone)));
                String pronoun;
                if(exerciseChosen.equals("Ciclismo")){
                    pronoun = "do ";
                } else {
                    pronoun = "da ";
                }
                String atividade = "Resumo " + pronoun + exerciseChosen;
                exerciseFinal.setText(atividade);
                timeFinal.setText(String.valueOf(exerciseTime));
                stepsFinal.setText(String.valueOf(stepsTaken));
                caloriesFinal.setText(String.valueOf(calorieCalculator(exerciseChosen,exerciseTimeLong)));
                locationManager.removeUpdates(locationListener);
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Marker finiMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Fim"));
                        finiMarker.showInfoWindow();
                    }
                });
                //exerciseChosen
                //distance
                //steps
            }
        });

        //ReportExercise//
        card3 = v.findViewById(R.id.cardview3);
        distanceFinal = v.findViewById(R.id.distanciaReportText);
        stepsFinal = v.findViewById(R.id.passosReportText);
        timeFinal = v.findViewById(R.id.tempoReportText);
        exerciseFinal = v.findViewById(R.id.exercicioReportText);
        caloriesFinal = v.findViewById(R.id.caloriasReportText);


        //StepCounter//
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Activity.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        steps = v.findViewById(R.id.stepsText);
        kms = v.findViewById(R.id.distanceText);

        if (stepSensor != null) {
            sensorManager.registerListener(this,stepSensor, SensorManager.SENSOR_DELAY_UI);
            Toast.makeText(requireActivity().getApplicationContext(),"Pedometer found!",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity().getApplicationContext(),"Pedometer not found!",Toast.LENGTH_SHORT).show();
        }

        //MusicButton//
        musicButton = v.findViewById(R.id.musicButton);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,Intent.CATEGORY_APP_MUSIC));
                startActivity(intent);
            }
        });
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
        card1.setVisibility(View.VISIBLE);
        card2.setVisibility(View.INVISIBLE);
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
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isExercising){
            Log.i("oi","Sensor reading");
            if(stepsInitial<1) {
                stepsInitial = (int) event.values[0];
                Log.i("oi",String.valueOf(stepsInitial));
            }

            stepsTaken=(int) event.values[0]-stepsInitial;
            steps.setText(String.valueOf(stepsTaken));
            kmsDone = stepsTaken / 1312.34;
            kms.setText(String.format("%.2f km", kmsDone));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public double calorieCalculator(String exerciseType,long exerciseTimeLong){
        int runningMET = 7;
        int walkingMET = 4;
        int cyclingMET = 6;

        double peso = 0;
        double caloriesPerMin = 0;
        double exerciseTime = (double) exerciseTimeLong;

        switch (exerciseType) {
            case "Corrida":
                caloriesPerMin = (runningMET*peso *3.5)/200;
                break;

            case "Caminhada":
                caloriesPerMin = (walkingMET*peso*3.5)/200;
                break;

            case "Ciclismo":
                caloriesPerMin = (cyclingMET*peso*3.5)/200;
        }


        return caloriesPerMin*((exerciseTime/1000)/60);
    }
}
