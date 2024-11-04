package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Alumnos_Profesores_adapter;
import com.example.academtracker.adapter.Useradapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaprofesoresActivity extends AppCompatActivity {

    MaterialButton regresar;

    ArrayList<String> Listamateria;
    ArrayList<String> Listaprofesor;
    ArrayList<String> Listagrado;

    RecyclerView profesores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesores);

        regresar = findViewById(R.id.returnbtn);

        Listamateria = new ArrayList<String>();
        Listaprofesor = new ArrayList<String>();
        Listagrado = new ArrayList<String>();

        profesores = (RecyclerView) findViewById(R.id.viewprofesores);
        //Los datos se iran de manera vertical con esta instruccion
        profesores.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Acta");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String materia = document.getString("NombreMateria");
                            String profesor = document.getString("NombreProfesor");
                            String grado = document.getString("ClaveMateria");

                                    Listamateria.add(materia);
                                    Listaprofesor.add(profesor);
                                    Listagrado.add(grado);
                        }

                        Alumnos_Profesores_adapter adaptador = new Alumnos_Profesores_adapter(Listamateria,Listaprofesor,Listagrado);
                        profesores.setAdapter(adaptador);

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
                Intent intent = new Intent(ListaprofesoresActivity.this, AlumnoActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
}