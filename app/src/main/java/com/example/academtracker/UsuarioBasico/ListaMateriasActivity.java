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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaMateriasActivity.this, ProfesorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarMaterias(String profesorEmail) {
        // Referencia a la subcolección "materias" dentro del documento del profesor
        CollectionReference materiasRef = db.collection("Profesores")
                .document(profesorEmail)
                .collection("materias");

        // Consultar Firestore para obtener las materias
        materiasRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        // Verifica si no está vacío
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.e("ListaMateriasActivity", "No se encontraron materias.");
                        } else {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                // Obtener el nombre de la materia del ID del documento
                                String nombreMateria = document.getId(); // El ID es el nombre de la materia
                                String grado = document.getString("grado");
                                String grupo = document.getString("grupo");

                                // Verificar los valores obtenidos
                                Log.d("ListaMateriasActivity", "Materia: " + nombreMateria + ", Grado: " + grado + ", Grupo: " + grupo);

                                // Si los datos son nulos, asignar valores predeterminados
                                if (grado == null) grado = "No disponible";
                                if (grupo == null) grupo = "No disponible";

                                // Crear un objeto Materias y agregarlo a la lista
                                Materias materia = new Materias(nombreMateria, grado, grupo);
                                materiasList.add(materia);
                            }

                            // Configurar el adaptador con los datos obtenidos
                            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ListaMateriasActivity", "Error al obtener materias", e);
                    }
                });
    }

}

