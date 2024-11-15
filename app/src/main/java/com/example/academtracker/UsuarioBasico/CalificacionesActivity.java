package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.academtracker.DirectorActualizaAlumnos_man;
import com.example.academtracker.ProfesorCalificaciones_man;
import com.example.academtracker.R;
import com.example.academtracker.UsuarioMedio.ActualizarDirectorActivity;
import com.example.academtracker.UsuarioMedio.DirectorActivity;
import com.example.academtracker.adapter.Profesores_Calificaciones_adapter;
import com.example.academtracker.adapter.Useradapter;
import com.example.academtracker.model.Alumnos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CalificacionesActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private EditText algebra1, algebra2, algebra3, geografia1, geografia2, geografia3, historia1, historia2, historia3;
    private Button guardarA, guardarG, guardarH;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificaciones);

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Inicializar los campos
        algebra1 = findViewById(R.id.et_algebra1);
        algebra2 = findViewById(R.id.et_algebra2);
        algebra3 = findViewById(R.id.et_algebra3);
        geografia1 = findViewById(R.id.et_geografia1);
        geografia2 = findViewById(R.id.et_geografia2);
        geografia3 = findViewById(R.id.et_geografia3);
        historia1 = findViewById(R.id.et_historia1);
        historia2 = findViewById(R.id.et_historia2);
        historia3 = findViewById(R.id.et_historia3);

        // Inicializar botones
        guardarA = findViewById(R.id.button_algebra);
        guardarG = findViewById(R.id.button_geografia);
        guardarH = findViewById(R.id.button_historia);

        // Obtener el UID del intent
        uid = getIntent().getStringExtra("uid");

        // Asignar eventos de clic
        asignarEventosClick();
    }

    private void asignarEventosClick() {
        guardarA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parcial1 = algebra1.getText().toString();
                String parcial2 = algebra2.getText().toString();
                String parcial3 = algebra3.getText().toString();
                ActualizarCalificaciones(parcial1, parcial2, parcial3, "Algebra");
            }
        });

        guardarG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parcial1 = geografia1.getText().toString();
                String parcial2 = geografia2.getText().toString();
                String parcial3 = geografia3.getText().toString();
                ActualizarCalificaciones(parcial1, parcial2, parcial3, "Geografia");
            }
        });

        guardarH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parcial1 = historia1.getText().toString();
                String parcial2 = historia2.getText().toString();
                String parcial3 = historia3.getText().toString();
                ActualizarCalificaciones(parcial1, parcial2, parcial3, "Historia");
            }
        });
    }

    private void ActualizarCalificaciones(String parcial1, String parcial2, String parcial3, String materia) {
        if (uid == null || uid.isEmpty()) {
            Log.e("CalificacionesActivity", "UID del alumno no proporcionado");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("1er parcial", parcial1);
        map.put("2do parcial", parcial2);
        map.put("3er parcial", parcial3);

        mFirestore.collection("Alumnos")
                .document(uid)
                .collection("calificaciones")
                .document(materia)
                .set(map) // Usamos set para sobreescribir los datos en vez de update
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CalificacionesActivity", "Error al actualizar los datos", e);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ProfesorActivity.class));
        finish();
    }
}
