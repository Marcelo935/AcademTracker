package com.example.academtracker.UsuarioMedio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.academtracker.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Firebase;
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
import java.util.Objects;

public class ImagenDirectorActivity extends AppCompatActivity {

    MaterialButton actualizar,eliminar,regresar;
    ImageView imageuser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    String storage_path = "Imagenes/*";
    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 200;
    private Uri image_url;
    String datafoto = "foto";
    String idd;
    ProgressDialog  progressDialog;
    String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen_director);

        progressDialog = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        actualizar = findViewById(R.id.actualizarimg);
        eliminar = findViewById(R.id.eliminarimg);
        regresar = findViewById(R.id.regresar);
        imageuser = findViewById(R.id.imagen);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            id = user.getEmail();
            idd = id;
            getData(id);
        }

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirFoto();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                map.put("foto", "");
                db.collection("Director").document(idd).update(map);
                Toast.makeText(ImagenDirectorActivity.this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                getData(idd);
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImagenDirectorActivity.this, DirectorActivity.class);
                startActivity(intent);
                finish();
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

        String rute_storage_foto = storage_path + "" + datafoto + mAuth.getUid() + "" + idd;
        StorageReference reference = storageReference.child(rute_storage_foto);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                Toast.makeText(ImagenDirectorActivity.this," " + idd, Toast.LENGTH_SHORT).show();
                while(!uriTask.isSuccessful());
                if(uriTask.isSuccessful())
                {
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            Map<String, Object>map = new HashMap<>();
                            map.put("foto", download_uri);

                            db.collection("Director").document(idd).update(map);

                            Toast.makeText(ImagenDirectorActivity.this, "Foto Actualizada", Toast.LENGTH_SHORT).show();
                            getData(idd);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImagenDirectorActivity.this, "ERROR CARGANDO LA FOTO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(String id) {
        db.collection("Director").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String FotoP = documentSnapshot.getString("foto");

                try {
                    if (FotoP != null && !FotoP.isEmpty()) {
                        Picasso.get()
                                .load(FotoP)
                                .resize(350, 350)
                                .into(imageuser);
                    } else {
                        imageuser.setImageResource(R.drawable.noneuser);
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
}