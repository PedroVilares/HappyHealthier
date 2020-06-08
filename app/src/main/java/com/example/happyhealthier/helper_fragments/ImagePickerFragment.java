package com.example.happyhealthier.helper_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.happyhealthier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagePickerFragment extends BottomSheetDialogFragment {

    private static final int RESULT_OK = -1;
    private static final int CAMERA_REQUEST_CODE = 1034;
    Button cameraButton, galleryButton;
    Uri profilePic;
    private ProgressDialog progressDialog;


    private StorageReference storageReference;

    public ImagePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image_picker, container, false);

        cameraButton = v.findViewById(R.id.cameraButton);
        galleryButton = v.findViewById(R.id.galleryButton);
        progressDialog = new ProgressDialog(this.getContext());

        //Camera//
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        //Gallery//
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK).setType("image/*");
                startActivityForResult(intent, 3);
            }
        });


        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {

                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                if (photo == null){
                    Log.e("urifragment", "still null");
                }else {
                    Log.e("urifragment", "pic not null");
                }
                Uri uri = getImageUri(this.getContext(),photo);
                Log.e("urifragment", String.valueOf(uri));
                Log.e("urifragment", String.valueOf(uri));
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference filepath = storageReference.child("Profile Pictures").child(userID);

                assert uri != null;
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Upload Unsuccessful",Toast.LENGTH_SHORT).show();
                    }
                });

//                profilePic = data.getData();
//                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                storageReference = FirebaseStorage.getInstance().getReference();
//
//                StorageReference pictureRef = storageReference.child(userID);
//                pictureRef.putFile(profilePic);
                    Toast.makeText(getContext(), "Imagem da camara", Toast.LENGTH_SHORT).show();


                } else {

                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();

                    profilePic = data.getData();
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    storageReference = FirebaseStorage.getInstance().getReference();

                    StorageReference filepath = storageReference.child("Profile Pictures").child(userID);
                    filepath.putFile(profilePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Upload Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Failed to set new profile pic", Toast.LENGTH_SHORT).show();
            }
        }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
