package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Alumnos_Estadisticas_adapter;
import com.example.academtracker.adapter.Useradapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EstadisticasActivity extends AppCompatActivity {

    MaterialButton regresar;

    ArrayList<String> Listamaterias;
    ArrayList<String> Listaclave;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalif;
    ArrayList<Integer> Listaasist;

    ArrayList<Integer> Listaporcentaje;

    RecyclerView estadisticas;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        regresar = findViewById(R.id.returnbtn);
        estadisticas = (RecyclerView) findViewById(R.id.viewestadisticas);
        estadisticas.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listamaterias = new ArrayList<String>();
        Listagrado = new ArrayList<String>();
        Listacalif = new ArrayList<Integer>();
        Listaclave = new ArrayList<String>();
        Listaasist = new ArrayList<Integer>();
        Listaporcentaje = new ArrayList<Integer>();

        Intent data = getIntent();
        String grado = data.getStringExtra("Grado");

        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Acta");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {


                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {

                            String materia1 = document.getString("NombreMateria");
                            String clave1 = document.getString("ClaveMateria");
                            int calif1 = document.getLong("Calificacion").intValue();
                            int asist1 = document.getLong("Asistencia").intValue();
                            int porcentaje1 = document.getLong("TotalAsistencias").intValue();

                            Listamaterias.add(materia1);
                            Listaclave.add(clave1);
                            Listagrado.add(grado);
                            Listacalif.add(calif1);
                            Listaasist.add(asist1);
                            Listaporcentaje.add(porcentaje1);

                        }

                        Alumnos_Estadisticas_adapter adaptador = new Alumnos_Estadisticas_adapter(Listamaterias,Listaclave,Listagrado,Listacalif,Listaasist,Listaporcentaje);
                        estadisticas.setAdapter(adaptador);

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
                Intent intent = new Intent(EstadisticasActivity.this, AlumnoActivity.class);
                startActivity(intent);
                finish();
            }

        });

    }
}