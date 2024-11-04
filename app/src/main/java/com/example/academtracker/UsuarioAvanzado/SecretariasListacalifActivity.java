package com.example.academtracker.UsuarioAvanzado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Lista_Alumnos_Calificaciones_adapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SecretariasListacalifActivity extends AppCompatActivity {

    RecyclerView alumngrades;
    ArrayList<String> listaEmail;
    ArrayList<String> listaGrado;
    ArrayList<String> listaGrupo;
    ArrayList<String> listaNombre;
    ArrayList<String> listaImagenes;
    Lista_Alumnos_Calificaciones_adapter adaptador;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretarias_listacalif);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar el RecyclerView
        alumngrades = findViewById(R.id.alumnos);
        alumngrades.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar listas
        listaEmail = new ArrayList<>();
        listaGrado = new ArrayList<>();
        listaGrupo = new ArrayList<>();
        listaNombre = new ArrayList<>();
        listaImagenes = new ArrayList<>();

        // Obtener datos de Firestore
        cargarAlumnos();
    }

    private void cargarAlumnos() {
        // Obtener referencia a la colección "Alumnos"
        CollectionReference collectionReference = db.collection("Alumnos");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    // Obtener los datos del documento
                    String email = document.getString("email");
                    String grado = document.getString("grado");
                    String grupo = document.getString("grupo");
                    String nombre = document.getString("nombre");
                    String imageUrl = document.getString("foto");

                    // Agregar los datos a las listas
                    listaEmail.add(email);
                    listaGrado.add(grado);
                    listaGrupo.add(grupo);
                    listaNombre.add(nombre);
                    listaImagenes.add(imageUrl);
                }

                // Crear el adaptador y asignarlo al RecyclerView
                adaptador = new Lista_Alumnos_Calificaciones_adapter(listaEmail, listaGrado, listaGrupo, listaNombre, listaImagenes, SecretariasListacalifActivity.this);
                alumngrades.setAdapter(adaptador);
            }
        });
    }
}
