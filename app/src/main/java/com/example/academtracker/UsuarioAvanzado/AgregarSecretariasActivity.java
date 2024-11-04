package com.example.academtracker.UsuarioAvanzado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.academtracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AgregarSecretariasActivity extends AppCompatActivity {

    TextView nuevoUsuario, bienvenidolabel, continuarlabel;
    ImageView signupImageView;
    StorageReference storageReference;
    String storage_path = "Imagenes/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 200;
    private Uri image_url;
    String datafoto = "foto";
    String idd = "";
    ProgressDialog progressDialog;
    TextInputLayout usuarioSignUpTextField, contraseñaTextField;
    MaterialButton inicioSesion, insertarimagenbtn;
    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText, gradoEditText, grupoEditText;
    TextInputLayout nombreTextField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Agregamos referencia a Firestore

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_secretarias);

        mAuth = FirebaseAuth.getInstance();
        // Inicializamos Firestore
        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        signupImageView = findViewById(R.id.signUpImageView);
        continuarlabel = findViewById(R.id.continuarLabel);
        usuarioSignUpTextField = findViewById(R.id.usuarioSignUpTextField);
        contraseñaTextField = findViewById(R.id.contraseñaTextField);
        inicioSesion = findViewById(R.id.inicioSesion);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.confirmPasswordEditText);
        confirmPasswordEditText= findViewById(R.id.confirmPasswordEditText);
        nombreTextField = findViewById(R.id.nombreTextField);
        gradoEditText = findViewById(R.id.gradoEditText);
        grupoEditText = findViewById(R.id.grupoEditText);
        insertarimagenbtn = findViewById(R.id.insertarimagenbtn);


        if(user != null)
        {
            idd = user.getEmail();
            getData(idd);
        }

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(AgregarDirectorActivity.this, DirectorActivity.class);
                //startActivity(intent);
                //finish();

                validate();
            }

        });

        mAuth = FirebaseAuth.getInstance();

        insertarimagenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subirFoto();
            }
        });
    }

    private void subirFoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        startActivityForResult(i,COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == COD_SEL_IMAGE)
            {
                image_url = data.getData();
                actualizarFoto(image_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void actualizarFoto(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        // String rute_storage_foto = storage_path + "" + datafoto + mAuth.getUid() + "" ;
        String rute_storage_foto = storage_path + "" + datafoto + idd + "" ;
        StorageReference reference = storageReference.child(rute_storage_foto);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                Toast.makeText(AgregarSecretariasActivity.this," " , Toast.LENGTH_SHORT).show();
                while(!uriTask.isSuccessful());
                if(uriTask.isSuccessful())
                {
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            Map<String, Object> map = new HashMap<>();
                            map.put("foto", download_uri);

                            db.collection("Alumnos").document(idd).update(map);

                            Toast.makeText(AgregarSecretariasActivity.this, "Foto Actualizada", Toast.LENGTH_SHORT).show();
                            getData(idd);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarSecretariasActivity.this, "ERROR CARGANDO LA FOTO", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getData(String id) {
        db.collection("Alumnos").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String FotoP = documentSnapshot.getString("foto");

                try {
                    if (FotoP != null && !FotoP.isEmpty()) {
                        Picasso.get()
                                .load(FotoP)
                                .resize(350, 350)
                                .into(signupImageView);
                    } else {
                        signupImageView.setImageResource(R.drawable.imagen_2024_02_21_173132115);
                    }
                } catch (Exception e) {
                    Log.e("PicassoError", "Error loading image: " + e.getMessage());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error obteniendo datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void validate(){
        String email = emailEditText.getText().toString().trim();
        String nombre = nombreTextField.getEditText().getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String grade = gradoEditText.getText().toString().trim();
        String group = grupoEditText.getText().toString().trim();


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

        if(!confirmPassword.equals(password))
        {
            confirmPasswordEditText.setError("Deben ser iguales");
            return;
        }else
        {
            registrar(email,password,nombre,grade,group);
        }

    }

    public void registrar(String email, String password, String nombre,String grade,String group){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        idd = email;
                        if(task.isSuccessful()){
                            guardarDatosUsuario(email,nombre,password,grade,group);
                            subirFoto();
                            //Intent intent = new Intent(AgregarDirectorActivity.this, DirectorActivity.class);
                            //startActivity(intent);
                            //finish();
                        }else
                        {
                            Toast.makeText(AgregarSecretariasActivity.this, "Fallo en registrarse", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void guardarDatosUsuario(String email, String nombre, String password,String grade,String group) {
        // Creamos un nuevo mapa para los datos del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", nombre);
        usuario.put("password",password);
        usuario.put("grado",grade);
        usuario.put("grupo",group);
        usuario.put("foto","");
        // Añadimos los datos del usuario a la colección "usuarios" en Firestore
        db.collection("Alumnos")
                .document(email)
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
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }
}