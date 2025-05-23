package com.example.academtracker.UsuarioBasico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.model.Materia;

import java.util.ArrayList;
import java.util.List;

public class ResultadoPromedioActivity extends AppCompatActivity {

    AnyChartView anyChartView;
    Button btnCompararPromedio;
    String textoBase;
    TextView tvMensajePromedio;
    TextView tvMensajeComparacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_promedio);

        tvMensajePromedio = findViewById(R.id.tvMensajePromedio);
        anyChartView = findViewById(R.id.any_chart_view);
        btnCompararPromedio = findViewById(R.id.btnCompararPromedio);
        tvMensajePromedio = findViewById(R.id.tvMensajePromedio);
        tvMensajeComparacion = findViewById(R.id.tvMensajeComparacion);

        // Recibe la lista de materias y el promedio general
        ArrayList<Materia> materias = (ArrayList<Materia>) getIntent().getSerializableExtra("listaMaterias");
        double promedioGeneral = getIntent().getDoubleExtra("promedioGeneral", 0);

        List<DataEntry> data = new ArrayList<>();
        StringBuilder estados = new StringBuilder();

        textoBase = estados.toString();
        tvMensajePromedio.setText(textoBase);

        if (materias != null && !materias.isEmpty()) {
            for (Materia materia : materias) {
                double promedio = materia.getPromedio();
                data.add(new ValueDataEntry(materia.getNombre(), promedio));

                String estado;
                if (promedio > 70) estado = "Por arriba del promedio";
                else if (promedio == 70) estado = "Justo en el promedio";
                else estado = "Por debajo del promedio";

                estados.append(materia.getNombre())
                        .append(": ")
                        .append(String.format("%.2f", promedio))
                        .append(" - ")
                        .append(estado)
                        .append("\n");
            }

            // Ajuste dinámico de altura del gráfico
            int alturaMinima = 400;
            int alturaPorMateria = 80;
            int nuevaAltura = alturaMinima + Math.max(0, (materias.size() - 4)) * alturaPorMateria;
            anyChartView.getLayoutParams().height = nuevaAltura;
            anyChartView.requestLayout();
        } else {
            estados.append("No hay materias disponibles");
        }

        tvMensajePromedio.setText(estados.toString());

        // Crear gráfico de columnas
        Cartesian cartesian = AnyChart.column();
        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER)
                .anchor(Anchor.CENTER)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title("Promedios por materia");
        cartesian.yScale().minimum(0d).maximum(100d);
        cartesian.yAxis(0).title("Calificación");
        cartesian.xAxis(0).title("Materia");

        cartesian.xAxis(0).labels().rotation(-45);
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        anyChartView.setChart(cartesian);

        // Lógica del botón de comparación
        btnCompararPromedio.setOnClickListener(v -> {
            double promedioAlumno = 0;
            for (Materia materia : materias) {
                promedioAlumno += materia.getPromedio();
            }
            promedioAlumno /= materias.size();

            String mensaje;
            if (promedioAlumno > promedioGeneral) {
                mensaje = "Tu promedio es superior al promedio general de todos los alumnos.";
            } else if (promedioAlumno == promedioGeneral) {
                mensaje = "Tu promedio es igual al promedio general.";
            } else {
                mensaje = "Tu promedio está por debajo del promedio general de todos los alumnos.";
            }

            tvMensajeComparacion.setText(mensaje);
        });

        Toast.makeText(this, "Interactúa con las gráficas para obtener información detallada.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
