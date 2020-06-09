package com.example.happyhealthier.main_fragments;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.happyhealthier.R;
import com.example.happyhealthier.RecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeasurementsFragment extends Fragment {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter recyclerViewAdapter;

    int []arr= {R.drawable.sono,R.drawable.peso,R.drawable.imc, R.drawable.passos,
            R.drawable.km, R.drawable.cal, R.drawable.batimento, R.drawable.pressao, R.drawable.glicemia,
            R.drawable.altura};

    String text_arr[];

    public MeasurementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_measurements, container, false);


        recyclerView = v.findViewById(R.id.recyclerView);
        text_arr = getResources().getStringArray(R.array.medicoes);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutManager = new GridLayoutManager(getContext(),2);
        } else {
            layoutManager = new GridLayoutManager(getContext(),3);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(arr,text_arr,getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        return v;
    }
}
