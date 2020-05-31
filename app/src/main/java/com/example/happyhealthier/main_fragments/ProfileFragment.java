package com.example.happyhealthier.main_fragments;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyhealthier.R;
import com.example.happyhealthier.helper_fragments.ImagePickerFragment;
import com.example.happyhealthier.helper_fragments.ProfileDataFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private Uri userprofilePic;
    private ImageView profilePic;
    private TextView userNameText, userAgeText, userHeightText, userWeightText,imcValueText,imcUnitsText,imcDescriptionText;
    //UserData//
    private ListenerRegistration userdataListener;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ConstraintLayout layout= v.findViewById(R.id.layout_profile);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //ProfilePic//
        profilePic = v.findViewById(R.id.profilePic);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference pictureRef = storageReference.child("Profile Pictures").child(userID);

        if (pictureRef != null) {
            pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    userprofilePic = uri;
                    Picasso.get().load(uri).into(profilePic);
                    profilePic.setBackgroundColor(Color.TRANSPARENT);
                }
            });
        }

        //UserData//
        userNameText = v.findViewById(R.id.usenameText);
        userAgeText = v.findViewById(R.id.userAgeText);
        userHeightText = v.findViewById(R.id.userHeightText);
        userWeightText = v.findViewById(R.id.userWeightText);


        //BotãoCâmara//
        ImageButton imageButton = v.findViewById(R.id.profilePicButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPictureFragment();
            }
        });

        //BotãoEditar//
        ImageButton editUserDataButton = v.findViewById(R.id.editUserInfo);
        editUserDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDataFragment profileDataFragment = new ProfileDataFragment();
                profileDataFragment.show(getChildFragmentManager(), "edit_info");
            }
        });

        //IMC
        imcValueText = v.findViewById(R.id.imcValueText);
        imcDescriptionText=v.findViewById(R.id.imcDescriptionText);
        imcUnitsText= v.findViewById(R.id.imcUnits);

        return v;

    }

    private double imcCalculator(Double weight,Double height) {

        return (weight / (height * height));
    }

    //Foto de Perfil//
    @AfterPermissionGranted(321)
    private void launchPictureFragment() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            ImagePickerFragment imagePickerFragment = new ImagePickerFragment();
            imagePickerFragment.show(getParentFragmentManager(), "image_picker");
        } else {
            EasyPermissions.requestPermissions(this, "O acesso à câmara e à galeria permite-lhe definir a sua nova foto de perfil.", 321, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        ImagePickerFragment imagePickerFragment = new ImagePickerFragment();
        imagePickerFragment.show(getChildFragmentManager(), "image_picker");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //FIREBASE//
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userdataDocument = db.collection(Objects.requireNonNull(user).getUid()).document("user_data");

    @Override
    public void onStart() {
        super.onStart();
        userdataListener = userdataDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Erro ao carregar dados\n" + e, Toast.LENGTH_SHORT).show();
                    return;
                }
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("Nome");
                    Double userAge = documentSnapshot.getDouble("Idade");
                    Double userWeight = documentSnapshot.getDouble("Peso");
                    Double userHeight = documentSnapshot.getDouble("Altura");

                    userNameText.setText(username);
                    userAgeText.setText(String.format("%s anos", String.valueOf(userAge.intValue())));
                    userHeightText.setText(String.format("%s m", String.valueOf( userHeight)));
                    userWeightText.setText(String.format("%s kg", String.valueOf(userWeight)));

                    //IMC//
                    imcUnitsText.setText("kg/m^2");
                    double IMC = imcCalculator(userWeight,userHeight);
                    Log.d("imc", String.valueOf(IMC));
                    imcValueText.setText(String.format("%.1f",IMC));
                    if (IMC>2 && IMC<=18.5) {
                        imcDescriptionText.setText("Está abaixo do peso ideal,\nconsidere ingerir mais\ncalorias diariamente!");
                    }
                    if (IMC>18.5 && IMC<25) {
                        imcDescriptionText.setText("Está no seu peso ideal!\nContinue com uma dieta\nvariada e com\nexercício regular!");
                    }
                    if (IMC>25){
                        imcDescriptionText.setText("Está acima do seu peso ideal,\nconsidere ser mais ativo\nseguindo uma dieta equilibrada!");
                    }
                    if (IMC<2) {
                        imcValueText.setText("0.0");
                        imcDescriptionText.setText("Sem dados");
                    }
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        userdataListener.remove();
    }
}
