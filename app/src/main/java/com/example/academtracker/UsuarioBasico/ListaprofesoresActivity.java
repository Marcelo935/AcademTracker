package com.example.academtracker.UsuarioBasico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Alumnos_Profesores_adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListaprofesoresActivity extends AppCompatActivity {

    private RecyclerView profesoresRecyclerView;
    private Alumnos_Profesores_adapter adaptador;
    private ArrayList<String> Listaprofesor;
    private ArrayList<String> Listamateria;
    private ArrayList<String> Listagrado;
    private ArrayList<String> ListanombreProfesor;  // Nueva lista para almacenar los nombres de los profesores

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesores);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener usuario autenticado
        FirebaseUser usuarioActual = mAuth.getCurrentUser();
        if (usuarioActual == null) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String alumnoEmail = usuarioActual.getEmail(); // Obtener correo del alumno logueado

        // Inicializar listas
        Listaprofesor = new ArrayList<>();
        Listamateria = new ArrayList<>();
        Listagrado = new ArrayList<>();
        ListanombreProfesor = new ArrayList<>();  // Inicializar lista para los nombres de los profesores

        // Configurar RecyclerView
        profesoresRecyclerView = findViewById(R.id.viewprofesores);
        profesoresRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar adaptador vacío
        adaptador = new Alumnos_Profesores_adapter(Listamateria, Listaprofesor, Listagrado, ListanombreProfesor);
        profesoresRecyclerView.setAdapter(adaptador);

        // Cargar datos del alumno y los profesores
        cargarProfesoresDelAlumno(alumnoEmail);
    }

    private void cargarProfesoresDelAlumno(String alumnoEmail) {
        db.collection("Alumnos").document(alumnoEmail).get()
                .addOnSuccessListener(alumnoDoc -> {
                    if (alumnoDoc.exists()) {
                        String alumnoNombre = alumnoDoc.getString("nombre");
                        Log.d("Firestore", "Alumno encontrado: " + alumnoNombre);
                        buscarProfesoresPorAlumno(alumnoNombre);
                    } else {
                        Log.e("Firestore", "No se encontró el alumno con el email: " + alumnoEmail);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener los datos del alumno", e));
    }

    private void buscarProfesoresPorAlumno(String alumnoNombre) {
        db.collection("Profesores").get()
                .addOnSuccessListener(profesores -> {
                    Log.d("Firestore", "Total profesores encontrados: " + profesores.size());

                    // Limpiar las listas antes de llenarlas
                    Listaprofesor.clear();
                    Listamateria.clear();
                    Listagrado.clear();
                    ListanombreProfesor.clear();  // Limpiar la nueva lista para los nombres de los profesores

                    for (DocumentSnapshot profesorDoc : profesores) {
                        String profesorEmail = profesorDoc.getId();
                        String profesorNombre = profesorDoc.getString("nombre");  // Obtener el nombre del profesor

                        profesorDoc.getReference().collection("materias").get()
                                .addOnSuccessListener(materias -> {
                                    for (DocumentSnapshot materiaDoc : materias) {
                                        String materiaNombre = materiaDoc.getId();

                                        // Obtener el mapa de alumnos
                                        Map<String, Object> alumnosMap = (Map<String, Object>) materiaDoc.get("alumnos");

                                        if (alumnosMap != null) {
                                            for (Map.Entry<String, Object> entry : alumnosMap.entrySet()) {
                                                Map<String, Object> alumnoData = (Map<String, Object>) entry.getValue();
                                                String nombreAlumnoFirestore = (String) alumnoData.get("nombre");

                                                if (nombreAlumnoFirestore != null && nombreAlumnoFirestore.equalsIgnoreCase(alumnoNombre)) {

                                                    // Agregar los datos correctamente
                                                    Listaprofesor.add(profesorEmail);  // Añadir el correo del profesor
                                                    ListanombreProfesor.add(profesorNombre);  // Añadir el nombre del profesor
                                                    Listamateria.add(materiaNombre);  // Añadir el nombre de la materia
                                                    Listagrado.add((String) alumnoData.get("grado"));  // Añadir el grado del alumno
                                                }
                                            }
                                        }
                                    }

                                    // Actualizar el RecyclerView después de haber agregado todos los datos
                                    actualizarRecyclerView();

                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener materias del profesor", e));
                    }

                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener profesores", e));
    }

    private void actualizarRecyclerView() {
        runOnUiThread(() -> {
            Log.d("RecyclerView", "Actualizando RecyclerView...");

            // Si las listas no están vacías, actualizar el RecyclerView
            if (!Listaprofesor.isEmpty()) {
                adaptador.notifyDataSetChanged();
            } else {
                Log.e("RecyclerView", "No se encontraron profesores.");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
