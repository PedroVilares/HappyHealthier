package com.example.happyhealthier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyhealthier.data_activities.AlturaActivity;
import com.example.happyhealthier.data_activities.BatimentoActivity;
import com.example.happyhealthier.data_activities.CaloriasActivity;
import com.example.happyhealthier.data_activities.DistanciaActivity;
import com.example.happyhealthier.data_activities.GlicemiaActivity;
import com.example.happyhealthier.data_activities.PassosActivity;
import com.example.happyhealthier.data_activities.PesoActivity;
import com.example.happyhealthier.data_activities.PressaoActivity;
import com.example.happyhealthier.data_activities.SonoActivity;
import com.example.happyhealthier.data_activities.imcActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    int []arr;
    String text[];
    Context context;



    public RecyclerViewAdapter(int[] arr, String text_arr[], Context context) {
        this.arr = arr;
        text = text_arr;
        this.context = context;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        //Create a object of myViewHolder
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.imageView.setImageResource(arr[position]);
        holder.textView.setText(text[position]);
    }

    @Override
    public int getItemCount() {
        // return total number of items
        return arr.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //Create ImageView and TextView

        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView=itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        Log.d("LogDiaViewHolder", "Posicao" + position);

                        switch (position) {
                            case 0:
                                Intent intent = new Intent(context, SonoActivity.class);
                                //intent.putExtra("posicao",position);
                                context.startActivity(intent);
                                break;
                            case 1:
                                Intent intent1 = new Intent(context, PesoActivity.class);
                                context.startActivity(intent1);
                                break;
                            case 2:
                                Intent intent2 = new Intent(context, imcActivity.class);
                                context.startActivity(intent2);
                                break;
                            case 3:
                                Intent intent3 = new Intent(context, PassosActivity.class);
                                context.startActivity(intent3);
                                break;
                            case 4:
                                Intent intent4 = new Intent(context, DistanciaActivity.class);
                                context.startActivity(intent4);
                                break;
                            case 5:
                                Intent intent5 = new Intent(context, CaloriasActivity.class);
                                context.startActivity(intent5);
                                break;
                            case 6:
                                Intent intent6 = new Intent(context, BatimentoActivity.class);
                                context.startActivity(intent6);
                                break;
                            case 7:
                                Intent intent7 = new Intent(context, PressaoActivity.class);
                                context.startActivity(intent7);
                                break;
                            case 8:
                                Intent intent8 = new Intent(context, GlicemiaActivity.class);
                                context.startActivity(intent8);
                                break;
                            case 9:
                                Intent intent9 = new Intent(context, AlturaActivity.class);
                                context.startActivity(intent9);
                                break;
                        }
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {


        }
    }
}
