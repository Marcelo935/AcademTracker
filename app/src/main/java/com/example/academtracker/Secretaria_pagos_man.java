package com.example.academtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.UsuarioAvanzado.RegistropagosActivity;
import com.example.academtracker.UsuarioBasico.ProfesorActivity;
import com.example.academtracker.model.Pagos;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Secretaria_pagos_man extends AppCompatActivity {
    Secretaria_pagos_adapter secretariaPagosAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore mFirestore;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretaria_pagos);
        mFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.RecVi_SecretariaPagos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CollectionReference alumnosRef = mFirestore.collection("Pagos");
        Query query = alumnosRef;
        FirestoreRecyclerOptions<Pagos> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Pagos>()
                        .setQuery(query, Pagos.class).
                        build();
        secretariaPagosAdapter = new Secretaria_pagos_adapter(firestoreRecyclerOptions, this);
        secretariaPagosAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(secretariaPagosAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        secretariaPagosAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (secretariaPagosAdapter != null) {
            secretariaPagosAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), RegistropagosActivity.class));
        finish();
    }
}
