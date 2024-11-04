package com.example.academtracker.UsuarioMedio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.LoginActivity;
import com.example.academtracker.R;
import com.example.academtracker.ListaDirectorAlumnosFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class DirectorActivity extends AppCompatActivity {

    MaterialButton perfil, modificar, actualizar, agregar, imagen, salir, lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director);

        perfil = findViewById(R.id.perfilbtn);
        modificar = findViewById(R.id.modificarbtn);
        actualizar = findViewById(R.id.actualizarbtn);
        agregar = findViewById(R.id.agregarbtn);
        imagen = findViewById(R.id.botonimagen);
        lista = findViewById(R.id.alumnos);
        salir = findViewById(R.id.salirbtn);

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorActivity.this, PerfilDirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorActivity.this, ModificarDirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una nueva instancia del fragmento ListaAlumnosFragment
                ListaDirectorAlumnosFragment listaDirectorAlumnosFragment = new ListaDirectorAlumnosFragment();

                // Obtener el FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Iniciar la transacción del fragmento
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Reemplazar el fragmento actual con el nuevo fragmento
                fragmentTransaction.replace(R.id.fragment_container, listaDirectorAlumnosFragment);

                // Añadir a la pila de retroceso si se desea
                fragmentTransaction.addToBackStack(null);

                // Confirmar la transacción
                fragmentTransaction.commit();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorActivity.this, ActualizarDirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorActivity.this, AgregarDirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DirectorActivity.this, ImagenDirectorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DirectorActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
