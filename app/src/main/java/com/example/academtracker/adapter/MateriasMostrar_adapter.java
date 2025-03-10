package com.example.academtracker.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.MateriasAlumnos;

import java.util.List;
import java.util.Map;

public class MateriasMostrar_adapter extends RecyclerView.Adapter<MateriasMostrar_adapter.MateriaViewHolder> {

    private final List<MateriasAlumnos> materias;

    public MateriasMostrar_adapter(List<MateriasAlumnos> materias) {
        this.materias = materias;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mostrar_calificaciones, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        MateriasAlumnos materia = materias.get(position);

        Log.d("MateriasMostrar_adapter", "Binding materia: " + materia.getNombre());

        holder.nombreTextView.setText(materia.getNombre());

        Map<String, Double> calificaciones = materia.getCalificaciones();
        StringBuilder calificacionesTexto = new StringBuilder();

        if (calificaciones != null && !calificaciones.isEmpty()) {
            // Iterar sobre todas las claves (parciales) del mapa de calificaciones
            for (Map.Entry<String, Double> entry : calificaciones.entrySet()) {
                calificacionesTexto.append("").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        } else {
            calificacionesTexto.append("Sin calificaciones disponibles.");
        }

        Log.d("MateriasMostrar_adapter", "Calificaciones: " + calificacionesTexto.toString());

        holder.calificacionesTextView.setText(calificacionesTexto.toString());
    }

    @Override
    public int getItemCount() {
        int count = (materias != null) ? materias.size() : 0;
        Log.d("MateriasMostrar_adapter", "Item count: " + count);
        return count;
    }

    public static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView calificacionesTextView;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            calificacionesTextView = itemView.findViewById(R.id.calificaciones);
        }
    }
}
