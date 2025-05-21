package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.core.cartesian.series.Line;
import com.example.academtracker.R;
import com.example.academtracker.model.Materia;

import java.util.ArrayList;
import java.util.List;

public class GraficasAdapter extends RecyclerView.Adapter<GraficasAdapter.ViewHolder> {

    private List<Materia> materiasList;

    public GraficasAdapter(List<Materia> materiasList) {
        this.materiasList = materiasList;
    }

    @NonNull
    @Override
    public GraficasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grafica, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GraficasAdapter.ViewHolder holder, int position) {
        Materia materia = materiasList.get(position);

        holder.txtMateria.setText(materia.getNombre());

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true);


        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("Calificaciones por Parcial");

        cartesian.yAxis(0).title("Calificación");
        cartesian.xAxis(0).title("Parciales");

        List<DataEntry> data = new ArrayList<>();
        try {
            double p1 = (materia.getParcial1());
            double p2 = (materia.getParcial2());
            double p3 = (materia.getParcial3());

            data.add(new ValueDataEntry("Parcial 1", p1));
            data.add(new ValueDataEntry("Parcial 2", p2));
            data.add(new ValueDataEntry("Parcial 3", p3));
        } catch (NumberFormatException e) {
            // Si hay error de conversión, poner 0s
            data.add(new ValueDataEntry("Parcial 1", 0));
            data.add(new ValueDataEntry("Parcial 2", 0));
            data.add(new ValueDataEntry("Parcial 3", 0));
        }

        Line series = cartesian.line(data);
        series.name("Calificación");
        series.hovered().markers().enabled(true);
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(false);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        holder.chartView.setChart(cartesian);
    }

    @Override
    public int getItemCount() {
        return materiasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMateria;
        AnyChartView chartView;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            chartView = itemView.findViewById(R.id.chartView);
        }
    }
}
