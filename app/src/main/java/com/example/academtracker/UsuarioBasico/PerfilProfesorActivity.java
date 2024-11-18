package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.academtracker.LectorNFCActivity;
import com.example.academtracker.NFCAlumno1Activity;
import com.example.academtracker.NFCAlumno2Activity;
import com.example.academtracker.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.Map;
import java.util.Objects;

public class PerfilProfesorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MaterialButton regresar;
    Button QrGeo, QrAlg, QrHis, NFC;
    String useremail = "", materia = "";
    TextView emailprofesor,nombreprofesor;
    DrawerLayout navDrawer;
    Toolbar toolbar; // Añadir Toolbar aquí
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_profesor);

        regresar = findViewById(R.id.returnbtn);
        nombreprofesor = findViewById(R.id.nombrecompleto);
        emailprofesor = findViewById(R.id.emailprofesor);
        QrGeo = findViewById(R.id.QRbuttonGeo);
        QrAlg = findViewById(R.id.QRbuttonAlg);
        QrHis = findViewById(R.id.QRbuttonHist);
        NFC = findViewById(R.id.NFCbutton);
        navDrawer = findViewById(R.id.navDrawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Establece el Toolbar como ActionBar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        QrGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materia = "Geografia";
                startQRScanner();
            }
        });

        QrHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materia = "Historia";
                startQRScanner();
            }
        });

        QrAlg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materia = "Algebra";
                startQRScanner();
            }
        });

        NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PerfilProfesorActivity.this, LectorNFCActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
        NFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia la actividad LectorNFCActivity
                Intent intent = new Intent(PerfilProfesorActivity.this, LectorNFCActivity.class);
                startActivity(intent);
            }
        });
         */

        // Configuración del toggle para abrir/cerrar el Drawer con el icono en la barra de acción
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navDrawer, R.string.open_drawer, R.string.close_drawer);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // Verifica si el ActionBar está disponible antes de manipularlo
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Habilitar el botón de la barra de navegación
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar el listener del NavigationView
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);//Instanciar una clase para crear un nuevo objeto hijo
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);//Formato de codigo QR
        integrator.setPrompt("Escanee el codigo QR del Alumno");//mensaje
        integrator.setCameraId(0);//se accede a la camara trasera del dispositivo
        integrator.setCaptureActivity(CaptureActivity.class);//se especifica la actividad a inicializar al llamar la funcion
        integrator.setBeepEnabled(true);//un beep al escanear
        integrator.initiateScan();//inicializar el escaneo
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);//la variable result recibe los datos
        if(result != null){
            if(result.getContents() != null) {
                getstudentID(result.getContents(), materia);
            }else{
                Toast.makeText(this, "ESCANEO INVALIDO", Toast.LENGTH_SHORT).show();//en caso de que no reciba nada
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);//gestiona los datos por defecto y evitar errores de nulo
        }
    }

    public void getstudentID(String id, String materia){//Funcion para obtener el id de los documentos de los estudiantes
        db.collection("Alumnos")
                .document(id)
                .collection("calificaciones")
                .document(materia)
                .update("asistencia", FieldValue.increment(1)) // Incrementa el campo "asistencia" en 1
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Asistencia capturada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
         });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int ItemId = item.getItemId();
        if(ItemId == R.id.nav_alfredo){
            Intent intent = new Intent(PerfilProfesorActivity.this, LectorNFCActivity.class);
            startActivity(intent);
            finish();
        }else if(ItemId == R.id.nav_miguel){
            Intent intent = new Intent(PerfilProfesorActivity.this, NFCAlumno1Activity.class);
            startActivity(intent);
            finish();
        }else if(ItemId == R.id.nav_marcelo){
            Intent intent2 = new Intent(PerfilProfesorActivity.this, NFCAlumno2Activity.class);
            startActivity(intent2);
            finish();
        }

        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}