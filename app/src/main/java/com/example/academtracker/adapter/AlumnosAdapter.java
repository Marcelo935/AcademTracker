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
import com.example.academtracker.model.GradoItem;

import java.util.ArrayList;


public class AlumnosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0; // Tipo de vista para encabezados
    private static final int TYPE_ITEM = 1;  // Tipo de vista para alumnos

    private ArrayList<GradoItem> listaItems;  // Lista combinada de encabezados y alumnos
    private ArrayList<String> alumnosSeleccionados; // Lista de IDs de alumnos seleccionados

    public AlumnosAdapter(ArrayList<GradoItem> listaItems, ArrayList<String> alumnosSeleccionados) {
        this.listaItems = listaItems;
        this.alumnosSeleccionados = alumnosSeleccionados;
    }

    @Override
    public int getItemViewType(int position) {
        return listaItems.get(position).isEncabezado() ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grado_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno, parent, false);
            return new AlumnoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GradoItem item = listaItems.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.getGrado());
        } else if (holder instanceof AlumnoViewHolder) {
            ((AlumnoViewHolder) holder).bind(item.getAlumno());
        }
    }

    @Override
    public int getItemCount() {
        return listaItems.size();
    }

    // ViewHolder para encabezados
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }

        public void bind(String grado) {
            tvHeader.setText(grado);
        }
    }

    // ViewHolder para alumnos
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
            cbSeleccionar.setOnCheckedChangeListener(null); // Evitar conflictos

            // Configurar el CheckBox según si el alumno está seleccionado
            cbSeleccionar.setChecked(alumnosSeleccionados.contains(alumno.getId()));

            // Listener para el CheckBox
            cbSeleccionar.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Agregar alumno a la lista de seleccionados
                    if (!alumnosSeleccionados.contains(alumno.getId())) {
                        alumnosSeleccionados.add(alumno.getId());
                    }
                } else {
                    // Quitar alumno de la lista de seleccionados
                    alumnosSeleccionados.remove(alumno.getId());
                }
            });
        }
    }
}


