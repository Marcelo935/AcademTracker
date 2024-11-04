package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Useradapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ListaMateriasActivity extends AppCompatActivity {

    MaterialButton regresar;

    ArrayList<String> Listaclave;
    ArrayList<String> Listagrado;
    ArrayList<String> Listanombre;

    RecyclerView Materias;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_materias);

       regresar = findViewById(R.id.returnbtn);
       Materias = (RecyclerView) findViewById(R.id.viewmaterias);
       Materias.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listaclave = new ArrayList<String>();
        Listagrado = new ArrayList<String>();
        Listanombre = new ArrayList<String>();

        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Materias");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String clave1 = document.getString("clave");
                            String grado1 = document.getString("grado");
                            String nombre1 = document.getString("nombre");

                            Listaclave.add(clave1);
                            Listagrado.add(grado1);
                            Listanombre.add(nombre1);

                        }

                        Useradapter adaptador = new Useradapter(Listanombre,Listaclave,Listagrado);
                        Materias.setAdapter(adaptador);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaMateriasActivity.this, ProfesorActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
}