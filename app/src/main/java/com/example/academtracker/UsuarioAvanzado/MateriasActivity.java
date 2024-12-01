package com.example.academtracker.UsuarioAvanzado;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.academtracker.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MateriasActivity extends AppCompatActivity {

    private LinearLayout linearLayoutDynamicFields;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias);

        linearLayoutDynamicFields = findViewById(R.id.linearLayoutDynamicFields);
        Button buttonAddField = findViewById(R.id.buttonAddField);
        Button buttonSaveAll = findViewById(R.id.buttonSaveAll);

        firestore = FirebaseFirestore.getInstance();

        // Agregar campos dinámicos al presionar el botón "Agregar Materia"
        buttonAddField.setOnClickListener(v -> agregarCampo());

        // Guardar todas las materias al presionar "Guardar Todas las Materias"
        buttonSaveAll.setOnClickListener(v -> guardarMaterias());
    }

    private void agregarCampo() {
        // Crear un contenedor para cada conjunto de campos
        LinearLayout newFieldLayout = new LinearLayout(this);
        newFieldLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Crear los campos para clave, nombre y grado
        EditText editTextClave = new EditText(this);
        editTextClave.setHint("Clave");
        editTextClave.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText editTextNombre = new EditText(this);
        editTextNombre.setHint("Nombre");
        editTextNombre.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText editTextGrado = new EditText(this);
        editTextGrado.setHint("Grado");
        editTextGrado.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Botón para eliminar el conjunto de campos
        Button removeButton = new Button(this);
        removeButton.setText("-");
        removeButton.setOnClickListener(v -> linearLayoutDynamicFields.removeView(newFieldLayout));

        // Agregar los campos y el botón al contenedor
        newFieldLayout.addView(editTextClave);
        newFieldLayout.addView(editTextNombre);
        newFieldLayout.addView(editTextGrado);
        newFieldLayout.addView(removeButton);

        // Agregar el contenedor al layout principal
        linearLayoutDynamicFields.addView(newFieldLayout);
    }

    private void guardarMaterias() {
        int childCount = linearLayoutDynamicFields.getChildCount();

        for (int i = 1; i < childCount; i++) { // Empezamos en 1 porque el botón "Agregar Materia" está incluido en el layout
            View childView = linearLayoutDynamicFields.getChildAt(i);

            if (childView instanceof LinearLayout) {
                LinearLayout fieldLayout = (LinearLayout) childView;

                // Obtener los valores de los campos
                EditText editTextClave = (EditText) fieldLayout.getChildAt(0);
                EditText editTextNombre = (EditText) fieldLayout.getChildAt(1);
                EditText editTextGrado = (EditText) fieldLayout.getChildAt(2);

                String clave = editTextClave.getText().toString();
                String nombre = editTextNombre.getText().toString();
                String grado = editTextGrado.getText().toString();

                if (!clave.isEmpty() && !nombre.isEmpty() && !grado.isEmpty()) {
                    // Verificar si la materia ya existe en Firestore
                    firestore.collection("Materias")
                            .whereEqualTo("clave", clave)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // Si no existe, agregar la materia
                                    Map<String, Object> materia = new HashMap<>();
                                    materia.put("clave", clave);
                                    materia.put("grado", grado);
                                    materia.put("nombre", nombre);

                                    firestore.collection("Materias").add(materia)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(this, "Materia guardada: " + nombre, Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al guardar materia", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // Si ya existe, mostrar un mensaje
                                    Toast.makeText(this, "La materia con clave "+ clave + nombre + " ya existe.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al verificar materia.", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "Completa todos los campos antes de guardar.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }

}
