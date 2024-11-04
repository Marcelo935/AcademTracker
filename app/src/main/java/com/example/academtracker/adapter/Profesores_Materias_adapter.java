package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Profesores_Materias_adapter extends RecyclerView.Adapter<Profesores_Materias_adapter.ViewHolderdatos>{
    ArrayList<String> Listamateria;
    ArrayList<String> Listaprofesor;
    ArrayList<String> Listagrado;

    public Profesores_Materias_adapter(ArrayList<String> listamateria, ArrayList<String> listaprofesor, ArrayList<String> listagrado) {
        this.Listamateria = listamateria;
        this.Listaprofesor = listaprofesor;
        this.Listagrado = listagrado;
    }

    @NonNull
    @Override
    public Profesores_Materias_adapter.ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_profesores_materias_single,null, false);

        return new Profesores_Materias_adapter.ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Profesores_Materias_adapter.ViewHolderdatos holder, int position) {
        holder.materia.setText("Materia: " + Listamateria.get(position));
        holder.profesor.setText("Profesor: " +Listaprofesor.get(position));
        holder.grado.setText("Grado: " +Listagrado.get(position));
    }

    @Override
    public int getItemCount() {
        return Listamateria.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView materia , profesor, grado;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);

            materia = itemView.findViewById(R.id.textViewmateria);
            profesor = itemView.findViewById(R.id.textViewprofesor);
            grado = itemView.findViewById(R.id.textViewgrado);
        }
    }
}
