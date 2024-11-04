package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academtracker.LoginActivity;
import com.example.academtracker.R;
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

public class ProfesorActivity extends AppCompatActivity {

    TextView nombreprofesor;

    MaterialButton perfil,listamaterias,calificaciones,asistencias,salir;
    private FirebaseFirestore db; // Agrega la instancia de FirebaseFirestore
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuariobasico_profesor);

        perfil = findViewById(R.id.perfilbtn);
        listamaterias = findViewById(R.id.materiasbtn);
        calificaciones = findViewById(R.id.califbtn);
        asistencias = findViewById(R.id.asistenciasbtn);
        salir = findViewById(R.id.salirbtn);



        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfesorActivity.this, PerfilProfesorActivity.class);
                startActivity(intent);
                finish();
            }

        });

        listamaterias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfesorActivity.this, ListaMateriasActivity.class);
                startActivity(intent);
                finish();
            }

        });

        calificaciones.setOnClickListener(new View.OnClickListener() {
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
                                String Nombre = "";
                                String Foto =  "";
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                                    //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    Grado = document.getString("grado");
                                    Nombre = document.getString("nombre");
                                    Foto = document.getString("foto");

                                    Intent intent = new Intent(ProfesorActivity.this, CalificacionesActivity.class);
                                    intent.putExtra("Nombre",Nombre);//Mandamos el valor de la variable al perfil de alumnos
                                    intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                    intent.putExtra("Foto",Foto);
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

        asistencias.setOnClickListener(new View.OnClickListener() {
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
                                String Nombre = "";
                                String Foto =  "";

                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                                    //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    Grado = document.getString("grado");
                                    Nombre = document.getString("nombre");
                                    Foto = document.getString("foto");

                                    Intent intent = new Intent(ProfesorActivity.this, AsistenciasActivity.class);
                                    intent.putExtra("Nombre",Nombre);//Mandamos el valor de la variable al perfil de alumnos
                                    intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                    intent.putExtra("Foto",Foto);
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
                Intent intent = new Intent(ProfesorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}