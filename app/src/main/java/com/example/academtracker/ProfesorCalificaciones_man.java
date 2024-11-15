package com.example.academtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.UsuarioBasico.ProfesorActivity;
import com.example.academtracker.UsuarioMedio.DirectorActivity;
import com.example.academtracker.model.Alumnos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProfesorCalificaciones_man extends AppCompatActivity {
    Profesor_Calificaciones_adapter profesorCalificacionesAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_calificaciones);
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.RecVi_ProfesorCalifAlumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        Query query = alumnosRef;
        FirestoreRecyclerOptions<Alumnos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Alumnos>()
                        .setQuery(query, Alumnos.class).
                        build();
        profesorCalificacionesAdapter = new Profesor_Calificaciones_adapter(firestoreRecyclerOptions, this);
        profesorCalificacionesAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(profesorCalificacionesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        profesorCalificacionesAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (profesorCalificacionesAdapter != null) {
            profesorCalificacionesAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ProfesorActivity.class));
        finish();
    }
}
