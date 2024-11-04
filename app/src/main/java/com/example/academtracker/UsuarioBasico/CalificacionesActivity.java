package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Profesores_Calificaciones_adapter;
import com.example.academtracker.adapter.Useradapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CalificacionesActivity extends AppCompatActivity {

    MaterialButton regresar;

    MaterialButton editar;

    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalificacion;

    RecyclerView calificaciones;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificaciones);

        regresar = findViewById(R.id.returnbtn);
        editar = findViewById(R.id.buttonedit);
        calificaciones = (RecyclerView) findViewById(R.id.calificaciones);
        calificaciones.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listamateria = new ArrayList<String>();
        Listaalumno = new ArrayList<String>();
        Listagrado = new ArrayList<String>();
        Listacalificacion = new ArrayList<Integer>();

        Intent data = getIntent();
        String name = data.getStringExtra("Nombre");
        String grade = data.getStringExtra("Grado");

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

                            Listamateria.add(materia1);
                            Listagrado.add(grade);
                            Listaalumno.add(name);
                            Listacalificacion.add(calif1);

                        }

                        Profesores_Calificaciones_adapter adaptador = new Profesores_Calificaciones_adapter(Listamateria,Listaalumno,Listagrado,Listacalificacion);
                        calificaciones.setAdapter(adaptador);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectionReference collectionReference = db.collection("Alumnos");
                collectionReference.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@androidx.annotation.NonNull QuerySnapshot querySnapshot) {

                                String Grado = "";
                                String Nombre = "";
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                                    //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                    Grado = document.getString("grado");
                                    Nombre = document.getString("nombre");

                                    Intent intent = new Intent(CalificacionesActivity.this, EditarActivity.class);
                                    intent.putExtra("Nombre",Nombre);//Mandamos el valor de la variable al perfil de alumnos
                                    intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                            }
                        });

                /*
                // Inicializar Firestore
                db = FirebaseFirestore.getInstance();
                // Obtener referencia a la colección
                //CollectionReference collectionReference = db.collection("UsersNew");
                CollectionReference collectionReference = db.collection("Acta");

                // Modificar un campo de una tabla en la firebase
                int cal = 0;

                String IDdocument = "Historia";
                Map<String, Object> modificar = new HashMap<>();
                //modificar.put("email", email);
                modificar.put("Calificacion", cal);
                //Toast.makeText(Alumno_Perfil.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                //Toast.makeText(Alumno_Perfil.this, "Error al actualizar el document", Toast.LENGTH_SHORT).show();

                db.collection("Acta")
                        .document(IDdocument)
                        .update(modificar)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos del usuario actualizados exitosamente
                                //Log.d(TAG, "Datos del usuario actualizados correctamente en Firestore");
                                Toast.makeText(CalificacionesActivity.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al actualizar los datos del usuario
                                //Log.w(TAG, "Error al actualizar los datos del usuario en Firestore", e);
                                Toast.makeText(CalificacionesActivity.this, "Error al actualizar el document", Toast.LENGTH_SHORT).show();
                            }
                        });


                 */
            }

        });


        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalificacionesActivity.this, ProfesorActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }
}