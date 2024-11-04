package com.example.academtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.academtracker.model.Alumnos;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarAlumno_Director extends AppCompatActivity {
    String uid;
    CircleImageView circleImageView;
    AppCompatButton BtnEditFoto, BtnEliminarFoto, BtnCancelar, BtnGuardar;
    EditText ETNombre, ETEmail, ETGrado, ETGrupo;
    FirebaseFirestore mFirestore;
    private static final int COD_SEL_IMAGE = 300;
    private Uri foto_url;
    String storage_path = "Imagenes/";
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alumno_director);
        uid = getIntent().getStringExtra("uid");
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        circleImageView = findViewById(R.id.ActEditarAlumno_foto);
        BtnEditFoto = findViewById(R.id.ActEditarAlumno_btn_editFoto);
        BtnEliminarFoto = findViewById(R.id.ActEditarAlumno_btn_DelFoto);
        BtnCancelar = findViewById(R.id.ActEditarAlumno_btn_cancelar);
        BtnGuardar = findViewById(R.id.ActEditarAlumno_btn_guardar);
        ETNombre = findViewById(R.id.ActEditarAlumno_ET_Nombre);
        ETEmail = findViewById(R.id.ActEditarAlumno_ET_Email);
        ETGrado = findViewById(R.id.ActEditarAlumno_ET_Grado);
        ETGrupo = findViewById(R.id.ActEditarAlumno_ET_Grupo);

        BtnEditFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarFoto();
            }
        });

        BtnEliminarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirestore.collection("Alumnos").document(uid)
                        .update("foto", "");
                Toast.makeText(getApplicationContext(), "Foto eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        BtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        BtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ETNombre.getText().toString()) | TextUtils.isEmpty(ETEmail.getText().toString()) |
                        TextUtils.isEmpty(ETGrado.getText().toString()) | TextUtils.isEmpty(ETGrupo.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Ningun campo puede estar vacio", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern p = Pattern.compile("[^A-Za-zñÑáéíóúÁÉÍÓÚ ]");
                Matcher m = p.matcher(ETNombre.getText().toString());
                if (m.find()) {
                    ETNombre.setError("No puede contener numeros ni caracteres especiales");
                    ETNombre.requestFocus();
                    ETNombre.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            ETNombre.setError(null);
                        }
                    });
                    return;
                }
                ETNombre.setError(null);

                /*Pattern emailPattern = Pattern.compile("^[A-Za-z0-9_.]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$");
                Matcher emailMatcher = emailPattern.matcher(ETEmail.getText().toString());
                if (emailMatcher.find()) {
                    ETEmail.setError("Correo Electronico no valido");
                    ETEmail.requestFocus();
                    ETEmail.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            ETEmail.setError(null);
                        }
                    });
                    return;
                }
                ETEmail.setError(null); */

                //Verificacion para evitar que los correos se repitan
                /*CollectionReference alumnosCollection = FirebaseFirestore.getInstance().collection("Alumnos");
                String Email = ETEmail.getText().toString().trim();
                alumnosCollection.whereEqualTo("email",Email).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if(!queryDocumentSnapshots.isEmpty()){
                                ETEmail.setError("Correo ya existente");
                                ETEmail.requestFocus();
                            }else{
                                ETEmail.setError(null);
                                ValidEmailExist(Email);
                            }
                        });*/
                String Nombre = ETNombre.getText().toString();
                //String Email = ETEmail.getText().toString();
                String Grado = ETGrado.getText().toString();
                String Grupo = ETGrupo.getText().toString();
                ActualizarAlumno(Nombre, Grado, Grupo);
            }

        });

        DocumentReference alumRef = mFirestore.collection("Alumnos").document(uid);
        alumRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("EditarAlumnos", "Listen Failed: " + e);
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Alumnos alumnos = documentSnapshot.toObject(Alumnos.class);
                    if (alumnos != null) {
                        String Nombre = alumnos.getNombre();
                        String Email = alumnos.getEmail();
                        String Grado = alumnos.getGrado();
                        String Grupo = alumnos.getGrupo();
                        String foto = alumnos.getFoto();

                        try {
                            if (!foto.equals("")) {
                                Picasso.get()
                                        .load(foto)
                                        .resize(150, 150)
                                        .into(circleImageView);
                            }
                        } catch (Exception exception) {
                            Log.d("EditarAlumnos", "Error al cargar la imagen");
                        }

                        ETNombre.setText(Nombre);
                        ETEmail.setText(Email);
                        ETGrado.setText(Grado);
                        ETGrupo.setText(Grupo);
                    }
                }
            }
        });
    }

    private void ActualizarAlumno(String Nombre, String Grado, String Grupo) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", Nombre);
        //map.put("email", Email);
        map.put("grado", Grado);
        map.put("grupo", Grupo);

        mFirestore.collection("Alumnos").document(uid).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("EditarAlumno", "Error al actualuizar los datos");
            }
        });
    }

    private void cambiarFoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,COD_SEL_IMAGE);
    }
    private void guardarFotoBD(Uri foto_url) {
        if (uid != null) {
            String route_storage_photo = storage_path + "*" + uid;
            StorageReference reference = storageReference.child(route_storage_photo);

            // Subir la foto a Firebase Storage
            reference.putFile(foto_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Obtener la URL de descarga
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_url = uri.toString();
                            // Actualizar la URL de la foto en Firestore
                            mFirestore.collection("Alumnos").document(uid)
                                    .update("foto", download_url)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Foto Actualizada", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error al actualizar la foto en Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error al subir la foto", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == COD_SEL_IMAGE) {
                foto_url = data.getData();
                guardarFotoBD(foto_url);
            }
            Log.d("image_url", "requestCode - RESULT_OK: " + requestCode + " " + RESULT_OK);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void ValidEmailExist(String Email){
        CollectionReference alumnosCollection = FirebaseFirestore.getInstance().collection("Alumnos");
        alumnosCollection.whereEqualTo("email", Email).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.isEmpty()){

                    }else{
                        ETEmail.setError("Correo ya existente");
                        ETEmail.requestFocus();
                        Toast.makeText(getApplicationContext(), "Correo ya existente", Toast.LENGTH_SHORT);
                    }
                });
    }

        @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DirectorActualizaAlumnos_man.class));
        finish();
    }
}