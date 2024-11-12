package com.example.academtracker.UsuarioAvanzado;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.academtracker.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistropagosActivity extends AppCompatActivity {

    private EditText etMonto, etMetodoPago, etFecha, etAlumnoId;
    private Button btnRegistrarPago;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registropagos);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Vincular los elementos del layout
        etMonto = findViewById(R.id.etMonto);
        etMetodoPago = findViewById(R.id.etMetodoPago);
        etFecha = findViewById(R.id.etFecha);
        etAlumnoId = findViewById(R.id.etAlumnoId);
        btnRegistrarPago = findViewById(R.id.btnRegistrarPago);

        // Configurar el botón de registro de pago
        btnRegistrarPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarPago();
            }
        });
    }

    private void registrarPago() {
        // Obtener los datos ingresados
        String monto = etMonto.getText().toString().trim();
        String metodoPago = etMetodoPago.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String alumnoId = etAlumnoId.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (monto.isEmpty() || metodoPago.isEmpty() || fecha.isEmpty() || alumnoId.isEmpty()) {
            Toast.makeText(RegistropagosActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un mapa para almacenar los datos del pago
        Payment payment = new Payment(monto, metodoPago, fecha, alumnoId);

        // Guardar el pago en Firestore
        db.collection("Pagos")
                .add(payment)
                .addOnSuccessListener(documentReference -> {
                    // Pago guardado exitosamente
                    Toast.makeText(RegistropagosActivity.this, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show();
                    // Limpiar los campos
                    etMonto.setText("");
                    etMetodoPago.setText("");
                    etFecha.setText("");
                    etAlumnoId.setText("");
                })
                .addOnFailureListener(e -> {
                    // Error al guardar el pago
                    Toast.makeText(RegistropagosActivity.this, "Error al registrar el pago", Toast.LENGTH_SHORT).show();
                });
    }

    // Clase interna para representar los datos del pago
    public static class Payment {
        private String monto;
        private String metodoPago;
        private String fecha;
        private String alumnoId;

        public Payment(String monto, String metodoPago, String fecha, String alumnoId) {
            this.monto = monto;
            this.metodoPago = metodoPago;
            this.fecha = fecha;
            this.alumnoId = alumnoId;
        }

        public String getMonto() {
            return monto;
        }

        public String getMetodoPago() {
            return metodoPago;
        }

        public String getFecha() {
            return fecha;
        }

        public String getAlumnoId() {
            return alumnoId;
        }
    }
}
