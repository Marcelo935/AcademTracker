package com.example.academtracker.UsuarioBasico;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.MateriasAdapter;
import com.example.academtracker.model.Materia;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalificacionesActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificaciones);

        mFirestore = FirebaseFirestore.getInstance();

        uid = getIntent().getStringExtra("uid");

        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Materia> materias = new ArrayList<>();
        MateriasAdapter adapter = new MateriasAdapter(materias, mFirestore, uid);
        recyclerView.setAdapter(adapter);

        mFirestore.collection("Alumnos")
                .document(uid)
                .collection("calificaciones")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(CalificacionesActivity.this, "Error al cargar materias", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            materias.clear();

                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                String nombre = document.getId(); // El ID del documento será el nombre de la materia
                                materias.add(new Materia(nombre, "", "", ""));
                            }

                            adapter.notifyDataSetChanged();
                        }
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
