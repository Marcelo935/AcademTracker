package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.academtracker.R;
import com.example.academtracker.adapter.MateriasProfesoresAdapter;
import com.example.academtracker.model.Materias;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListaMateriasActivity extends AppCompatActivity {

    private MaterialButton regresarButton;
    private RecyclerView recyclerViewMaterias;
    private MateriasProfesoresAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Materias> materiasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_materias);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas con IDs del XML
        regresarButton = findViewById(R.id.returnbtn);
        recyclerViewMaterias = findViewById(R.id.viewmaterias);
        recyclerViewMaterias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Inicializar lista y adaptador
        materiasList = new ArrayList<>();
        adapter = new MateriasProfesoresAdapter(this, materiasList);
        recyclerViewMaterias.setAdapter(adapter);

        // Obtener el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String profesorEmail = currentUser.getEmail();
            if (profesorEmail != null) {
                cargarMaterias(profesorEmail);
            } else {
                Log.e("ListaMateriasActivity", "El correo del usuario es nulo");
            }
        } else {
            Log.e("ListaMateriasActivity", "No hay un usuario logueado");
        }

        // Acción del botón regresar
        regresarButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListaMateriasActivity.this, ProfesorActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarMaterias(String profesorEmail) {
        CollectionReference materiasRef = db.collection("Profesores")
                .document(profesorEmail)
                .collection("materias");

        materiasRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e("ListaMateriasActivity", "No se encontraron materias.");
                    } else {
                        materiasList.clear(); // Limpiar lista antes de agregar nuevos datos
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            final String nombreMateria = document.getId();
                            final String grado = document.getString("grado") != null ? document.getString("grado") : "No disponible";
                            final String grupo = document.getString("grupo") != null ? document.getString("grupo") : "No disponible";

                            // Obtener los alumnos almacenados en el mapa dentro de Firestore
                            List<String> listaAlumnos = new ArrayList<>();
                            Map<String, Object> alumnosMap = (Map<String, Object>) document.get("alumnos");

                            if (alumnosMap != null) {
                                // Iterar sobre los alumnos en el mapa
                                for (Map.Entry<String, Object> entry : alumnosMap.entrySet()) {
                                    // Cada entry es un alumno con su id como clave
                                    Map<String, Object> alumnoDetalles = (Map<String, Object>) entry.getValue();

                                    // Obtener nombre, grado y grupo del alumno
                                    String nombreAlumno = (String) alumnoDetalles.get("nombre");
                                    String gradoAlumno = (String) alumnoDetalles.get("grado");
                                    String grupoAlumno = (String) alumnoDetalles.get("grupo");

                                    // Si los valores no están disponibles, asignar "No disponible"
                                    nombreAlumno = nombreAlumno != null ? nombreAlumno : "No disponible";
                                    gradoAlumno = gradoAlumno != null ? gradoAlumno : "No disponible";
                                    grupoAlumno = grupoAlumno != null ? grupoAlumno : "No disponible";

                                    // Formatear el detalle del alumno con saltos de línea
                                    String alumnoDetalle = "\n" + "Nombre: " + nombreAlumno + "\n" +
                                            "Grado: " + gradoAlumno + "\n" +
                                            "Grupo: " + grupoAlumno + "\n";

                                    // Agregar el detalle del alumno a la lista
                                    listaAlumnos.add(alumnoDetalle);
                                }

                                // Crear objeto Materias con la lista de alumnos
                                Materias materia = new Materias(nombreMateria, grado, grupo, listaAlumnos);
                                materiasList.add(materia);

                                // Notificar al adaptador una vez que los detalles estén obtenidos
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.e("ListaMateriasActivity", "El mapa de alumnos está vacío o no existe.");
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ListaMateriasActivity", "Error al obtener materias", e));
    }
}
