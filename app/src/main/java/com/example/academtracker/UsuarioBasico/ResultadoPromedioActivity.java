package com.example.academtracker.UsuarioBasico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;

import com.example.academtracker.R;
import com.example.academtracker.model.Materia;

import java.util.ArrayList;
import java.util.List;

public class ResultadoPromedioActivity extends AppCompatActivity {

    TextView tvMensajePromedio;
    AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_promedio);

        tvMensajePromedio = findViewById(R.id.tvMensajePromedio);
        anyChartView = findViewById(R.id.any_chart_view);

        // Recibe la lista de materias
        ArrayList<Materia> materias = (ArrayList<Materia>) getIntent().getSerializableExtra("listaMaterias");

        List<DataEntry> data = new ArrayList<>();
        StringBuilder estados = new StringBuilder();

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
        } else {
            estados.append("No hay materias disponibles");
        }

        tvMensajePromedio.setText(estados.toString());

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

        anyChartView.setChart(cartesian);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
