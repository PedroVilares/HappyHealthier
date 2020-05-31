package com.example.happyhealthier.helper_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyhealthier.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDataFragment extends DialogFragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference userdataDocumentReference = db.collection(Objects.requireNonNull(user).getUid()).document("user_data");

    private EditText usernameEdit,userageEdit,userheightEdit,userweightEdit;
    private Button mEditButton;

    public ProfileDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_data, container, false);
        usernameEdit = v.findViewById(R.id.nomeInputText);
        userageEdit = v.findViewById(R.id.idadeInputText);
        userheightEdit= v.findViewById(R.id.alturaInputText);
        userweightEdit = v.findViewById(R.id.pesoInputText);

        userdataDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DocumentSnapshot userdataDocument = documentSnapshot;
                String oldUsername = userdataDocument.getString("Nome");
                Double oldUserAge = userdataDocument.getDouble("Idade");
                Double oldUserHeight = userdataDocument.getDouble("Altura");
                Double oldUserWeight = userdataDocument.getDouble("Peso");

                usernameEdit.setText(oldUsername);
                userageEdit.setText(String.valueOf(oldUserAge));
                userheightEdit.setText(String.valueOf(oldUserHeight));
                userweightEdit.setText(String.valueOf(oldUserWeight));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Sem Internet",Toast.LENGTH_SHORT).show();
            }
        });

        mEditButton = v.findViewById(R.id.editUserDataButton);

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> new_user_data = new HashMap<>();

                String newUsername = usernameEdit.getText().toString();
                String newUserAge = userageEdit.getText().toString();
                String newUserHeight = userheightEdit.getText().toString();
                String newUserWeight = userweightEdit.getText().toString();


                new_user_data.put("Nome",newUsername);
                new_user_data.put("Idade", Double.parseDouble(newUserAge));
                new_user_data.put("Altura",Double.parseDouble(newUserHeight));
                new_user_data.put("Peso",Double.parseDouble(newUserWeight));

                db.collection(user.getUid()).document("user_data").set(new_user_data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Sem Internet",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Dados atualizados",Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
            }
        });

        return v;
    }
}
