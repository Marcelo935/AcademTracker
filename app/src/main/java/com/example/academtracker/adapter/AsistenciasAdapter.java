package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.Asistencia;

import java.util.List;

public class AsistenciasAdapter extends RecyclerView.Adapter<AsistenciasAdapter.ViewHolder> {

    private List<Asistencia> asistencias;

    public AsistenciasAdapter(List<Asistencia> asistencias) {
        this.asistencias = asistencias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_asistencia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asistencia asistencia = asistencias.get(position);
        holder.tvAlumno.setText("Alumno: " + asistencia.getAlumnoID());
        holder.tvMateria.setText("Materia: " + asistencia.getMateria());
        holder.tvFecha.setText("Fecha: " + asistencia.getFecha().toString());
        holder.tvPresente.setText("Presente: " + (asistencia.isPresente() ? "Sí" : "No"));
    }

    @Override
    public int getItemCount() {
        return asistencias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlumno, tvMateria, tvFecha, tvPresente;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlumno = itemView.findViewById(R.id.tvAlumno);
            tvMateria = itemView.findViewById(R.id.tvMateria);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvPresente = itemView.findViewById(R.id.tvPresente);

        }
    }
}
