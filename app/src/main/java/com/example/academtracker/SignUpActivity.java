package com.example.academtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    TextView nuevoUsuario, bienvenidolabel, continuarlabel;
    ImageView signupImageView;
    TextInputLayout usuarioSignUpTextField, contraseñaTextField;
    MaterialButton inicioSesion;
    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    TextInputLayout nombreTextField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Agregamos referencia a Firestore

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        // Inicializamos Firestore
        db = FirebaseFirestore.getInstance();

        signupImageView = findViewById(R.id.signUpImageView);
        bienvenidolabel = findViewById(R.id.bienvenidoLabel);
        continuarlabel = findViewById(R.id.continuarLabel);
        usuarioSignUpTextField = findViewById(R.id.usuarioSignUpTextField);
        contraseñaTextField = findViewById(R.id.contraseñaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        nuevoUsuario = findViewById(R.id.nuevoUsuario);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.confirmPasswordEditText);
        confirmPasswordEditText= findViewById(R.id.confirmPasswordEditText);
        nombreTextField = findViewById(R.id.nombreTextField);


        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionBack();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    public void validate() {
        String email = emailEditText.getText().toString().trim();
        String nombre = nombreTextField.getEditText().getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validación del email
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo Invalido");
            return;
        } else {
            emailEditText.setError(null);
        }

        // Validación de la contraseña
        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Se necesitan más de 6 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            passwordEditText.setError("Al menos un número");
            return;
        } else {
            passwordEditText.setError(null);
        }

        // Validación de la confirmación de la contraseña
        if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Deben ser iguales");
            return;
        } else {
            confirmPasswordEditText.setError(null);
        }

        // Determinar el rol según el dominio del correo
        int rol = obtenerRolDelCorreo(email); // Devuelve 1 para secretaria, 0 para profesor
        if (rol == -1) {
            emailEditText.setError("El correo debe terminar con gmail.com");
            return;
        }
        // Registrar al usuario
        registrar(email, password, nombre, rol);
    }

    private int obtenerRolDelCorreo(String email) {
        // Lógica para determinar el rol según el correo
        if (email.endsWith("gmail.com")) {
            if (email.contains(".udp")) {
                return 1;  // 1 para secretaria
            } else {
                return 0;  // 0 para profesor
            }
        } else {
            return -1; // Dominio no válido
        }
    }

    public void registrar(String email, String password, String nombre, int rol) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            guardarDatosUsuario(email, nombre, password, rol);
                            Intent intent = new Intent(SignUpActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Fallo en registrarse", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuario(String email, String nombre, String password, int rol) {
        // Creamos un nuevo mapa para los datos del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", nombre);
        usuario.put("password", password);
        usuario.put("rol", rol);  // Asignar rol según el dominio del correo

        // Definir la colección según el rol
        String coleccion = (rol == 1) ? "Secretarias" : "Profesores"; // 1 es secretaria, 0 es profesor

        // Añadimos los datos del usuario a la colección correspondiente en Firestore
        db.collection(coleccion)
                .document(email)  // El documento es el email del usuario
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Datos del usuario guardados exitosamente
                        Log.d(TAG, "Datos del usuario guardados correctamente en Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al guardar los datos del usuario
                        Log.w(TAG, "Error al guardar los datos del usuario en Firestore", e);
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transitionBack();
    }
    public void transitionBack(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(signupImageView, "logoImageTrans");
        pairs[1] = new Pair<View, String>(bienvenidolabel, "textTrans");
        pairs[2] = new Pair<View, String>(continuarlabel,  "iniciaSesionTextTrans");
        pairs[3] = new Pair<View, String>(usuarioSignUpTextField, "emailInputTextTrans");
        pairs[4] = new Pair<View, String>(contraseñaTextField, "passwordInputTextTrans");
        pairs[5] = new Pair<View, String>(inicioSesion, "buttonSignInTrans");
        pairs[6] = new Pair<View, String>(nuevoUsuario, "newUserTrans");


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
            startActivity(intent, options.toBundle());
        }else
        {
            startActivity(intent);
            finish();
        }
    }
}