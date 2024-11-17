package com.example.academtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.model.Profesores;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Secretarias_materias_man extends AppCompatActivity {

    Secretarias_materias_adapter secretariasMateriasAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profesores_asignar_materias);
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.RecVi_SecretariaAsignarProfesores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference profesRef = mFirestore.collection("Profesores");
        Query query = profesRef;
        FirestoreRecyclerOptions<Profesores> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Profesores>()
                        .setQuery(query, Profesores.class).
                        build();
        secretariasMateriasAdapter = new Secretarias_materias_adapter(firestoreRecyclerOptions, this);
        secretariasMateriasAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(secretariasMateriasAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        secretariasMateriasAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (secretariasMateriasAdapter != null) {
            secretariasMateriasAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }

}
