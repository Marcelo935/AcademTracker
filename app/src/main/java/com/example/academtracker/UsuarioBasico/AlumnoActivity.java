package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


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

public class AlumnoActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    TextView nombrealumno;

    MaterialButton perfil, listaprofesores, estadistica, salir, listapagos, calificaciones,graficoprom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuariobasico_alumnos);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar elementos del layout
        nombrealumno = findViewById(R.id.nombrealumno);
        perfil = findViewById(R.id.perfilbtn);
        listaprofesores = findViewById(R.id.profesoresbtn);
        estadistica = findViewById(R.id.statsbtn);
        salir = findViewById(R.id.salirbtn);
        listapagos = findViewById(R.id.pagosbtn);
        calificaciones = findViewById(R.id.calificacionesbtn);
        graficoprom = findViewById(R.id.promediochart);

        // Obtener usuario logueado
        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual != null) {
            String alumnoEmail = usuarioActual.getEmail();

            // Obtener el nombre del alumno desde Firestore
            db.collection("Alumnos").document(alumnoEmail).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombreAlumno = documentSnapshot.getString("nombre");
                            nombrealumno.setText(nombreAlumno); // Mostrar nombre en el TextView
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                    });
        }

        // Setear los OnClickListeners para los botones
        perfil.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoActivity.this, PerfilAlumnoActivity.class);
            startActivity(intent);
            finish();
        });

        graficoprom.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoActivity.this, ResultadoPromedioActivity.class);
            startActivity(intent);
            finish();
        });

        calificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoActivity.this, MateriasAlumnosActivity.class);
            startActivity(intent);
            finish();
        });

        listaprofesores.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoActivity.this, ListaprofesoresActivity.class);
            startActivity(intent);
            finish();
        });

        listapagos.setOnClickListener(v -> {
            Intent intent = new Intent(AlumnoActivity.this, MostrarPagosActivity.class);
            startActivity(intent);
            finish();
        });

        estadistica.setOnClickListener(v -> {
            db.collection("Alumnos").get()
                    .addOnSuccessListener(querySnapshot -> {
                        String Grado = "";
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Grado = document.getString("grado");

                            Intent intent = new Intent(AlumnoActivity.this, EstadisticasActivity.class);
                            intent.putExtra("Grado", Grado);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                    });
        });

        salir.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AlumnoActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
