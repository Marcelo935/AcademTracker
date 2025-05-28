package com.example.academtracker.UsuarioAvanzado;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.academtracker.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelActivity extends AppCompatActivity {

    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);

        titulo = findViewById(R.id.tvTitulo);

        exportarCalificacionesDeTodos();
    }

    private void exportarCalificacionesDeTodos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Calificaciones");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Alumno");
        header.createCell(1).setCellValue("Materia");
        header.createCell(2).setCellValue("Parcial 1");
        header.createCell(3).setCellValue("Parcial 2");
        header.createCell(4).setCellValue("Parcial 3");

        db.collection("Alumnos").get().addOnSuccessListener(alumnos -> {
            if (alumnos.isEmpty()) {
                Toast.makeText(this, "No hay alumnos para exportar", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Task<QuerySnapshot>> tareasMaterias = new ArrayList<>();
            List<String> correosAlumnos = new ArrayList<>();

            for (QueryDocumentSnapshot alumnoDoc : alumnos) {
                correosAlumnos.add(alumnoDoc.getId());
                Task<QuerySnapshot> tareaMaterias = alumnoDoc.getReference().collection("calificaciones").get();
                tareasMaterias.add(tareaMaterias);
            }

            Tasks.whenAllSuccess(tareasMaterias).addOnSuccessListener(listMaterias -> {
                int rowIndex = 1;

                for (int i = 0; i < listMaterias.size(); i++) {
                    QuerySnapshot materias = (QuerySnapshot) listMaterias.get(i);
                    String email = correosAlumnos.get(i);

                    for (QueryDocumentSnapshot materiaDoc : materias) {
                        Object p1Obj = materiaDoc.get("1er parcial");
                        Object p2Obj = materiaDoc.get("2do parcial");
                        Object p3Obj = materiaDoc.get("3er parcial");

                        double p1 = obtenerCalificacionDesdeString(p1Obj);
                        double p2 = obtenerCalificacionDesdeString(p2Obj);
                        double p3 = obtenerCalificacionDesdeString(p3Obj);

                        // Log para verificar datos
                        Log.d("ExcelExport", "Alumno: " + email + ", Materia: " + materiaDoc.getId() +
                                ", P1: " + p1 + ", P2: " + p2 + ", P3: " + p3);

                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(email);
                        row.createCell(1).setCellValue(materiaDoc.getString("nombre") != null ? materiaDoc.getString("nombre") : materiaDoc.getId());
                        row.createCell(2).setCellValue(p1);
                        row.createCell(3).setCellValue(p2);
                        row.createCell(4).setCellValue(p3);
                    }
                }

                // Guardar archivo
                guardarExcelEnDescargas(workbook);

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al obtener materias", Toast.LENGTH_SHORT).show();
                Log.e("ExcelExport", "Error obteniendo materias", e);
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener alumnos", Toast.LENGTH_SHORT).show();
            Log.e("ExcelExport", "Error obteniendo alumnos", e);
        });
    }



    private double obtenerCalificacionDesdeString(Object valor) {
        if (valor == null) return 0;
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        }
        if (valor instanceof String) {
            try {
                return Double.parseDouble((String) valor);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }


    private void guardarExcelEnDescargas(Workbook workbook) {
        try {
            String fileName = "Calificaciones_Todos.xlsx";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // API 29 o superior: usar MediaStore
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                values.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver resolver = getContentResolver();
                Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri fileUri = resolver.insert(collection, values);

                if (fileUri != null) {
                    OutputStream out = resolver.openOutputStream(fileUri);
                    workbook.write(out);
                    workbook.close();
                    out.close();

                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);

                    Toast.makeText(this, "Archivo guardado correctamente en Descargas", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show();
                }
            } else {
                // API 28 o menor: guardar manualmente en carpeta pública
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs();
                }

                File file = new File(downloadsDir, fileName);
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                workbook.close();
                out.close();

                Toast.makeText(this, "Archivo guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PerfilSecretariasActivity.class);
        startActivity(intent);
        finish();
    }

}
