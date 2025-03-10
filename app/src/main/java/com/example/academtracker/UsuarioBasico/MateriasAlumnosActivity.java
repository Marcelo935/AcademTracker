package com.example.academtracker.UsuarioBasico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.adapter.MateriasMostrar_adapter;
import com.example.academtracker.model.MateriasAlumnos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MateriasAlumnosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MateriasMostrar_adapter adapter;
    private List<MateriasAlumnos> materiasList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias_alumnos);

        recyclerView = findViewById(R.id.recyclerViewMaterias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MateriasMostrar_adapter(materiasList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarMaterias();
    }

    private void cargarMaterias() {
        String emailAlumno = auth.getCurrentUser().getEmail(); // Obtener el email del alumno logueado

        if (emailAlumno == null) {
            Toast.makeText(this, "Error: No se pudo obtener el correo del alumno.", Toast.LENGTH_SHORT).show();
            Log.e("MateriasAlumnosActivity", "No se pudo obtener el correo del alumno.");
            return;
        }

        Log.d("MateriasAlumnosActivity", "Correo del alumno: " + emailAlumno);

        // Referencia a la colección de calificaciones del alumno en Firestore
        CollectionReference materiasRef = db.collection("Alumnos")
                .document(emailAlumno)
                .collection("calificaciones");

        // Consultar las materias del alumno
        materiasRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verifica si se encontraron documentos
                        if (task.getResult().isEmpty()) {
                            Log.d("MateriasAlumnosActivity", "El alumno no tiene materias asignadas.");
                            Toast.makeText(this, "El alumno no tiene materias asignadas.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("MateriasAlumnosActivity", "Documentos recuperados: " + task.getResult().size());
                            materiasList.clear();  // Limpiar la lista antes de agregar nuevos elementos

                            // Iterar sobre los documentos de materias
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombreMateria = document.getId();  // Obtener el nombre de la materia
                                Log.d("MateriasAlumnosActivity", "Materia: " + nombreMateria);

                                // Crear un mapa para almacenar las calificaciones
                                Map<String, Double> calificaciones = new HashMap<>();

                                // Obtener las calificaciones de los parciales
                                for (String key : document.getData().keySet()) {
                                    Object value = document.get(key);
                                    if (value instanceof String) {
                                        // Si el valor es un String, intenta convertirlo a Double
                                        try {
                                            double calificacion = Double.parseDouble((String) value);
                                            calificaciones.put(key, calificacion);
                                        } catch (NumberFormatException e) {
                                            Log.e("MateriasAlumnosActivity", "Error al convertir calificación a Double: " + value);
                                        }
                                    }
                                }

                                // Crear objeto MateriasAlumnos y agregar a la lista
                                MateriasAlumnos materia = new MateriasAlumnos(nombreMateria, calificaciones);
                                materiasList.add(materia);
                            }

                            Log.d("MateriasAlumnosActivity", "Materias cargadas: " + materiasList.size());
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al cargar las materias.", Toast.LENGTH_SHORT).show();
                        Log.e("MateriasAlumnosActivity", "Error al obtener las materias", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MateriasAlumnosActivity", "Error al obtener materias", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
