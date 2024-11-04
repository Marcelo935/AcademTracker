package com.example.academtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academtracker.UsuarioAvanzado.SecretariasActivity;
import com.example.academtracker.UsuarioBasico.AlumnoActivity;
import com.example.academtracker.UsuarioBasico.ProfesorActivity;
import com.example.academtracker.UsuarioMedio.DirectorActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView bienvenidolabel, continuarlabel, nuevoUsuario, olvidasteContraseña;
    ImageView loginImageView;
    TextInputLayout usuarioTextField, contraseñaTextField;
    MaterialButton inicioSesion;
    TextInputEditText emailEditText, passwordEditText;
    String nombrealumno = "";

    String gradoalumno = "";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Agrega la instancia de FirebaseFirestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginImageView = findViewById(R.id.loginImageView);
        bienvenidolabel = findViewById(R.id.bienvenidoLabel);
        continuarlabel = findViewById(R.id.continuarLabel);
        usuarioTextField = findViewById(R.id.usuarioTextField);
        contraseñaTextField = findViewById(R.id.contraseñaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        olvidasteContraseña = findViewById(R.id.olvidasteContra);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inicializa la instancia de FirebaseFirestore

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,  SignUpActivity.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(loginImageView, "logoImageTrans");
                pairs[1] = new Pair<View, String>(bienvenidolabel, "textTrans");
                pairs[2] = new Pair<View, String>(continuarlabel,  "iniciaSesionTextTrans");
                pairs[3] = new Pair<View, String>(usuarioTextField, "emailInputTextTrans");
                pairs[4] = new Pair<View, String>(contraseñaTextField, "passwordInputTextTrans");
                pairs[5] = new Pair<View, String>(inicioSesion, "buttonSignInTrans");
                pairs[6] = new Pair<View, String>(nuevoUsuario, "newUserTrans");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                    startActivity(intent, options.toBundle());
                }else
                {
                    startActivity(intent);
                    finish();
                }
            }
        });

        olvidasteContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    public void validate(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEditText.setError("Correo Invalido");
            return;
        }else
        {
            emailEditText.setError(null);
        }

        if(password.isEmpty() || password.length()<6)
        {
            passwordEditText.setError("Se necesitan mas de 6 caracteres");
        } else if (!Pattern.compile("[0-9]").matcher(password).find())
        {
            passwordEditText.setError("Al menos un numero");
            return;
        }else
        {
            passwordEditText.setError(null);
        }

        IniciarSesion(email,password);
    }

    public void IniciarSesion(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String userId = mAuth.getCurrentUser().getUid(); // Obtén el ID del usuario actual
                            obtenerRolUsuario(userId); // Llama al método para obtener el rol del usuario

                        }else
                        {
                            Toast.makeText(LoginActivity.this, "Credenciales Equivocadas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void obtenerRolUsuario(String userId) {

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
                            Toast.makeText(LoginActivity.this, Useremail, Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(LoginActivity.this, AlumnoActivity.class);

                                intent.putExtra("Email",Email);//Mandamos el valor de la variable al perfil de alumnos
                                intent.putExtra("Grado",Grado);//Mandamos el valor de la variable al perfil de alumnos
                                intent.putExtra("Grupo",Grupo);//Mandamos el valor de la variable al perfil de alumnos
                                intent.putExtra("Nombre",Nombre);//Mandamos el valor de la variable al perfil de alumnos
                                intent.putExtra("Password",Password);//Mandamos el valor de la variable al perfil de alumnos

                                startActivity(intent);
                                finish();
                            }

                            nombrealumno = document.getString("nombre");
                            gradoalumno = document.getString("grado");

                        }



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Ausencia de alumnos", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(LoginActivity.this, ProfesorActivity.class);
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
                        Toast.makeText(LoginActivity.this, "Ausencia de profesores", Toast.LENGTH_SHORT).show();
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
                                Intent intent = new Intent(LoginActivity.this, DirectorActivity.class);
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
                        Toast.makeText(LoginActivity.this, "Ausencia de director", Toast.LENGTH_SHORT).show();
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
                                //Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                                Intent intent = new Intent(LoginActivity.this, SecretariasActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Ausencia de secretarias", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
