package com.example.academtracker.UsuarioBasico;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioAvanzado.PerfilSecretariasActivity;
import com.example.academtracker.adapter.AsistenciasAdapter;
import com.example.academtracker.model.Asistencia;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VerAsistenciasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private AsistenciasAdapter adapter;
    private List<Asistencia> listaAsistencias;

    private EditText etFiltroAlumno, etFiltroMateria;
    private Button btnAplicarFiltros, btnExportarExcel;

    private Uri archivoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_asistencias);

        db = FirebaseFirestore.getInstance();

        etFiltroAlumno = findViewById(R.id.etFiltroAlumno);
        etFiltroMateria = findViewById(R.id.etFiltroMateria);
        btnAplicarFiltros = findViewById(R.id.btnAplicarFiltros);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);

        recyclerView = findViewById(R.id.recyclerAsistencias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaAsistencias = new ArrayList<>();
        adapter = new AsistenciasAdapter(listaAsistencias);
        recyclerView.setAdapter(adapter);

        cargarAsistencias(null, null);

        btnAplicarFiltros.setOnClickListener(v -> {
            String alumno = etFiltroAlumno.getText().toString().trim();
            String materia = etFiltroMateria.getText().toString().trim();

            cargarAsistencias(
                    alumno.isEmpty() ? null : alumno,
                    materia.isEmpty() ? null : materia
            );
            etFiltroAlumno.setText("");
            etFiltroMateria.setText("");
        });

        btnExportarExcel.setOnClickListener(v -> exportarAsistenciasAExcel());
    }

    private void cargarAsistencias(String alumnoID, String materia) {
        Query query = db.collection("Asistencias");

        if (alumnoID != null) {
            query = query.whereEqualTo("alumnoID", alumnoID);
        }
        if (materia != null) {
            query = query.whereEqualTo("materia", materia);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaAsistencias.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Asistencia asistencia = new Asistencia();
                        asistencia.setAlumnoID(doc.getString("alumnoID"));
                        asistencia.setMateria(doc.getString("materia"));
                        asistencia.setPresente(Boolean.TRUE.equals(doc.getBoolean("presente")));
                        asistencia.setFecha(doc.getTimestamp("fecha"));

                        listaAsistencias.add(asistencia);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar asistencias", Toast.LENGTH_SHORT).show();
                });
    }

    private void exportarAsistenciasAExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asistencias");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Alumno ID");
        headerRow.createCell(1).setCellValue("Materia");
        headerRow.createCell(2).setCellValue("Fecha");
        headerRow.createCell(3).setCellValue("Asistió");

        for (int i = 0; i < listaAsistencias.size(); i++) {
            Asistencia asistencia = listaAsistencias.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(asistencia.getAlumnoID());
            row.createCell(1).setCellValue(asistencia.getMateria());

            Timestamp ts = asistencia.getFecha();
            String fechaFormateada = "Sin fecha";
            if (ts != null) {
                Date date = ts.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                fechaFormateada = sdf.format(date);
            }
            row.createCell(2).setCellValue(fechaFormateada);
            row.createCell(3).setCellValue(asistencia.isPresente() ? "Sí" : "No");
        }

        try {
            String nombreArchivo = "Asistencias" + System.currentTimeMillis() + ".xlsx";
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, nombreArchivo);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) {
                    Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show();
                    return;
                }

                archivoUri = uri;
                outputStream = getContentResolver().openOutputStream(uri);
            } else {
                File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File archivo = new File(directorio, nombreArchivo);
                outputStream = new FileOutputStream(archivo);
            }

            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

            Toast.makeText(this, "Archivo guardado en Descargas", Toast.LENGTH_LONG).show();
            abrirArchivoExcel();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirArchivoExcel() {
        if (archivoUri == null) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(archivoUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(intent, "Abrir archivo con:"));
        } catch (Exception e) {
            Toast.makeText(this, "No hay app para abrir el archivo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ProfesorActivity.class);
        startActivity(intent);
        finish();
    }
}
