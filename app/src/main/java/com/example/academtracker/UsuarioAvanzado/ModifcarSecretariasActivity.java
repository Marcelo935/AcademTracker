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
import com.example.academtracker.Secretarias_materias_man;
import com.example.academtracker.UsuarioBasico.ProfesorActivity;
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
                validarMateria(uid,materia.getText().toString(),alumno.getText().toString());
            }
        });
    }

    private void agregarMateria(String idProfesor, String idMateria) {
        // Referencia a la subcolección "materias" dentro de "Profesores"
        CollectionReference addMateriaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias");

        // Agregar materia a la subcolección "materias"
        addMateriaRef.document(idMateria).set(new HashMap<>())
                .addOnSuccessListener(aVoid -> {
                    // Una vez agregada en "materias", agregar la materia a todos los alumnos en "calificaciones"
                    agregarMateriaACalificacionesDeAlumnos(idMateria);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al agregar la materia en 'materias'.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al agregar la materia en 'materias'", e);
                });
    }

    private void agregarMateriaACalificacionesDeAlumnos(String idMateria) {
        // Referencia a la colección "Alumnos"
        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        // Obtener todos los alumnos
        alumnosRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot alumnoSnapshot : queryDocumentSnapshots) {
                        String alumnoId = alumnoSnapshot.getId(); // ID del documento del alumno

                        // Crear documento para la materia en la subcolección "calificaciones"
                        DocumentReference calificacionesRef = alumnosRef
                                .document(alumnoId)
                                .collection("calificaciones")
                                .document(idMateria);

                        Map<String, Object> calificacionesIniciales = new HashMap<>();
                        calificacionesIniciales.put("1er parcial", 0);
                        calificacionesIniciales.put("2do parcial", 0);
                        calificacionesIniciales.put("3er parcial", 0);

                        // Agregar el documento a la subcolección "calificaciones"
                        calificacionesRef.set(calificacionesIniciales)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("ModifcarSecretarias", "Materia " + idMateria + " agregada a calificaciones de " + alumnoId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ModifcarSecretarias", "Error al agregar la materia a calificaciones de " + alumnoId, e);
                                });
                    }
                    Toast.makeText(this, "Materia agregada a calificaciones de todos los alumnos.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener los alumnos.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al obtener los alumnos", e);
                });
    }

    private void agregarAlumno(String idMateria, String nombreAlumno, String idProfesor){
            DocumentReference addStudent = mFirestore.collection("Profesores")
                    .document(idProfesor)
                    .collection("materias")
                    .document(idMateria);
            String idAlumno = addStudent.getId() + "_" + System.currentTimeMillis();
            addStudent.update("alumnos." + idAlumno, nombreAlumno)
                    .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Alumno registrado con éxito.",Toast.LENGTH_SHORT).show();
            })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Fallo al registrar Alumno.",Toast.LENGTH_SHORT).show();
            });
    }
    
    private void validarMateria(String idProfesor, String nombreMateria, String nombreAlumno) {
        DocumentReference materiaRef = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias")
                .document(nombreMateria);

        //Validamos si existe la materia
        materiaRef.get().addOnSuccessListener(DocumentSnapshot -> {
            if(!DocumentSnapshot.exists()){
                agregarMateria(idProfesor,nombreMateria);
                agregarAlumno(nombreMateria,nombreAlumno,idProfesor);
            }else{
                agregarAlumno(nombreMateria,nombreAlumno,idProfesor);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Secretarias_materias_man.class));
        finish();
    }
}