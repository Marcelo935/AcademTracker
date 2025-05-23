package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.adapter.GraficasAdapter;
import com.example.academtracker.model.Materia;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PerfilAlumnoActivity extends AppCompatActivity {

    MaterialButton regresar;
    TextView emailalumno, nombrealumno, gradoalumno, grupoalumno;
    ImageView imageView;

    RecyclerView recyclerView;
    List<Materia> listaMaterias;
    GraficasAdapter adapter;

    private FirebaseFirestore db;
    String useremail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_alumno);

        regresar = findViewById(R.id.returnbtn);
        nombrealumno = findViewById(R.id.nombrecompleto);
        emailalumno = findViewById(R.id.email);
        gradoalumno = findViewById(R.id.grado);
        grupoalumno = findViewById(R.id.grupo);
        imageView = findViewById(R.id.imageView2); // <- Aquí se enlaza el ImageView de la foto

        recyclerView = findViewById(R.id.recyclerViewGraficas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaMaterias = new ArrayList<>();
        adapter = new GraficasAdapter(listaMaterias);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            useremail = user.getEmail();
            cargarDatosUsuario(useremail);
            cargarCalificaciones(useremail);
        }

        regresar.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilAlumnoActivity.this, AlumnoActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void cargarDatosUsuario(String email) {
        db.collection("Alumnos").document(email).get()
                .addOnSuccessListener(document -> {
                    nombrealumno.setText("Nombre: " + document.getString("nombre"));
                    emailalumno.setText("Email: " + document.getString("email"));
                    gradoalumno.setText("Grado: " + document.getString("grado"));
                    grupoalumno.setText("Grupo: " + document.getString("grupo"));

                    // Cargar imagen con Picasso
                    String fotoUrl = document.getString("foto");
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Picasso.get()
                                .load(fotoUrl)
                                .placeholder(R.drawable.noneuser) // imagen por defecto mientras carga
                                .error(R.drawable.noneuser)       // imagen por defecto si falla
                                .into(imageView);
                    }
                });
    }

    private void cargarCalificaciones(String email) {
        db.collection("Alumnos")
                .document(email)
                .collection("calificaciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double sumaPromediosMaterias = 0;
                    int contadorMaterias = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String nombre = doc.getId();

                        double p1 = safeValueToDouble(doc.get("1er parcial"));
                        double p2 = safeValueToDouble(doc.get("2do parcial"));
                        double p3 = safeValueToDouble(doc.get("3er parcial"));

                        double promedioMateria = (p1 + p2 + p3) / 3;

                        sumaPromediosMaterias += promedioMateria;
                        contadorMaterias++;

                        listaMaterias.add(new Materia(nombre,
                                String.format("%.2f", p1),
                                String.format("%.2f", p2),
                                String.format("%.2f", p3)));
                    }
                    adapter.notifyDataSetChanged();

                    if (contadorMaterias > 0) {
                        double promedioGeneral = sumaPromediosMaterias / contadorMaterias;

                        MaterialButton btnVerPromedio = findViewById(R.id.btnVerPromedio);
                        btnVerPromedio.setOnClickListener(v -> {
                            Intent intent = new Intent(PerfilAlumnoActivity.this, ResultadoPromedioActivity.class);
                            intent.putParcelableArrayListExtra("listaMaterias", new ArrayList<>(listaMaterias));
                            startActivity(intent);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de error si falla la carga
                });
    }

    private double safeValueToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String safeValueToString(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Number) {
            return String.valueOf(value);
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), PerfilAlumnoActivity.class));
        finish();
    }
}
