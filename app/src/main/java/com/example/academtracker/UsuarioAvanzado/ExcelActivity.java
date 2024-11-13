package com.example.academtracker.UsuarioAvanzado;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.academtracker.R;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelActivity extends AppCompatActivity {

    private EditText grade1, grade2, grade3;
    private Button saveButton, calcularcalif;
    private TextView calcular;
    private double calificacion1;
    private double calificacion2;
    private double calificacion3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);

        grade1 = findViewById(R.id.grade1);
        grade2 = findViewById(R.id.grade2);
        grade3 = findViewById(R.id.grade3);
        saveButton = findViewById(R.id.saveButton);
        calcular = findViewById(R.id.txt_calificacion);
        calcularcalif = findViewById(R.id.calculate_btn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToExcel();
            }
        });
    }

    private void saveToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Calificaciones");
        Row row = sheet.createRow(0);

        try {
            if (!grade1.getText().toString().isEmpty()) {
                row.createCell(0).setCellValue(Double.parseDouble(grade1.getText().toString()));
            }
            if (!grade2.getText().toString().isEmpty()) {
                row.createCell(1).setCellValue(Double.parseDouble(grade2.getText().toString()));
            }
            if (!grade3.getText().toString().isEmpty()) {
                row.createCell(2).setCellValue(Double.parseDouble(grade3.getText().toString()));
            }

            File file = new File(getExternalFilesDir(null), "Calificaciones.xlsx");
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            Toast.makeText(this, "Calificaciones guardadas en Excel", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar en Excel", Toast.LENGTH_SHORT).show();
        }

        calcularcalif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularCalificacionNecesaria();
            }
        });
    }

    private void calcularCalificacionNecesaria() {
        try {
            File file = new File(getExternalFilesDir(null), "Calificaciones.xlsx");
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);

            // Asigna cada calificación a las variables sin calcular el promedio
            if (row.getCell(0) != null) {
                calificacion1 = row.getCell(0).getNumericCellValue();
            }
            if (row.getCell(1) != null) {
                calificacion2 = row.getCell(1).getNumericCellValue();
            }
            if (row.getCell(2) != null) {
                calificacion3 = row.getCell(2).getNumericCellValue();
            }

            workbook.close();

            // Muestra un mensaje para verificar que las calificaciones se guardaron
            calcularPromedio(calificacion1,calificacion2,calificacion3);

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer las calificaciones", Toast.LENGTH_SHORT).show();
        }
    }

    public void calcularPromedio(double valor1, double valor2, double valor3) {
        // Verificar si los tres valores son diferentes de 0
        if (valor1 != 0 && valor2 != 0 && valor3 != 0) {
            double promedio = (valor1 + valor2 + valor3) / 3;
            if(promedio > 100.00){
                Toast.makeText(this,"Ya no puedes pasar la materia :( ",Toast.LENGTH_SHORT).show();
            }else{
                calcular.setText(Double.toString(promedio));
            }
        } else {
            // Calcular el valor faltante necesario para obtener un promedio de al menos 70
            double promedioMinimo = 70;
            if (valor1 == 0) {
                valor1 = promedioMinimo * 3 - valor2 - valor3;
                if(valor1 > 100.00){
                    Toast.makeText(this,"Ya no puedes pasar la materia :( ",Toast.LENGTH_SHORT).show();
                }else{
                    calcular.setText(Double.toString(valor1));
                }
            } else if (valor2 == 0) {
                valor2 = promedioMinimo * 3 - valor1 - valor3;
                if(valor2 > 100.00){
                    Toast.makeText(this,"Ya no puedes pasar la materia :( ",Toast.LENGTH_SHORT).show();
                }else{
                    calcular.setText(Double.toString(valor2));
                }
            } else if (valor3 == 0) {
                valor3 = promedioMinimo * 3 - valor1 - valor2;
                if(valor3 > 100.00){
                    Toast.makeText(this,"Ya no puedes pasar la materia :( ",Toast.LENGTH_SHORT).show();
                }else{
                    calcular.setText(Double.toString(valor3));
                }

            }

        }
    }
}