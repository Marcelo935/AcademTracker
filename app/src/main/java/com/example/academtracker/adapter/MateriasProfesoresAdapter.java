package com.example.academtracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.Materias;

import java.util.List;

public class MateriasProfesoresAdapter extends RecyclerView.Adapter<MateriasProfesoresAdapter.MateriaViewHolder> {

    private List<Materias> materiasList;
    private Context context;

    // Constructor del adaptador
    public MateriasProfesoresAdapter(Context context, List<Materias> materiasList) {
        this.context = context;
        this.materiasList = materiasList;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar la vista del elemento de la lista
        View view = LayoutInflater.from(context).inflate(R.layout.item_profesores_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        // Obtener el objeto Materias de la lista
        Materias materia = materiasList.get(position);

        // Validar datos antes de asignarlos
        if (materia != null) {
            holder.nombreTextView.setText(materia.getNombre() != null ? materia.getNombre() : "Sin nombre");
        }
    }

    @Override
    public int getItemCount() {
        // Retornar el tamaño de la lista
        return materiasList != null ? materiasList.size() : 0;
    }

    public class MateriaViewHolder extends RecyclerView.ViewHolder {

        // Declarar los TextView
        TextView nombreTextView;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Vincular las vistas con sus IDs
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
        }
    }
}
