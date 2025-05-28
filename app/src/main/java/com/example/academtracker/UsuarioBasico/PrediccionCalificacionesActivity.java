package com.example.academtracker.UsuarioBasico;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.TooltipPositionMode;
import com.example.academtracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PrediccionCalificacionesActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String correoAlumno;

    EditText etParcial1, etParcial2, etParcial3;
    Button btnGenerarGrafica;
    AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediccion_calificaciones);

        Toast.makeText(this, "Para obtener mayor información, interactúa con la gráfica", Toast.LENGTH_LONG).show();

        db = FirebaseFirestore.getInstance();
        correoAlumno = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        etParcial1 = findViewById(R.id.et_parcial1);
        etParcial2 = findViewById(R.id.et_parcial2);
        etParcial3 = findViewById(R.id.et_parcial3);
        btnGenerarGrafica = findViewById(R.id.btn_generar_grafica);
        anyChartView = findViewById(R.id.any_chart_view);

        btnGenerarGrafica.setOnClickListener(v -> generarGraficaDesdeInputs());

        Toast.makeText(this, "Ingresa las calificaciones de los parciales", Toast.LENGTH_LONG).show();
    }

    private void generarGraficaDesdeInputs() {
        Double p1 = convertirACalificacion(etParcial1.getText().toString());
        Double p2 = convertirACalificacion(etParcial2.getText().toString());
        Double p3 = convertirACalificacion(etParcial3.getText().toString());

        double sumaActual = 0;
        int registrados = 0;

        if (p1 != null && p1 > 0) { sumaActual += p1; registrados++; }
        if (p2 != null && p2 > 0) { sumaActual += p2; registrados++; }
        if (p3 != null && p3 > 0) { sumaActual += p3; registrados++; }

        double sumaNecesaria = 70 * 3;
        double restante = sumaNecesaria - sumaActual;

        StringBuilder mensaje = new StringBuilder();
        List<DataEntry> datos = new ArrayList<>();

        if (registrados == 3) {
            double promedioFinal = sumaActual / 3;
            mensaje.append("Ya ingresaste todas las calificaciones.\nPromedio final: ")
                    .append(String.format("%.2f", promedioFinal));

            datos.add(new CustomDataEntry("Parcial 1", p1, null));
            datos.add(new CustomDataEntry("Parcial 2", p2, null));
            datos.add(new CustomDataEntry("Parcial 3", p3, null));
        } else {
            double necesarioPorParcial = restante / (3 - registrados);
            boolean imposible = necesarioPorParcial > 100;

            if (imposible) {
                mensaje.append("No es posible aprobar con las calificaciones actuales.");
                necesarioPorParcial = 100; // Para mostrar la barra al máximo
            } else {
                mensaje.append("Para aprobar necesitas:\n");
            }

            if (p1 != null && p1 > 0) {
                datos.add(new CustomDataEntry("Parcial 1", p1, null));
            } else {
                datos.add(new CustomDataEntry("Parcial 1", 0, necesarioPorParcial));
                if (!imposible) mensaje.append("- Parcial 1: ").append(String.format("%.2f", necesarioPorParcial)).append("\n");
            }

            if (p2 != null && p2 > 0) {
                datos.add(new CustomDataEntry("Parcial 2", p2, null));
            } else {
                datos.add(new CustomDataEntry("Parcial 2", 0, necesarioPorParcial));
                if (!imposible) mensaje.append("- Parcial 2: ").append(String.format("%.2f", necesarioPorParcial)).append("\n");
            }

            if (p3 != null && p3 > 0) {
                datos.add(new CustomDataEntry("Parcial 3", p3, null));
            } else {
                datos.add(new CustomDataEntry("Parcial 3", 0, necesarioPorParcial));
                if (!imposible) mensaje.append("- Parcial 3: ").append(String.format("%.2f", necesarioPorParcial)).append("\n");
            }
        }

        mostrarGraficaComparativa(datos);
        Toast.makeText(this, mensaje.toString(), Toast.LENGTH_LONG).show();
    }

    private Double convertirACalificacion(String valor) {
        try {
            if (valor == null || valor.trim().isEmpty()) return null;
            return Double.parseDouble(valor.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static class CustomDataEntry extends DataEntry {
        CustomDataEntry(String x, Number actual, Number proyectado) {
            setValue("x", x);
            setValue("actual", actual);
            if (proyectado != null) {
                setValue("proyectado", proyectado);
            }
        }
    }

    private void mostrarGraficaComparativa(List<DataEntry> datosCombinados) {
        Cartesian cartesian = AnyChart.column();

        Set set = Set.instantiate();
        set.data(datosCombinados);

        Mapping mappingActual = set.mapAs("{ x: 'x', value: 'actual' }");
        Mapping mappingProyectado = set.mapAs("{ x: 'x', value: 'proyectado' }");

        Column columnActual = cartesian.column(mappingActual);
        columnActual.name("Calificación actual")
                .color("#1E88E5")
                .tooltip().format("Actual: {%value}");

        Column columnProyectado = cartesian.column(mappingProyectado);
        columnProyectado.name("Proyección necesaria")
                .color("#F4511E")
                .tooltip().format("Necesario: {%value}");

        cartesian.animation(true);
        cartesian.title("Comparativa de calificaciones y proyecciones");

        double maxValor = 100;
        cartesian.yScale().minimum(0).maximum(maxValor);

        cartesian.yAxis(0).title("Calificación");
        cartesian.xAxis(0).title("Parciales");

        cartesian.tooltip()
                .positionMode(TooltipPositionMode.POINT)
                .anchor(Anchor.CENTER_BOTTOM)
                .position("top")
                .offsetX(0)
                .offsetY(5);

        cartesian.interactivity().hoverMode(HoverMode.SINGLE);
        cartesian.legend(true);

        anyChartView.setChart(cartesian);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
