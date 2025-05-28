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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        TextView AcademTrackerTv = findViewById(R.id.AcademTrackerTv);
        final ImageView logoImage = findViewById(R.id.logoImage);

        AcademTrackerTv.setAnimation(animacion2);
        logoImage.setAnimation(animacion1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userEmail = user.getEmail();

                    // Obtener y guardar token FCM
                    FirebaseMessaging.getInstance().getToken()
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String token) {
                                    if (userEmail != null) {
                                        Map<String, Object> tokenMap = new HashMap<>();
                                        tokenMap.put("token", token);

                                        db.collection("Tokens")
                                                .document(userEmail)
                                                .set(tokenMap)
                                                .addOnSuccessListener(unused -> {
                                                    // Token guardado correctamente
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(MainActivity.this, "Error al guardar token", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }
                            });

                    // Verifica si es Alumno
                    db.collection("Alumnos").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        String email = document.getString("email");
                                        if (Objects.equals(email, userEmail)) {
                                            String grado = document.getString("grado");
                                            String grupo = document.getString("grupo");
                                            String nombre = document.getString("nombre");
                                            String password = document.getString("password");

                                            Intent intent = new Intent(MainActivity.this, AlumnoActivity.class);
                                            intent.putExtra("Email", email);
                                            intent.putExtra("Grado", grado);
                                            intent.putExtra("Grupo", grupo);
                                            intent.putExtra("Nombre", nombre);
                                            intent.putExtra("Password", password);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }
                                    }
                                }
                            });

                    // Verifica si es Profesor
                    db.collection("Profesores").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        String email = document.getString("email");
                                        if (Objects.equals(email, userEmail)) {
                                            startActivity(new Intent(MainActivity.this, ProfesorActivity.class));
                                            finish();
                                            return;
                                        }
                                    }
                                }
                            });

                    // Verifica si es Director
                    db.collection("Director").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        String email = document.getString("Email");
                                        if (Objects.equals(email, userEmail)) {
                                            startActivity(new Intent(MainActivity.this, DirectorActivity.class));
                                            finish();
                                            return;
                                        }
                                    }
                                }
                            });

                    // Verifica si es Secretaria
                    db.collection("Secretarias").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(@NonNull QuerySnapshot querySnapshot) {
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        String email = document.getString("email");
                                        if (Objects.equals(email, userEmail)) {
                                            startActivity(new Intent(MainActivity.this, SecretariasActivity.class));
                                            finish();
                                            return;
                                        }
                                    }
                                }
                            });
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(logoImage, "logoImageTrans");
                    pairs[1] = new Pair<View, String>(logoImage, "textTrans");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 4000);
    }
}
