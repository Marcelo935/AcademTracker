package com.example.academtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


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
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    TextView emailTextView;
    MaterialButton logoutButton;
    RecyclerView mRecycler;

    FirebaseFirestore mFirestore;

    TextView nombre,password,email,todo;

    String useremail = "";

    ArrayList<String> Listanombre;
    ArrayList<String> Listaemail;
    ArrayList<String> Listapassword;

    RecyclerView users;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        emailTextView = findViewById(R.id.emailTextView);
        logoutButton = findViewById(R.id.logoutButton);

        nombre = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.correo);
        todo = findViewById(R.id.todo);

        int rol_user =  getIntent().getIntExtra("administrador",0);

        users = (RecyclerView) findViewById(R.id.usuarios);
        //Los datos se iran de manera vertical con esta instruccion
        users.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Listanombre = new ArrayList<String>();
        Listaemail = new ArrayList<String>();
        Listapassword = new ArrayList<String>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            emailTextView.setText(user.getEmail());
            useremail = user.getEmail();//guardar lo que retorna lo del usuario actual
        }
        db = FirebaseFirestore.getInstance();
        //toma la referencia de la coleccion usuarios
        CollectionReference collectionReference = db.collection("usuarios");
        // se realiza la consulta
        collectionReference.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                                String getnombre = "";
                                String getemail = "";
                                String getpassword = "";
                                String gettodo = "";
                                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments())
                                {
                                    String nombre1 = document.getString("nombre");
                                    String email1 = document.getString("email");
                                    String password1 = document.getString("password");

                                    if(rol_user == 1)
                                    {
                                        getnombre += "Nombre: " + nombre1 + "\n";
                                        getemail += "Email: " + email1 + "\n";
                                        getpassword += "Password: " + password1 + "\n";
                                        gettodo += "Nombre: " + nombre1 + " Email: " + email1 + " Pasword: " + password1 + "\n";
                                        Listanombre.add(nombre1);
                                        Listaemail.add(email1);
                                        Listapassword.add(password1);

                                    }else
                                    {
                                        //compara el email que sea igual al que se identifico y asi retorne los datos del usuario que esta autenticado
                                        if(Objects.equals(email1,useremail))
                                        {
                                            getnombre = "Nombre: " + nombre1;
                                            getemail = "Email: " + email1;
                                            getpassword = "Password: " + password1;
                                            gettodo = "Nombre: " + nombre1 + " Email: " + email1 + " Pasword: " + password1;
                                            Listanombre.add(nombre1);
                                            Listaemail.add(email1);
                                            Listapassword.add(password1);
                                        }
                                    }

                                }
                                /*
                                nombre.setText(getnombre);
                                email.setText(getemail);
                                password.setText(getpassword);
                                todo.setText(gettodo);
                                 */

                                Useradapter adaptador = new Useradapter(Listanombre,Listaemail,Listapassword);
                                users.setAdapter(adaptador);

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}