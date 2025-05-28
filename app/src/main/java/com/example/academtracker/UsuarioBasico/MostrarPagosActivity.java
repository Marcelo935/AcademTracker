package com.example.academtracker.UsuarioBasico;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.adapter.AlumnosPagos_adapter;
import com.example.academtracker.model.Pagos;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MostrarPagosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerViewPagos;
    private AlumnosPagos_adapter adapter;
    private ArrayList<Pagos> pagosList;
    private String alumnoId;
    private TextView tvEmptyMessage;

    private EditText etFechaFiltro, etConceptoFiltro;
    private Button btnAplicarFiltro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_pagos);

        db = FirebaseFirestore.getInstance();

        recyclerViewPagos = findViewById(R.id.recyclerViewPagos);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        etFechaFiltro = findViewById(R.id.etFechaFiltro);
        etConceptoFiltro = findViewById(R.id.etConceptoFiltro);
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro);

        pagosList = new ArrayList<>();

        alumnoId = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        recyclerViewPagos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnosPagos_adapter(pagosList);
        recyclerViewPagos.setAdapter(adapter);

        if (alumnoId != null) {
            mostrarPagos(alumnoId, null, null);
        } else {
            Toast.makeText(this, "No se pudo obtener la información del usuario", Toast.LENGTH_SHORT).show();
        }

        // Abrir DatePicker al tocar el campo fecha
        etFechaFiltro.setFocusable(false);
        etFechaFiltro.setClickable(true);
        etFechaFiltro.setOnClickListener(v -> mostrarDatePicker());

        btnAplicarFiltro.setOnClickListener(v -> {
            String fechaFiltro = etFechaFiltro.getText().toString().trim();
            String conceptoFiltro = etConceptoFiltro.getText().toString().trim();

            mostrarPagos(alumnoId,
                    fechaFiltro.isEmpty() ? null : fechaFiltro,
                    conceptoFiltro.isEmpty() ? null : conceptoFiltro);
        });
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int year = calendario.get(Calendar.YEAR);
        int month = calendario.get(Calendar.MONTH);
        int day = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MostrarPagosActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(year1, month1, dayOfMonth);
                    SimpleDateFormat formato = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
                    etFechaFiltro.setText(formato.format(fechaSeleccionada.getTime()));
                }, year, month, day);

        datePickerDialog.show();
    }

    private void mostrarPagos(String alumnoId, String fechaFiltro, String conceptoFiltro) {
        db.collection("Pagos")
                .whereEqualTo("alumnoId", alumnoId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            pagosList.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Double monto = document.getDouble("monto");
                                String fechaCompleta = document.getString("fecha");
                                String metodoPago = document.getString("metodoPago");
                                String conceptoPago = document.getString("conceptoPago");

                                // Extraer solo la parte yyyy-MM-dd si la fecha incluye hora
                                String fecha = null;
                                if (fechaCompleta != null) {
                                    fecha = fechaCompleta.length() >= 10 ? fechaCompleta.substring(0, 10) : fechaCompleta;
                                }

                                boolean cumpleFecha = (fechaFiltro == null) || (fecha != null && fecha.equals(fechaFiltro));
                                boolean cumpleConcepto = (conceptoFiltro == null) || (conceptoPago != null && conceptoPago.toLowerCase().contains(conceptoFiltro.toLowerCase()));

                                if (cumpleFecha && cumpleConcepto) {
                                    Pagos pago = new Pagos(alumnoId, fecha, metodoPago, monto, conceptoPago);
                                    pagosList.add(pago);
                                }
                            }

                            if (pagosList.isEmpty()) {
                                recyclerViewPagos.setVisibility(View.GONE);
                                tvEmptyMessage.setVisibility(View.VISIBLE);
                            } else {
                                adapter.notifyDataSetChanged();
                                recyclerViewPagos.setVisibility(View.VISIBLE);
                                tvEmptyMessage.setVisibility(View.GONE);
                            }
                        } else {
                            recyclerViewPagos.setVisibility(View.GONE);
                            tvEmptyMessage.setVisibility(View.VISIBLE);
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
