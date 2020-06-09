package com.example.happyhealthier;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.happyhealthier.initial_pages.LauncherScreens;
import com.example.happyhealthier.initial_pages.WelcomeActivity;
import com.example.happyhealthier.main_fragments.AboutFragment;
import com.example.happyhealthier.main_fragments.ExerciseFragment;
import com.example.happyhealthier.main_fragments.HealthFragment;
import com.example.happyhealthier.main_fragments.MeasurementsFragment;
import com.example.happyhealthier.main_fragments.ProfileFragment;
import com.example.happyhealthier.main_fragments.SettingsFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int REQUEST_CODE = 1;
    private static final int SPLASH_TIME = 1000;
    private ListenerRegistration userdataListener;
    List<AuthUI.IdpConfig> providers;
    DrawerLayout drawer;
    LinearLayout layout;
    TextView navUsername;
    Map<String,Object> user_data = new HashMap<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userdataDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main_layout);
        //Navigation drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();

        //Setting text and picture on NavDrawer to user's name//
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.nav_nameText);
        final ImageView navPicture = headerView.findViewById(R.id.nav_profilepic);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userdataDocument = db.collection(user.getUid()).document("user_data");
            navUsername.setText(user.getDisplayName());
            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference pictureRef = storageReference.child("Profile Pictures").child(userID);

            if (pictureRef != null) {
                pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri userprofilePic = uri;
                        Picasso.get().load(uri).into(navPicture);
                        //profilePic.setBackgroundColor(Color.TRANSPARENT);
                    }
                });}


            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference(user.getUid()).child("Sono");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot sleepValue : dataSnapshot.getChildren()){
                        PointValue pointValues = sleepValue.getValue(PointValue.class);

                        long fireTime = pointValues.getxValue();

                        Intent intent = new Intent(getApplicationContext(),MyReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        Calendar sonoStartTime = Calendar.getInstance();
                        Calendar now = Calendar.getInstance();
                        sonoStartTime.set(Calendar.HOUR_OF_DAY,9);
                        sonoStartTime.set(Calendar.MINUTE, 0);
                        sonoStartTime.set(Calendar.SECOND,0);
                        if (now.after(sonoStartTime)) {
                            sonoStartTime.add(Calendar.DATE,1);
                        }
                        if(sonoStartTime.getTimeInMillis()-fireTime < 32400000){
                            sonoStartTime.add(Calendar.DATE,1);
                        }
                        Log.e("alarm", String.valueOf(sonoStartTime.getTimeInMillis()));
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,sonoStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        //Getting different login providers (Email,Google,Facebook)
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        if (savedInstanceState == null) {
            showSignInOptions();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeasurementsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_measures);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_measures:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MeasurementsFragment()).commit();
                        break;
                    case R.id.nav_exercise:
                        launchExerciseFragment();
                        break;
                    case R.id.nav_health:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HealthFragment()).commit();
                        break;
                    case R.id.nav_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                        break;
                    case R.id.nav_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                        break;
                    case R.id.nav_about:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    public void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().
                        setAvailableProviders(providers).
                        setTheme(R.style.MyTheme).
                        build(),
                REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){

                if(response.isNewUser()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user_data.put("Nome",user.getDisplayName());
                    user_data.put("Idade",1);
                    user_data.put("Altura",1);
                    user_data.put("Peso",1);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(user.getUid()).document("user_data").set(user_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Gravou",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LauncherScreens.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Falha no Registo",Toast.LENGTH_SHORT).show();
                            showSignInOptions();
                        }
                    });
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String username = user.getDisplayName();
                if (username != null){
                    Snackbar snackbar = Snackbar.make(layout,"Bem vindo, "+username,Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();

                } else{
                    Snackbar snackbar = Snackbar.make(layout,"Bem vindo",Snackbar.LENGTH_SHORT);
                    snackbar.setActionTextColor(Color.WHITE);
                    snackbar.show();
                }

            }
            else {
                assert response != null;
                Toast.makeText(this,response.getError().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            Log.i("frags",fragment.toString());
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @AfterPermissionGranted(123)
    private void launchExerciseFragment() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.FOREGROUND_SERVICE};
        if (EasyPermissions.hasPermissions(this,perms)){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExerciseFragment()).commit();
        } else {
            EasyPermissions.requestPermissions(this,"O acesso à sua localização permite que lhe mostremos um mapa" +
                    " para saber sempre onde está durante os seus exercícios.",123,perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExerciseFragment()).commit();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (userdataDocument != null){
            userdataListener = userdataDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Toast.makeText(getApplicationContext(), "Erro ao carregar dados\n" + e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("Nome");
                        navUsername.setText(username);
                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(userdataDocument != null){
            userdataListener.remove();
        }
    }
}
