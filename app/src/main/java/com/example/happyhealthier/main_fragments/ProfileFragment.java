package com.example.happyhealthier.main_fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.happyhealthier.R;
import com.example.happyhealthier.helper_fragments.ImagePickerFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Uri userprofilePic;
    private ImageView profilePic;
    private TextView userName,userAge,userHeight,userWeight;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        //ProfilePic//
        profilePic = v.findViewById(R.id.profilePic);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pictureRef = storageReference.child("Profile Pictures").child(userID);

        //UserData//
        userName = v.findViewById(R.id.usenameText);
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        userName.setText(username);

        if (pictureRef != null) {
            pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    userprofilePic = uri;
                    Picasso.get().load(uri).into(profilePic);
                    profilePic.setBackgroundColor(Color.TRANSPARENT);
                }
            });}


        //BotãoCâmara//
        ImageButton imageButton = v.findViewById(R.id.profilePicButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerFragment imagePickerFragment = new ImagePickerFragment();
                imagePickerFragment.show(getChildFragmentManager(),"image_picker");
            }
        });

        //IMC
        TextView imcValueText, imcDescriptionText,imcUnitsText;

        double IMC = 0;
        //TODO: Se o utilizador tiver valores de peso e altura, calcular o imc pela função, se não, é zero;
        String IMCDescription;

        return v;

    }

    private double imcCalculator(){
        double peso = 0;
        double altura = 0;

        return (peso/(altura*altura));
    }
}
