package com.example.academtracker.UsuarioBasico;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.adapter.AlumnosPagos_adapter;
import com.example.academtracker.model.Pagos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MostrarPagosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewPagos;
    private AlumnosPagos_adapter adapter;
    private ArrayList<Pagos> pagosList;
    private String alumnoId;
    private TextView tvEmptyMessage; // Asegúrate de que esta variable sea de tipo TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_pagos);

        // Inicializar Firestore y la vista
        db = FirebaseFirestore.getInstance();
        recyclerViewPagos = findViewById(R.id.recyclerViewPagos);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage); // Esta línea debería funcionar sin errores
        pagosList = new ArrayList<>();

        // Obtener el correo del usuario autenticado
        alumnoId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Configurar RecyclerView
        recyclerViewPagos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnosPagos_adapter(pagosList);
        recyclerViewPagos.setAdapter(adapter);

        if (alumnoId != null) {
            // Consultar los pagos de este alumno en Firestore
            mostrarPagos(alumnoId);
        } else {
            Toast.makeText(this, "No se pudo obtener la información del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarPagos(String alumnoId) {
        // Consultar la colección de Pagos donde el campo alumnoId coincida con el email del alumno
        db.collection("Pagos")
                .whereEqualTo("alumnoId", alumnoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Limpiar la lista antes de agregar nuevos pagos
                            pagosList.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Obtener los datos del pago, asegurándote de manejar los tipos de datos correctos
                                Double monto = document.getDouble("monto"); // Convertir a String si es necesario
                                String fecha = document.getString("fecha");
                                String metodoPago = document.getString("metodoPago");

                                // Crear el objeto Pagos y agregarlo a la lista
                                Pagos pago = new Pagos(alumnoId, fecha, metodoPago, monto);
                                pagosList.add(pago);
                            }
                            adapter.notifyDataSetChanged(); // Actualizar la RecyclerView
                            recyclerViewPagos.setVisibility(View.VISIBLE);
                            tvEmptyMessage.setVisibility(View.GONE); // Ocultar mensaje de "sin pagos"
                        } else {
                            // Si no hay pagos registrados
                            recyclerViewPagos.setVisibility(View.GONE);
                            tvEmptyMessage.setVisibility(View.VISIBLE); // Mostrar el mensaje de "sin pagos"
                        }
                    } else {
                        Toast.makeText(MostrarPagosActivity.this, "Error al obtener los pagos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }

}
