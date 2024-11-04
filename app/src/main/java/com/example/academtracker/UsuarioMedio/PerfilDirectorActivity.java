package com.example.academtracker.UsuarioMedio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioBasico.AlumnoActivity;
import com.example.academtracker.UsuarioBasico.PerfilAlumnoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class PerfilDirectorActivity extends AppCompatActivity {

    MaterialButton regresar;
    TextView nombre,email;
    String useremail = "";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_director);

        regresar = findViewById(R.id.returnbtn);
        nombre = findViewById(R.id.nombrecompleto);
        email = findViewById(R.id.emaildirector);

        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Director");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        String getnombre = "";
                        String getemail = "";
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null)
                        {
                            useremail = user.getEmail();//guardar lo que retorna lo del usuario actual
                        }
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String nombre1 = document.getString("Nombre");
                            String email1 = document.getString("Email");


                            //compara el email que sea igual al que se identifico y asi retorne los datos del usuario que esta autenticado
                            if(Objects.equals(email1,useremail))
                            {
                                getnombre = "Nombre: " + nombre1;
                                getemail = "Email: " + email1;
                            }
                        }
                        nombre.setText(getnombre);
                        email.setText(getemail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilDirectorActivity.this, DirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}