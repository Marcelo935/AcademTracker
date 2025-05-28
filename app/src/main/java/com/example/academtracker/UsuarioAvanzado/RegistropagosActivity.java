package com.example.academtracker.UsuarioAvanzado;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.academtracker.R;
import com.example.academtracker.Secretaria_pagos_man;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class RegistropagosActivity extends AppCompatActivity {

    private EditText etMonto, etFecha, etAlumnoId, etConceptoPago;
    private Spinner spMetodoPago;
    private Button btnRegistrarPago, btnVerPagos;
    private FirebaseFirestore db;
    private String metodoPagoSeleccionado;
    private String conceptoPago;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registropagos);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        etMonto = findViewById(R.id.etMonto);
        etFecha = findViewById(R.id.etFecha);
        etAlumnoId = findViewById(R.id.etAlumnoId);
        etConceptoPago = findViewById(R.id.etConceptoPago); // Nuevo campo
        spMetodoPago = findViewById(R.id.spMetodoPago);
        btnRegistrarPago = findViewById(R.id.btnRegistrarPago);
        btnVerPagos = findViewById(R.id.btnVerPagos);

        // Configurar Spinner método de pago
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metodos_pago, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMetodoPago.setAdapter(adapter);

        spMetodoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                metodoPagoSeleccionado = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                metodoPagoSeleccionado = "";
            }
        });

        // Selector de fecha
        etFecha.setOnClickListener(v -> mostrarSelectorFecha());

        // Botón registrar pago
        btnRegistrarPago.setOnClickListener(v -> registrarPago());

        // Botón ver pagos
        btnVerPagos.setOnClickListener(v -> {
            Intent intent = new Intent(RegistropagosActivity.this, Secretaria_pagos_man.class);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarSelectorFecha() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String fechaSeleccionada = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    etFecha.setText(fechaSeleccionada);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void registrarPago() {
        String montoString = etMonto.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String alumnoId = etAlumnoId.getText().toString().trim();
        String conceptoPago = etConceptoPago.getText().toString().trim();

        // Validar campos obligatorios
        if (montoString.isEmpty() || metodoPagoSeleccionado.isEmpty() || fecha.isEmpty() || alumnoId.isEmpty() || conceptoPago.isEmpty()) {
            Toast.makeText(RegistropagosActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoString);
        } catch (NumberFormatException e) {
            Toast.makeText(RegistropagosActivity.this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que el alumno exista
        db.collection("Alumnos").document(alumnoId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Payment payment = new Payment(monto, metodoPagoSeleccionado, fecha, alumnoId, conceptoPago);

                db.collection("Pagos")
                        .add(payment)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(RegistropagosActivity.this, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RegistropagosActivity.this, "Error al registrar el pago", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(RegistropagosActivity.this, "El ID del alumno no está registrado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(RegistropagosActivity.this, "Error al verificar el ID del alumno", Toast.LENGTH_SHORT).show();
        });
    }

    private void limpiarCampos() {
        etMonto.setText("");
        etFecha.setText("");
        etAlumnoId.setText("");
        etConceptoPago.setText("");
        spMetodoPago.setSelection(0);
    }

    public static class Payment {
        private double monto;
        private String metodoPago;
        private String fecha;
        private String alumnoId;
        private String conceptoPago;

        public Payment(double monto, String metodoPago, String fecha, String alumnoId, String conceptoPago) {
            this.monto = monto;
            this.metodoPago = metodoPago;
            this.fecha = fecha;
            this.alumnoId = alumnoId;
            this.conceptoPago = conceptoPago;
        }

        public double getMonto() { return monto; }
        public String getMetodoPago() { return metodoPago; }
        public String getFecha() { return fecha; }
        public String getAlumnoId() { return alumnoId; }
        public String getConceptoPago() { return conceptoPago; }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }
}
