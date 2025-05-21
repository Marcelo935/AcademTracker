package com.example.academtracker.UsuarioBasico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.enums.LegendLayout;
import com.example.academtracker.LectorNFCActivity;
import com.example.academtracker.NFCAlumno1Activity;
import com.example.academtracker.NFCAlumno2Activity;
import com.example.academtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PerfilProfesorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MaterialButton regresar;
    String useremail = "";
    TextView emailprofesor, nombreprofesor;
    DrawerLayout navDrawer;
    Toolbar toolbar;
    private FirebaseFirestore db;
    private LinearLayout contenedorMaterias;
    MaterialButton btnRegistrarInasistencias;
    AnyChartView anyChartView;

    private String materiaSeleccionada = "";  // guarda materia activa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_profesor);

        regresar = findViewById(R.id.returnbtn);
        nombreprofesor = findViewById(R.id.nombrecompleto);
        emailprofesor = findViewById(R.id.emailprofesor);
        navDrawer = findViewById(R.id.navDrawer);
        toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        contenedorMaterias = findViewById(R.id.contenedorMaterias);
        btnRegistrarInasistencias = findViewById(R.id.btnRegistrarInasistencias);
        anyChartView = findViewById(R.id.any_chart_view);

        btnRegistrarInasistencias.setOnClickListener(v -> registrarInasistencias(materiaSeleccionada));

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, navDrawer, toolbar, R.string.open_drawer, R.string.close_drawer);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            useremail = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();

        db.collection("Profesores").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String getnombre = "";
                    String getemail = "";
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String nombre1 = document.getString("nombre");
                        String email1 = document.getString("email");

                        if (Objects.equals(email1, useremail)) {
                            getnombre = nombre1;
                            getemail = email1;
                            obtenerMateriasProfesor(document.getId());
                        }
                    }
                    nombreprofesor.setText(getnombre);
                    emailprofesor.setText(getemail);
                });

        regresar.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilProfesorActivity.this, ProfesorActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void obtenerMateriasProfesor(String profesorId) {
        db.collection("Profesores")
                .document(profesorId)
                .collection("materias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> materias = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot materiaDoc : materias) {
                        String nombreMateria = materiaDoc.getString("nombre");
                        if (nombreMateria == null || nombreMateria.isEmpty()) {
                            nombreMateria = materiaDoc.getId();
                        }
                        crearBotonMateria(nombreMateria);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener materias", Toast.LENGTH_SHORT).show();
                });
    }

    private void crearBotonMateria(String nombreMateria) {
        MaterialButton botonMateria = new MaterialButton(this);
        botonMateria.setText(nombreMateria);
        botonMateria.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        botonMateria.setBackgroundColor(ContextCompat.getColor(this, R.color.color_boton));

        botonMateria.setOnClickListener(v -> {
            materiaSeleccionada = nombreMateria;
            btnRegistrarInasistencias.setVisibility(MaterialButton.VISIBLE); // Mostrar botón
            mostrarGraficoAsistencia(materiaSeleccionada);
            iniciarEscaneoQR();
        });

        contenedorMaterias.addView(botonMateria);
    }


    private void iniciarEscaneoQR() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanee el código QR del Alumno para " + materiaSeleccionada);
        integrator.setCameraId(0);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String idAlumno = result.getContents();  // El contenido del QR

            if (idAlumno != null && !idAlumno.isEmpty() && materiaSeleccionada != null && !materiaSeleccionada.isEmpty()) {
                registrarAsistencia(idAlumno, materiaSeleccionada);
            } else {
                Toast.makeText(this, "No se encontró el ID del alumno o la materia no está seleccionada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ESCANEO INVÁLIDO", Toast.LENGTH_SHORT).show();
        }
    }

    public void registrarAsistencia(String alumnoID, String materia) {
        // Obtener fecha actual como string (solo día) para evitar duplicados en el mismo día
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("Asistencias")
                .whereEqualTo("alumnoID", alumnoID)
                .whereEqualTo("materia", materia)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean yaRegistradoHoy = false;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Timestamp timestamp = doc.getTimestamp("fecha");
                        if (timestamp != null) {
                            String fechaGuardada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp.toDate());
                            if (fechaGuardada.equals(fechaActual)) {
                                yaRegistradoHoy = true;
                                break;
                            }
                        }
                    }

                    if (!yaRegistradoHoy) {
                        Map<String, Object> asistenciaData = new HashMap<>();
                        asistenciaData.put("alumnoID", alumnoID);
                        asistenciaData.put("materia", materia);
                        asistenciaData.put("fecha", FieldValue.serverTimestamp());
                        asistenciaData.put("presente", true);
                        asistenciaData.put("profesorEmail", useremail);

                        db.collection("Asistencias")
                                .add(asistenciaData)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Asistencia registrada", Toast.LENGTH_SHORT).show();
                                    mostrarGraficoAsistencia(materia); // Actualizar gráfico
                                })
                                .addOnFailureListener(exception -> {
                                    Toast.makeText(this, "Error al registrar asistencia", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Ya se registró asistencia hoy para este alumno", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registrarInasistencias(String materia) {
        if (materia == null || materia.isEmpty()) {
            Toast.makeText(this, "Primero selecciona una materia", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        db.collection("Alumnos").get().addOnSuccessListener(alumnosSnapshot -> {
            for (DocumentSnapshot alumnoDoc : alumnosSnapshot.getDocuments()) {
                String alumnoID = alumnoDoc.getId();

                db.collection("Asistencias")
                        .whereEqualTo("alumnoID", alumnoID)
                        .whereEqualTo("materia", materia)
                        .get()
                        .addOnSuccessListener(asistenciaSnapshot -> {
                            boolean yaRegistradoHoy = false;

                            for (DocumentSnapshot doc : asistenciaSnapshot.getDocuments()) {
                                Timestamp timestamp = doc.getTimestamp("fecha");
                                if (timestamp != null) {
                                    String fechaGuardada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timestamp.toDate());
                                    if (fechaGuardada.equals(fechaActual)) {
                                        yaRegistradoHoy = true;
                                        break;
                                    }
                                }
                            }

                            if (!yaRegistradoHoy) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("alumnoID", alumnoID);
                                data.put("materia", materia);
                                data.put("profesorEmail", useremail);
                                data.put("fecha", FieldValue.serverTimestamp());
                                data.put("presente", false); // inasistencia

                                db.collection("Asistencias").add(data)
                                        .addOnSuccessListener(aVoid -> mostrarGraficoAsistencia(materia))
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al registrar inasistencia", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
            }

            Toast.makeText(this, "Inasistencias registradas para alumnos sin escaneo", Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarGraficoAsistencia(String materia) {
        db.collection("Asistencias")
                .whereEqualTo("materia", materia)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int conteoAsistencias = 0;
                    int conteoInasistencias = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Boolean presente = doc.getBoolean("presente");
                        if (presente != null && presente) {
                            conteoAsistencias++;
                        } else {
                            conteoInasistencias++;
                        }
                    }

                    mostrarGraficoPie(materia, conteoAsistencias, conteoInasistencias); // pasa la materia aquí
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos para el gráfico", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarGraficoPie(String materia, int asistencias, int inasistencias) {
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Asistencias", asistencias));
        data.add(new ValueDataEntry("Inasistencias", inasistencias));

        com.anychart.charts.Pie pie = AnyChart.pie();
        pie.data(data);

        pie.title("Distribución de asistencia - " + materia);
        pie.labels().position("outside");

        pie.legend()
                .title().enabled(true);
        pie.legend().title().text("Estado");
        pie.legend().position("center-bottom");
        pie.legend().itemsLayout(LegendLayout.HORIZONTAL);
        pie.legend().align("center");

        anyChartView.setChart(pie);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_alfredo) {
            startActivity(new Intent(this, LectorNFCActivity.class));
        } else if (itemId == R.id.nav_miguel) {
            startActivity(new Intent(this, NFCAlumno1Activity.class));
        } else if (itemId == R.id.nav_marcelo) {
            startActivity(new Intent(this, NFCAlumno2Activity.class));
        }
        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}