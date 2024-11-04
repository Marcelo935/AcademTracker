package com.example.academtracker.UsuarioMedio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.ListaDirectorAlumnosFragment;
import com.example.academtracker.R;
import com.google.android.material.button.MaterialButton;

public class ListaPersonalActivity extends AppCompatActivity {

    MaterialButton regresar,alumnos,profesores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_personal);

        regresar = findViewById(R.id.returnbtn);
        profesores = findViewById(R.id.verprofesores);
        alumnos = findViewById(R.id.veralumnos);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaPersonalActivity.this, DirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListaDirectorAlumnosFragment veralumnos = new ListaDirectorAlumnosFragment();//se hace el objeto tipo "UpdatestudentFragment"
                veralumnos.show(getSupportFragmentManager(),"navegar a fragment");
            }
        });

        profesores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}