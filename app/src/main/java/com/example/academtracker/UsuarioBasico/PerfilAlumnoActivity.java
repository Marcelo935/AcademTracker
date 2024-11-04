package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Useradapter;
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

public class PerfilAlumnoActivity extends AppCompatActivity {

    MaterialButton regresar;

    String useremail = "";

    TextView emailalumno,nombrealumno,gradoalumno,grupoalumno;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_alumno);

        regresar = findViewById(R.id.returnbtn);
        nombrealumno = findViewById(R.id.nombrecompleto);
        emailalumno = findViewById(R.id.email);
        gradoalumno = findViewById(R.id.grado);
        grupoalumno = findViewById(R.id.grupo);

        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Alumnos");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        String getnombre = "";
                        String getgrupo = "";
                        String getgrado = "";
                        String getemail = "";
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null)
                        {
                            useremail = user.getEmail();//guardar lo que retorna lo del usuario actual
                        }
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String nombre1 = document.getString("nombre");
                            String email1 = document.getString("email");
                            String grado1 = document.getString("grado");
                            String grupo1 = document.getString("grupo");

                                //compara el email que sea igual al que se identifico y asi retorne los datos del usuario que esta autenticado
                                if(Objects.equals(email1,useremail))
                                {
                                    getnombre = "Nombre: " + nombre1;
                                    getemail = "Email: " + email1;
                                    getgrado = "Grado: " + grado1;
                                    getgrupo = "Grupo: " + grupo1;
                                }


                        }

                        nombrealumno.setText(getnombre);
                        emailalumno.setText(getemail);
                        gradoalumno.setText(getgrado);
                        grupoalumno.setText(getgrupo);
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

                Intent intent = new Intent(PerfilAlumnoActivity.this, AlumnoActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
}