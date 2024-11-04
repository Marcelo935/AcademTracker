package com.example.academtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.model.Alumnos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SecretariaActualizaAlumnos_man extends AppCompatActivity {

    Secretaria_ActAlumnos_adapter secretariaActAlumnosAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretaria_actualiza_alumnos);
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.RecVi_SecretariaActAlumnos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        Query query = alumnosRef;
        FirestoreRecyclerOptions<Alumnos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Alumnos>()
                        .setQuery(query, Alumnos.class).
                        build();
        secretariaActAlumnosAdapter = new Secretaria_ActAlumnos_adapter(firestoreRecyclerOptions, this);
        secretariaActAlumnosAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(secretariaActAlumnosAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        secretariaActAlumnosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (secretariaActAlumnosAdapter != null) {
            secretariaActAlumnosAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }

}
