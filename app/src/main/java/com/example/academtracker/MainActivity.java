package com.example.academtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.UsuarioBasico.AlumnoActivity;
import com.example.academtracker.UsuarioBasico.ProfesorActivity;
import com.example.academtracker.UsuarioMedio.DirectorActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Agrega la instancia de FirebaseFirestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Aqui agrego animaciones
        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inicializa la instancia de FirebaseFirestore

        TextView AcademTrackerTv = findViewById(R.id.AcademTrackerTv);
        final ImageView logoImage = findViewById(R.id.logoImage);

        AcademTrackerTv.setAnimation(animacion2);
        logoImage.setAnimation(animacion1);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                {
                    //Se va a buscar el usuario si es alumno
                    // Obtener referencia a la colección
                    CollectionReference collectionReference = db.collection("Alumnos");
                    // Realizar la consulta
                    collectionReference.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    String Email = "";
                                    String Useremail = "";
                                    String Grado = "";
                                    String Grupo = "";
                                    String Nombre = "";
                                    String Password = "";

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user != null) {
                                        Useremail = user.getEmail();
                                        Toast.makeText(MainActivity.this, Useremail, Toast.LENGTH_SHORT).show();
                                    }
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        Email = document.getString("email");

                                        if(Objects.equals(Email, Useremail))
                                        {
                                            //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                            Grado = document.getString("grado");
                                            Grupo = document.getString("grupo");
                                            Nombre = document.getString("nombre");
                                            Password = document.getString("password");
                                            Intent intent = new Intent(MainActivity.this, AlumnoActivity.class);

                                            intent.putExtra("Email",Email);//Mandamos el valor de la variable al perfil de alumnos
                                            intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                            intent.putExtra("Grupo",Grupo);//Mandamos el valor de la variable al perfil de alumnos
                                            intent.putExtra("Nombre",Nombre);//Mandamos el valor de la variable al perfil de alumnos
                                            intent.putExtra("Password",Password);//Mandamos el valor de la variable al perfil de alumnos

                                            startActivity(intent);
                                            finish();
                                        }
                                    }



                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });

                    //Se va a buscar si el usuario es profesor
                    // Obtener referencia a la colección
                    CollectionReference collectionReference1 = db.collection("Profesores");
                    // Realizar la consulta
                    collectionReference1.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    String Email = "";
                                    String Useremail = " ";

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user != null) {
                                        Useremail = user.getEmail();
                                    }
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        Email = document.getString("email");

                                        if(Objects.equals(Email, Useremail))
                                        {
                                            //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                            Intent intent = new Intent(MainActivity.this, ProfesorActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                    CollectionReference collectionReference2 = db.collection("Director");
                    // Realizar la consulta
                    collectionReference2.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    String Email = "";
                                    String Useremail = " ";

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user != null) {
                                        Useremail = user.getEmail();
                                    }
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        Email = document.getString("Email");

                                        if(Objects.equals(Email, Useremail))
                                        {
                                            //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                            Intent intent = new Intent(MainActivity.this, DirectorActivity.class);
                                            //intent.putExtra("Nombre",nombrealumno);//Mandamos el valor de la variable al perfil de alumnos
                                            //intent.putExtra("Grado",gradoalumno);//Mandamos el valor de la variable al perfil de alumnos
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Ausencia de director", Toast.LENGTH_SHORT).show();
                                }
                            });

                    CollectionReference collectionReference3 = db.collection("Secretarias");
                    // Realizar la consulta
                    collectionReference3.get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    String Email = "";
                                    String Useremail = " ";

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user != null) {
                                        Useremail = user.getEmail();
                                    }
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        Email = document.getString("email");

                                        if(Objects.equals(Email, Useremail))
                                        {
                                            Intent intent = new Intent(MainActivity.this, SecretariasActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Ausencia de secretaria", Toast.LENGTH_SHORT).show();
                                }
                            });

                }else
                {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logoImage, "logoImageTrans");
                    pairs[1] = new Pair<View, String>(logoImage, "textTrans");

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                    }else
                    {
                        startActivity(intent);
                        finish();
                    }
                }

            }
        }, 4000);
    }
}