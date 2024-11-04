package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.academtracker.LoginActivity;
import com.example.academtracker.R;
import com.example.academtracker.UserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AlumnoActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    TextView nombrealumno;

    MaterialButton perfil,listaprofesores,estadistica,salir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuariobasico_alumnos);

        perfil = findViewById(R.id.perfilbtn);
        nombrealumno = findViewById(R.id.nombrealumno);
        listaprofesores = findViewById(R.id.profesoresbtn);
        estadistica = findViewById(R.id.statsbtn);
        salir = findViewById(R.id.salirbtn);


        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlumnoActivity.this, PerfilAlumnoActivity.class);
                startActivity(intent);
                finish();
            }

        });

        listaprofesores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlumnoActivity.this, ListaprofesoresActivity.class);
                startActivity(intent);
                finish();
            }

        });

        estadistica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                // Obtener referencia a la colección
                CollectionReference collectionReference = db.collection("Alumnos");

                // Realizar la consulta
                collectionReference.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot querySnapshot) {

                                String Grado = "";
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                                    //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    Grado = document.getString("grado");

                                    Intent intent = new Intent(AlumnoActivity.this, EstadisticasActivity.class);
                                    intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
            }

        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AlumnoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

}

}