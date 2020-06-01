package com.example.happyhealthier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CaloriasActivity extends AppCompatActivity {

    EditText yValue;
    Button btn_insert;

    FirebaseDatabase database;
    DatabaseReference reference;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
    GraphView graphView;
    LineGraphSeries series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorias);

        Intent intent1 = getIntent();

        yValue = (EditText) findViewById(R.id.pressaoMaxima);
        btn_insert = (Button) findViewById(R.id.btnInsert);
        graphView = (GraphView) findViewById(R.id.graph);

        series = new LineGraphSeries();
        graphView.addSeries(series);

        //design do gráfico
        //series.setColor(Color.rgb(245, 126, 66));
        //series.setThickness(8);
        series.setDrawBackground(true);
        //series.setBackgroundColor(Color.argb(60,247, 228, 218));
        series.setDrawDataPoints(true);
        //series.setDataPointsRadius(17);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user.getUid()).child("Calorias");

        setListeners();

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    return sdf.format(new Date((long) value));
                }else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
    }

    private void setListeners() {
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = reference.push().getKey();
                long x = new Date().getTime();
                float y = Integer.parseInt(yValue.getText().toString());

                PointValue pointValue = new PointValue(x,y);
                reference.child(id).setValue(pointValue);

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index = 0;

                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    PointValue pointValues = myDataSnapshot.getValue(PointValue.class);

                    dp[index] = new DataPoint(pointValues.getxValue(), pointValues.getyValue());
                    index++;
                }

                series.resetData(dp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}