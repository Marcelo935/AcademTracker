package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.Alumno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AlumnosAdapter extends RecyclerView.Adapter<AlumnosAdapter.AlumnoViewHolder> {

    private ArrayList<Alumno> listaAlumnos;
    private ArrayList<String> alumnosSeleccionados;

    public AlumnosAdapter(ArrayList<Alumno> listaAlumnos, ArrayList<String> alumnosSeleccionados) {
        this.listaAlumnos = listaAlumnos;
        this.alumnosSeleccionados = alumnosSeleccionados;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno, parent, false);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        Alumno alumno = listaAlumnos.get(position);
        holder.bind(alumno);
    }

    @Override
    public int getItemCount() {
        return listaAlumnos.size();
    }

    class AlumnoViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre;
        CheckBox cbSeleccionar;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreAlumno);
            cbSeleccionar = itemView.findViewById(R.id.cbSeleccionarAlumno);
        }

        public void bind(Alumno alumno) {
            tvNombre.setText(alumno.getNombre());
            cbSeleccionar.setOnCheckedChangeListener(null); // Evita comportamientos inesperados

            // Configura el CheckBox según si el alumno está seleccionado
            cbSeleccionar.setChecked(alumnosSeleccionados.contains(alumno.getId()));

            // Listener del CheckBox
            cbSeleccionar.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Agregar alumno a la lista de seleccionados
                    if (!alumnosSeleccionados.contains(alumno.getId())) {
                        alumnosSeleccionados.add(alumno.getId());
                    }
                } else {
                    // Eliminar alumno de la lista de seleccionados
                    alumnosSeleccionados.remove(alumno.getId());
                }
            });
        }
    }
}


