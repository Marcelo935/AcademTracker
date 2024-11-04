package com.example.academtracker.UsuarioMedio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.DirectorActualizaAlumnos_man;
import com.example.academtracker.R;
import com.google.android.material.button.MaterialButton;

public class ActualizarDirectorActivity extends AppCompatActivity {

    MaterialButton regresar,profesores,alumnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_director);

        regresar = findViewById(R.id.returnbtn);
        profesores = findViewById(R.id.actualizarprofesores);
        alumnos = findViewById(R.id.actualizaralumnos);

        profesores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarDirectorActivity.this, Director_ActualizarProfesorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarDirectorActivity.this, DirectorActualizaAlumnos_man.class);
                startActivity(intent);
                finish();

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActualizarDirectorActivity.this, DirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}