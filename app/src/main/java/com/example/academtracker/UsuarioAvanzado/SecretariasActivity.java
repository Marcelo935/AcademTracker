package com.example.academtracker.UsuarioAvanzado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.ListaSecretariasAlumnosFragment;
import com.example.academtracker.LoginActivity;
import com.example.academtracker.R;
import com.example.academtracker.Secretarias_materias_man;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class SecretariasActivity extends AppCompatActivity {

    MaterialButton perfil, modificar, actualizar, agregar, salir, lista, pagos, materias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretarias);

        perfil = findViewById(R.id.perfilbtn);
        pagos = findViewById(R.id.pagosbtn);
        modificar = findViewById(R.id.modificarbtn);
        actualizar = findViewById(R.id.actualizarbtn);
        materias = findViewById(R.id.materiasbtn);
        agregar = findViewById(R.id.agregarbtn);
        lista = findViewById(R.id.alumnos);
        salir = findViewById(R.id.salirbtn);

        materias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, MateriasActivity.class);
                startActivity(intent);
                finish();
            }
        });

        pagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, RegistropagosActivity.class);
                startActivity(intent);
                finish();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, PerfilSecretariasActivity.class);
                startActivity(intent);
                finish();
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, AgregarSecretariasActivity.class);
                startActivity(intent);
                finish();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, ActualizarSecretariasActivity.class);
                startActivity(intent);
                finish();
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecretariasActivity.this, Secretarias_materias_man.class);
                startActivity(intent);
                finish();
            }
        });

        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una nueva instancia del fragmento ListaAlumnosFragment
                ListaSecretariasAlumnosFragment listasecretariasAlumnosFragment = new ListaSecretariasAlumnosFragment();

                // Obtener el FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();

                // Iniciar la transacción del fragmento
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Reemplazar el fragmento actual con el nuevo fragmento
                fragmentTransaction.replace(R.id.fragment_container, listasecretariasAlumnosFragment);

                // Añadir a la pila de retroceso si se desea
                fragmentTransaction.addToBackStack(null);

                // Confirmar la transacción
                fragmentTransaction.commit();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SecretariasActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}