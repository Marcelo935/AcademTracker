package com.example.academtracker.UsuarioAvanzado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.R;
import com.example.academtracker.SecretariaActualizaAlumnos_man;
import com.google.android.material.button.MaterialButton;

public class ActualizarSecretariasActivity extends AppCompatActivity {

    MaterialButton regresar,profesores,alumnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_secretarias);

        regresar = findViewById(R.id.returnbtn);
        profesores = findViewById(R.id.actualizarprofesores);
        alumnos = findViewById(R.id.actualizaralumnos);

        alumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarSecretariasActivity.this, SecretariaActualizaAlumnos_man.class);
                startActivity(intent);
                finish();

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarSecretariasActivity.this, SecretariasActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}