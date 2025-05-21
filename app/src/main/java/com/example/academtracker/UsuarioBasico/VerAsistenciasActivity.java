package com.example.academtracker.UsuarioBasico;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.adapter.AsistenciasAdapter;
import com.example.academtracker.model.Asistencia;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class VerAsistenciasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private AsistenciasAdapter adapter;
    private List<Asistencia> listaAsistencias;

    private EditText etFiltroAlumno, etFiltroMateria;
    private Button btnAplicarFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_asistencias);

        db = FirebaseFirestore.getInstance();

        etFiltroAlumno = findViewById(R.id.etFiltroAlumno);
        etFiltroMateria = findViewById(R.id.etFiltroMateria);
        btnAplicarFiltros = findViewById(R.id.btnAplicarFiltros);

        recyclerView = findViewById(R.id.recyclerAsistencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaAsistencias = new ArrayList<>();
        adapter = new AsistenciasAdapter(listaAsistencias);
        recyclerView.setAdapter(adapter);

        // Cargar todas las asistencias al inicio
        cargarAsistencias(null, null);

        btnAplicarFiltros.setOnClickListener(v -> {
            String alumno = etFiltroAlumno.getText().toString().trim();
            String materia = etFiltroMateria.getText().toString().trim();

            cargarAsistencias(
                    alumno.isEmpty() ? null : alumno,
                    materia.isEmpty() ? null : materia
            );
            etFiltroAlumno.setText("");
            etFiltroMateria.setText("");
        });
    }

    private void cargarAsistencias(String alumnoID, String materia) {
        Query query = db.collection("Asistencias");

        if (alumnoID != null) {
            query = query.whereEqualTo("alumnoID", alumnoID);
        }
        if (materia != null) {
            query = query.whereEqualTo("materia", materia);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaAsistencias.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Asistencia asistencia = doc.toObject(Asistencia.class);
                        listaAsistencias.add(asistencia);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar asistencias", Toast.LENGTH_SHORT).show();
                });
    }
}
