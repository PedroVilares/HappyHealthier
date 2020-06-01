package com.example.happyhealthier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PressaoActivity extends AppCompatActivity {

    EditText yValue;
    EditText yValueMin;
    Button btn_insert;

    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseDatabase databaseMin;
    DatabaseReference referenceMin;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
    GraphView graphView;
    LineGraphSeries series;
    LineGraphSeries seriesMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressao);

        Intent intent1 = getIntent();

        yValue = findViewById(R.id.pressaoMaxima);
        yValueMin = findViewById(R.id.pressaoMinima);
        btn_insert = findViewById(R.id.btnInsert);
        graphView = findViewById(R.id.graph);

        series = new LineGraphSeries();
        graphView.addSeries(series);

        //graphView.getGridLabelRenderer().setNumHorizontalLabels(2);
        //graphView.getGridLabelRenderer().setLabelsSpace(10);

        //graphView.getGridLabelRenderer().setNumVerticalLabels(2);
        //graphView.getGridLabelRenderer().getNumVerticalLabels()

        //design do gráfico
        //series.setColor(Color.rgb(245, 126, 66));
        //series.setThickness(8);
        //series.setDrawBackground(true);
        //series.setBackgroundColor(Color.argb(60,247, 228, 218));
        series.setDrawDataPoints(true);
        //series.setDataPointsRadius(17);
        series.setTitle("Pressão Arterial Máxima");
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.getLegendRenderer().setFixedPosition(10,15);
        //graphView.getViewport().setScalableY(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(user.getUid()).child("Pressao");

        seriesMin = new LineGraphSeries();
        graphView.addSeries(seriesMin);
        seriesMin.setDrawDataPoints(true);

        seriesMin.setColor(Color.rgb(245, 126, 66));
        seriesMin.setTitle("Pressão Arterial Mínima");
        databaseMin = FirebaseDatabase.getInstance();
        referenceMin = databaseMin.getReference("PressaoMin");

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(300);
        graphView.getViewport().setMinY(50);


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
                double y =Double.parseDouble(yValue.getText().toString());

                PointValue pointValue = new PointValue(x,y);
                reference.child(id).setValue(pointValue);

                String idMin = reference.push().getKey();
                double yMin = Double.parseDouble(yValueMin.getText().toString());
                long xMin = new Date().getTime();

                PointValue pointValueMin = new PointValue(xMin,yMin);
                referenceMin.child(idMin).setValue(pointValueMin);

                if(y < 130.0 & yMin < 85.0){
                    Toast.makeText(getApplicationContext(),"Pressão arterial normal " +
                            "Mantenha um estilo de vida saudável!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Pressão arterial elevada. Pratique um estilo de vida saudável!",Toast.LENGTH_LONG).show();
                }


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

        referenceMin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataPoint[] dp = new DataPoint[(int) dataSnapshot.getChildrenCount()];
                int index = 0;

                for (DataSnapshot myDataSnapshot : dataSnapshot.getChildren()) {

                    PointValue pointValues = myDataSnapshot.getValue(PointValue.class);

                    dp[index] = new DataPoint(pointValues.getxValue(), pointValues.getyValue());
                    index++;
                }

                seriesMin.resetData(dp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
