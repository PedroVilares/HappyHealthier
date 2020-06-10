package com.example.happyhealthier.main_fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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

import com.example.happyhealthier.ExerciseService;
import com.example.happyhealthier.MainActivity;
import com.example.happyhealthier.PointValue;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static android.Manifest.*;

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
    private double calories;
    private TextView distanceFinal, timeFinal, stepsFinal, caloriesFinal, exerciseFinal;

    //Location//
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FusedLocationProviderClient fusedLocationClient;
    private Location previousLocation;

    private ArrayList<Polyline> polylines = new ArrayList<>();
    private ArrayList<LatLng> allLatLngs = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.P)
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
                startService(0,String.valueOf(0));
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
                distanceFinal.setText(String.format("%.2f km", kmsDone));
                String pronoun;
                if(exerciseChosen.equals("Ciclismo")){
                    pronoun = "do ";
                } else {
                    pronoun = "da ";
                }
                String atividade = "Resumo " + pronoun + exerciseChosen;
                calories = calorieCalculator(exerciseChosen,exerciseTimeLong);
                Log.e("calories",String.valueOf(calories));
                exerciseFinal.setText(atividade);
                timeFinal.setText(String.valueOf(exerciseTime));
                stepsFinal.setText(String.valueOf(stepsTaken));
                caloriesFinal.setText(String.format("%.2f kcal",calories));
                locationManager.removeUpdates(locationListener);
                fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Marker finiMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Fim"));
                        finiMarker.showInfoWindow();
                    }
                });

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference referencePassos = database.getReference(user.getUid()).child("Passos");
                DatabaseReference referenceKms = database.getReference(user.getUid()).child("Distância");
                DatabaseReference referenceCals = database.getReference(user.getUid()).child("Calorias");

                String idPassos = referencePassos.push().getKey();
                String idKms = referenceKms.push().getKey();
                String idCals = referenceCals.push().getKey();

                long x = new Date().getTime();
                PointValue pointValuePassos = new PointValue(x,stepsTaken);
                PointValue pointValueKms = new PointValue(x,kmsDone);
                PointValue pointValueCals = new PointValue(x,calories);

                referencePassos.child(idPassos).setValue(pointValuePassos);
                referenceKms.child(idKms).setValue(pointValueKms);
                referenceCals.child(idCals).setValue(pointValueCals).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.e("realtime","Com Sucesso");
                    }
                });
                stopService(v);
                sendNotificationExercise(v,calories);

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

    private void sendNotificationExercise(View v, double cals) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        Notification notification = new NotificationCompat.Builder(getContext(),"notificacao_exercicio")
                .setContentTitle("Está de Parabéns!")
                .setContentText(String.format("Ufa! Bom esforço, queimou %.2f calorias!",cals))
                .setSmallIcon(R.drawable.ic_exercise)
                .build();

        notificationManager.notify(1,notification);
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
            if(stepsInitial<1) {
                stepsInitial = (int) event.values[0];
            }

            String time = chronometer.getText().toString();
            stepsTaken=(int) event.values[0]-stepsInitial;
            steps.setText(String.valueOf(stepsTaken));
            kmsDone = stepsTaken / 1312.34;
            kms.setText(String.format("%.2f km", kmsDone));
            startService(kmsDone,time);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public double calorieCalculator(final String exerciseType, final long exerciseTimeLong){
        final int runningMET = 7;
        final int walkingMET = 4;
        final int cyclingMET = 6;

        final double[] exerciseTime = {(double) exerciseTimeLong};
        Log.e("tempo", String.valueOf((exerciseTime[0] /1000)/60));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userdataDocument = db.collection(Objects.requireNonNull(user).getUid()).document("user_data");
        userdataDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Erro ao carregar dados\n" + e, Toast.LENGTH_SHORT).show();
                }
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    double caloriesPerMin = 0;
                    double peso = documentSnapshot.getDouble("Peso");

                    switch (exerciseType) {
                        case "Corrida":
                            caloriesPerMin = (runningMET* peso *3.5)/200;
                            calories = caloriesPerMin *((exerciseTime[0] /1000)/60);
                            break;

                        case "Caminhada":
                            caloriesPerMin = (walkingMET* peso *3.5)/200;
                            calories = caloriesPerMin *((exerciseTime[0] /1000)/60);
                            break;

                        case "Ciclismo":
                            caloriesPerMin = (cyclingMET* peso *3.5)/200;
                            calories = caloriesPerMin *((exerciseTime[0] /1000)/60);
                    }
            }
        }});
        Log.e("cpm", String.valueOf(calories));
        return calories;
    }

    private void startService(double kms,String time){

        Intent serviceIntent = new Intent(getContext(), ExerciseService.class);
        serviceIntent.putExtra("distance",String.valueOf(kms));
        serviceIntent.putExtra("time",time);

        requireActivity().startService(serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(getContext(),ExerciseService.class);
        requireActivity().stopService(serviceIntent);
        Toast.makeText(getContext(),"Fim do Exercício",Toast.LENGTH_SHORT).show();

    }
}
