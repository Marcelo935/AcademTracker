package com.example.academtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.academtracker.UsuarioMedio.DirectorActivity;
import com.example.academtracker.model.Alumnos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DirectorActualizaAlumnos_man extends AppCompatActivity {
    Director_ActAlumnos_adapter directorActAlumnosAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_actualiza_alumnos);
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.RecVi_DirectorActAlumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        Query query = alumnosRef;
        FirestoreRecyclerOptions<Alumnos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Alumnos>()
                        .setQuery(query, Alumnos.class).
                        build();
        directorActAlumnosAdapter = new Director_ActAlumnos_adapter(firestoreRecyclerOptions, this);
        directorActAlumnosAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(directorActAlumnosAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        directorActAlumnosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (directorActAlumnosAdapter != null) {
            directorActAlumnosAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DirectorActivity.class));
        finish();
    }
}