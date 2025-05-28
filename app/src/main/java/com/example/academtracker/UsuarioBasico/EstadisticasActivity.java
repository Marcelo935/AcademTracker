package com.example.academtracker.UsuarioBasico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.example.academtracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EstadisticasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String alumnoEmail;
    private TextView tvPrediccion, tvRecomendacion;
    private LinearLayout containerGraficas;
    private ScrollView scrollViewGraficas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        tvPrediccion = findViewById(R.id.tvPrediccion);
        tvRecomendacion = findViewById(R.id.tvRecomendacion);
        containerGraficas = findViewById(R.id.containerGraficas);
        scrollViewGraficas = findViewById(R.id.scrollViewGraf);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            alumnoEmail = currentUser.getEmail();
            cargarTodasLasMateriasYPredecir();
        } else {
            tvPrediccion.setText("No hay usuario autenticado.");
            tvRecomendacion.setText("");
        }
    }

    private void cargarTodasLasMateriasYPredecir() {
        CollectionReference materiasRef = db.collection("Alumnos")
                .document(alumnoEmail)
                .collection("calificaciones");

        materiasRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                tvPrediccion.setText("No hay materias registradas.");
                tvRecomendacion.setText("");
                return;
            }

            StringBuilder sbPredicciones = new StringBuilder();
            StringBuilder sbRecomendaciones = new StringBuilder();

            containerGraficas.removeAllViews(); // limpiar gráficas anteriores

            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                String materia = doc.getId();

                Double p1 = parseDoubleFromString(doc.getString("1er parcial"));
                Double p2 = parseDoubleFromString(doc.getString("2do parcial"));
                Double p3 = parseDoubleFromString(doc.getString("3er parcial"));

                if (p1 != null && p2 != null && p3 != null) {
                    double[] x = {1, 2, 3};
                    double[] y = {p1, p2, p3};
                    double[] coef = calcularRegresionLineal(x, y);

                    double a = coef[0];
                    double b = coef[1];

                    sbPredicciones.append("Materia: ").append(materia).append("\n");
                    sbPredicciones.append("Predicción para examen extraordinario:\n");

                    double predExtra = a + b * 4;
                    predExtra = Math.min(100, Math.max(0, predExtra));
                    sbPredicciones.append("  Calificación estimada: ")
                            .append(String.format("%.2f", predExtra)).append("\n");

                    sbRecomendaciones.append("Materia: ").append(materia)
                            .append(" - Recomendación: ").append(generarRecomendacion(predExtra))
                            .append("\n");

                    String patron = analizarPatron(p1, p2, p3);
                    sbRecomendaciones.append("  Tendencia: ").append(patron).append("\n\n");

                    // Preparar listas para graficar
                    List<Double> calificacionesMateria = new ArrayList<>();
                    calificacionesMateria.add(p1);
                    calificacionesMateria.add(p2);
                    calificacionesMateria.add(p3);

                    List<Double> prediccionesMateria = new ArrayList<>();
                    prediccionesMateria.add(predExtra);

                    // Crear y agregar gráfica dinámica
                    AnyChartView chartView = new AnyChartView(this);
                    mostrarGrafica(chartView, materia, calificacionesMateria, prediccionesMateria);
                    containerGraficas.addView(chartView,
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    600 // altura fija para la gráfica
                            )
                    );

                } else {
                    sbPredicciones.append("Materia: ").append(materia)
                            .append(" - No hay suficientes datos para predicción.\n\n");
                }
            }

            tvPrediccion.setText(sbPredicciones.toString());
            tvRecomendacion.setText(sbRecomendaciones.toString());

            // Opcional: hacer scroll hacia arriba para mostrar los textos
            scrollViewGraficas.post(() -> scrollViewGraficas.fullScroll(ScrollView.FOCUS_UP));

        }).addOnFailureListener(e -> {
            tvPrediccion.setText("Error al cargar materias: " + e.getMessage());
            tvRecomendacion.setText("");
        });
    }

    private Double parseDoubleFromString(String str) {
        try {
            if (str != null) {
                return Double.parseDouble(str);
            }
        } catch (NumberFormatException e) {
            // Ignorar formato incorrecto
        }
        return null;
    }

    private double[] calcularRegresionLineal(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        return new double[]{a, b};
    }

    private String generarRecomendacion(double prediccion) {
        if (prediccion >= 90) {
            return "Excelente desempeño, sigue así.";
        } else if (prediccion >= 75) {
            return "Buen desempeño, pero puede mejorar.";
        } else if (prediccion >= 60) {
            return "Regular, estudia más para mejorar.";
        } else {
            return "Necesita apoyo urgente para aprobar.";
        }
    }

    private String analizarPatron(double p1, double p2, double p3) {
        if (p1 < p2 && p2 < p3) {
            return "Mejora constante 📈";
        } else if (p1 > p2 && p2 > p3) {
            return "Deterioro constante 📉";
        } else if (p1 == p2 && p2 == p3) {
            return "Rendimiento estable ➖";
        } else {
            return "Patrón variable ⚖️";
        }
    }

    private void mostrarGrafica(AnyChartView anyChartView, String materia, List<Double> calificaciones, List<Double> predicciones) {
        Cartesian cartesian = AnyChart.line();

        List<DataEntry> data = new ArrayList<>();

        // Calificaciones reales
        for (int i = 0; i < calificaciones.size(); i++) {
            data.add(new ValueDataEntry("Parcial " + (i + 1), calificaciones.get(i)));
        }

        // Predicciones (solo 1 para examen extraordinario)
        for (int i = 0; i < predicciones.size(); i++) {
            data.add(new ValueDataEntry("Extraordinario", predicciones.get(i)));
        }

        Line line = cartesian.line(data);
        line.name(materia + " (Calif. y Predicción)");

        cartesian.title("Rendimiento Académico de " + materia);
        cartesian.yAxis(0).title("Calificación");
        cartesian.xAxis(0).title("Evaluación");
        cartesian.yScale().minimum(0).maximum(100);

        anyChartView.setChart(cartesian);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AlumnoActivity.class));
        finish();
    }
}
