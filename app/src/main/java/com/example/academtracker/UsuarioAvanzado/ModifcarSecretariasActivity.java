package com.example.academtracker.UsuarioAvanzado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Profesores_Editarcalf_adapter;
import com.example.academtracker.adapter.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifcarSecretariasActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private String uid;
    EditText materia,alumno;
    Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcar_secretarias);

        mFirestore = FirebaseFirestore.getInstance();

        uid = getIntent().getStringExtra("uid");

        materia = findViewById(R.id.et_materia);
        alumno = findViewById(R.id.et_alumno);
        guardar = findViewById(R.id.btn_save);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarMateria(uid,materia.getText().toString());
            }
        });

    }

    private void agregarMateria(String idProfesor, String idMateria) {
        CollectionReference addMateriaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias");
        // Usar el ID proporcionado para crear un nuevo documento
        addMateriaRef.document(idMateria);
    }

    private void agregarAlumno(DocumentReference materiaRef, String nombreAlumno){
        materiaRef.update(nombreAlumno, true).addOnSuccessListener(aVoid -> {

        });
    }

    private void validarMateria(String idProfesor, String nombreMateria) {
        DocumentReference materiaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias")
                .document(nombreMateria);

        //Validamos si existe la materia
        materiaRef.get().addOnSuccessListener(DocumentSnapshot -> {
            if(!DocumentSnapshot.exists()){
                agregarMateria(idProfesor,nombreMateria);
            }
        });
    }
}