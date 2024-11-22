package com.example.academtracker.UsuarioAvanzado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.Secretarias_materias_man;
import com.example.academtracker.UsuarioBasico.ProfesorActivity;
import com.example.academtracker.adapter.Profesores_Editarcalf_adapter;
import com.example.academtracker.adapter.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifcarSecretariasActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private String uid;
    private Spinner spinnerMaterias;
    private EditText alumno;
    private Button guardar;
    private ArrayList<String> listaMaterias = new ArrayList<>();
    private String materiaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcar_secretarias);

        mFirestore = FirebaseFirestore.getInstance();
        uid = getIntent().getStringExtra("uid");

        spinnerMaterias = findViewById(R.id.spinner_materias);
        alumno = findViewById(R.id.et_alumno);
        guardar = findViewById(R.id.btn_save);

        // Cargar las materias del profesor y llenar el Spinner
        cargarMaterias();

        // Listener del botón Guardar
        guardar.setOnClickListener(v -> {
            if (materiaSeleccionada != null && !alumno.getText().toString().isEmpty()) {
                validarMateria(uid, materiaSeleccionada, alumno.getText().toString());
            } else {
                Toast.makeText(this, "Selecciona una materia y escribe el nombre del alumno.", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener del Spinner para capturar la materia seleccionada
        spinnerMaterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                materiaSeleccionada = listaMaterias.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                materiaSeleccionada = null;
            }
        });
    }

    private void cargarMaterias() {
        mFirestore.collection("Materias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaMaterias.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String materia = document.getString("nombre"); // Asumiendo que cada documento tiene un campo "nombre"
                        if (materia != null) {
                            listaMaterias.add(materia);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ModifcarSecretariasActivity.this,
                            android.R.layout.simple_spinner_item,
                            listaMaterias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMaterias.setAdapter(adapter);

                    if (!listaMaterias.isEmpty()) {
                        materiaSeleccionada = listaMaterias.get(0);
                    } else {
                        Toast.makeText(this, "No hay materias disponibles.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar las materias.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al cargar las materias", e);
                });
    }


    private void validarMateria(String idProfesor, String nombreMateria, String nombreAlumno) {
        DocumentReference materiaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias")
                .document(nombreMateria);

        // Validar si la materia ya existe
        materiaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                // Si no existe, agregar la materia y luego el alumno
                agregarMateria(idProfesor, nombreMateria);
                agregarAlumno(nombreMateria, nombreAlumno, idProfesor);
            } else {
                // Si ya existe, solo agregar el alumno
                agregarAlumno(nombreMateria, nombreAlumno, idProfesor);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al validar la materia.", Toast.LENGTH_SHORT).show();
            Log.e("ModifcarSecretarias", "Error al validar la materia", e);
        });
    }

    private void agregarMateria(String idProfesor, String idMateria) {
        CollectionReference addMateriaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias");

        addMateriaRef.document(idMateria).set(new HashMap<>())
                .addOnSuccessListener(aVoid -> {
                    // Una vez creada la materia, agregarla a las calificaciones de los alumnos
                    agregarMateriaACalificacionesDeAlumnos(idMateria);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al agregar la materia.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al agregar la materia", e);
                });
    }

    private void agregarMateriaACalificacionesDeAlumnos(String idMateria) {
        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        alumnosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot alumnoSnapshot : queryDocumentSnapshots) {
                String alumnoId = alumnoSnapshot.getId();

                DocumentReference calificacionesRef = alumnosRef.document(alumnoId)
                        .collection("calificaciones")
                        .document(idMateria);

                Map<String, Object> calificacionesIniciales = new HashMap<>();
                calificacionesIniciales.put("1er parcial", 0);
                calificacionesIniciales.put("2do parcial", 0);
                calificacionesIniciales.put("3er parcial", 0);

                calificacionesRef.set(calificacionesIniciales)
                        .addOnSuccessListener(aVoid -> Log.d("ModifcarSecretarias", "Materia agregada a calificaciones de " + alumnoId))
                        .addOnFailureListener(e -> Log.e("ModifcarSecretarias", "Error al agregar materia a calificaciones de " + alumnoId, e));
            }
            Toast.makeText(this, "Materia agregada a calificaciones de todos los alumnos.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener los alumnos.", Toast.LENGTH_SHORT).show();
            Log.e("ModifcarSecretarias", "Error al obtener los alumnos", e);
        });
    }

    private void agregarAlumno(String idMateria, String nombreAlumno, String idProfesor) {
        DocumentReference addStudent = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias")
                .document(idMateria);

            String idAlumno = addStudent.getId() + "_" + System.currentTimeMillis();
                addStudent.update("alumnos." + idAlumno, nombreAlumno)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Alumno registrado con éxito.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Fallo al registrar Alumno.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Secretarias_materias_man.class));
        finish();
    }
}
