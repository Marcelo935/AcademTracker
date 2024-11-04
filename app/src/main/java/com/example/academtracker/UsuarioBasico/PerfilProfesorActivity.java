package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.academtracker.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class PerfilProfesorActivity extends AppCompatActivity {

    MaterialButton regresar;

    String useremail = "";

    TextView emailprofesor,nombreprofesor;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_profesor);

        regresar = findViewById(R.id.returnbtn);
        nombreprofesor = findViewById(R.id.nombrecompleto);
        emailprofesor = findViewById(R.id.emailprofesor);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            useremail = user.getEmail();//guardar lo que retorna lo del usuario actual
        }

        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("Profesores");
        // se realiza la consulta
        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        String getnombre = "";
                        String getemail = "";
                        for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                        {
                            String nombre1 = document.getString("nombre");
                            String email1 = document.getString("email");

                            //compara el email que sea igual al que se identifico y asi retorne los datos del usuario que esta autenticado
                            if(Objects.equals(email1,useremail))
                            {
                                getnombre = "Nombre: " + nombre1;
                                getemail = "Email: " + email1;
                            }

                        }
                        nombreprofesor.setText(getnombre);
                        emailprofesor.setText(getemail);
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
                Intent intent = new Intent(PerfilProfesorActivity.this, ProfesorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}