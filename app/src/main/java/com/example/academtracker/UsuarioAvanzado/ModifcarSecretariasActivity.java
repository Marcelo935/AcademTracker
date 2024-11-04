package com.example.academtracker.UsuarioAvanzado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.Profesores_Editarcalf_adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModifcarSecretariasActivity extends AppCompatActivity {

    MaterialButton regresar,guardar;
    RecyclerView edicion;
    Profesores_Editarcalf_adapter adaptador;
    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalif;
    String dato = "";
    String grado1="";
    String nombre1="";
    int calificacion = 0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcar_secretarias);

        regresar = findViewById(R.id.returnbtn);
        guardar = findViewById(R.id.guardarsec);

        edicion = (RecyclerView) findViewById(R.id.modifcarview);
        edicion.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listamateria = new ArrayList<String>();
        Listaalumno = new ArrayList<String>();
        Listagrado = new ArrayList<String>();
        Listacalif = new ArrayList<Integer>();

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Alumnos");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@androidx.annotation.NonNull QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            nombre1 = document.getString("nombre");
                            grado1 = document.getString("grado");
                        }
                    }
                });
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference1 = db.collection("Acta");
        // se realiza la consulta
        collectionReference1.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@androidx.annotation.NonNull QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String materia1 = document.getString("NombreMateria");
                            int calif1 = document.getLong("Calificacion").intValue();

                            Listamateria.add(materia1);
                            Listagrado.add(grado1);
                            Listaalumno.add(nombre1);
                            Listacalif.add(calif1);
                        }
                        adaptador = new Profesores_Editarcalf_adapter(Listamateria,Listaalumno,Listagrado,Listacalif);
                        edicion.setAdapter(adaptador);
                    }
                });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String [] datos = adaptador.getListaedit();
                for(int i = 0; i< datos.length; i++)
                {
                    dato += "dato " + i + ": " + datos[i];
                    calificacion = Integer.parseInt(datos[i]);
                }

                // Inicializar Firestore
                db = FirebaseFirestore.getInstance();

                // Modificar un campo de una tabla en la firebase
                int cal = Integer.parseInt(datos[0]);
                int cal2 = Integer.parseInt(datos[1]);
                int cal3 = Integer.parseInt(datos[2]);

                String materia1 = "Algebra";
                String materia2 = "Geografia";
                String materia3 = "Historia";
                Map<String, Object> modificar = new HashMap<>();
                Map<String, Object> modificar2 = new HashMap<>();
                Map<String, Object> modificar3 = new HashMap<>();
                //modificar.put("email", email);
                modificar.put("Calificacion", cal);
                modificar2.put("Calificacion", cal2);
                modificar3.put("Calificacion", cal3);
                //Toast.makeText(Alumno_Perfil.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                //Toast.makeText(Alumno_Perfil.this, "Error al actualizar el document", Toast.LENGTH_SHORT).show();

                db.collection("Acta")
                        .document(materia1)
                        .update(modificar)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos del usuario actualizados exitosamente
                                //Log.d(TAG, "Datos del usuario actualizados correctamente en Firestore");
                                Toast.makeText(ModifcarSecretariasActivity.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al actualizar los datos del usuario
                                //Log.w(TAG, "Error al actualizar los datos del usuario en Firestore", e);
                                Toast.makeText(ModifcarSecretariasActivity.this, "Error al actualizar el document", Toast.LENGTH_SHORT).show();
                            }
                        });

                db.collection("Acta")
                        .document(materia2)
                        .update(modificar2)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos del usuario actualizados exitosamente
                                //Log.d(TAG, "Datos del usuario actualizados correctamente en Firestore");
                                Toast.makeText(ModifcarSecretariasActivity.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        });

                db.collection("Acta")
                        .document(materia3)
                        .update(modificar3)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos del usuario actualizados exitosamente
                                //Log.d(TAG, "Datos del usuario actualizados correctamente en Firestore");
                                Toast.makeText(ModifcarSecretariasActivity.this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifcarSecretariasActivity.this, SecretariasActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}