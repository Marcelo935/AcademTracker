package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.academtracker.LoginActivity;
import com.example.academtracker.R;
import com.example.academtracker.adapter.Profesores_Asistencias_adapter;
import com.example.academtracker.adapter.Profesores_Calificaciones_adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AsistenciasActivity extends AppCompatActivity {

    MaterialButton regresar;

    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listaasist;
    ArrayList<Integer> ListaasistT;
    ArrayList<String> Listafoto;

    private FirebaseFirestore db;
    RecyclerView asistencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asistencias);
        asistencias = (RecyclerView) findViewById(R.id.asistenciasview);
        asistencias.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listamateria = new ArrayList<String>();
        Listaalumno = new ArrayList<String>();
        Listagrado = new ArrayList<String>();
        Listaasist = new ArrayList<Integer>();
        ListaasistT = new ArrayList<Integer>();
        Listafoto = new ArrayList<String>();

        Intent data = getIntent();
        String Nombre = data.getStringExtra("Nombre");
        String Grado = data.getStringExtra("Grado");
        String Foto = data.getStringExtra("Foto");


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
                            int calif1 = document.getLong("Calificacion").intValue();
                            int asist1 = document.getLong("Asistencia").intValue();
                            int porcentaje1 = document.getLong("TotalAsistencias").intValue();

                            Listamateria.add(materia1);
                            Listagrado.add(Grado);
                            Listaalumno.add(Nombre);
                            Listafoto.add(Foto);
                            Listaasist.add(asist1);
                            ListaasistT.add(porcentaje1);
                            Toast.makeText(AsistenciasActivity.this, Nombre, Toast.LENGTH_SHORT).show();
                        }

                        Profesores_Asistencias_adapter adaptador = new Profesores_Asistencias_adapter(Listamateria,Listaalumno,Listagrado,Listaasist,ListaasistT,Listafoto,(Activity) AsistenciasActivity.this);
                        asistencias.setAdapter(adaptador);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        regresar = findViewById(R.id.returnbtn);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AsistenciasActivity.this, ProfesorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}