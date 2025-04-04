package com.example.academtracker.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Alumnos_Profesores_adapter extends RecyclerView.Adapter<Alumnos_Profesores_adapter.ViewHolderdatos> {

    private ArrayList<String> Listamateria;
    private ArrayList<String> Listaprofesor;
    private ArrayList<String> Listagrado;
    private ArrayList<String> ListanombreProfesor;  // Nueva lista para el nombre del profesor

    public Alumnos_Profesores_adapter(ArrayList<String> listamateria, ArrayList<String> listaprofesor, ArrayList<String> listagrado, ArrayList<String> listanombreProfesor) {
        this.Listamateria = listamateria != null ? listamateria : new ArrayList<>();
        this.Listaprofesor = listaprofesor != null ? listaprofesor : new ArrayList<>();
        this.Listagrado = listagrado != null ? listagrado : new ArrayList<>();
        this.ListanombreProfesor = listanombreProfesor != null ? listanombreProfesor : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_alumnos_profesores_single, parent, false);
        return new ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderdatos holder, int position) {
        if (position < Listamateria.size() && position < Listaprofesor.size() && position < Listagrado.size() && position < ListanombreProfesor.size()) {
            // Asignamos los valores correctos a los TextViews en el orden correcto
            holder.materia.setText("Materia: " + Listamateria.get(position));  // Mostrar el nombre de la materia
            holder.nombreProfesor.setText("Profesor: " + ListanombreProfesor.get(position));  // Mostrar el nombre del profesor
            holder.profesor.setText("Correo: " + Listaprofesor.get(position));  // Mostrar el correo del profesor
            holder.grado.setText("Grado: " + Listagrado.get(position));  // Mostrar el grado del alumno
        }
    }

    @Override
    public int getItemCount() {
        // Devuelve el número de elementos más pequeño entre las cuatro listas
        return Math.min(Listamateria.size(), Math.min(Listaprofesor.size(), Math.min(Listagrado.size(), ListanombreProfesor.size())));
    }

    public static class ViewHolderdatos extends RecyclerView.ViewHolder {
        TextView materia, nombreProfesor, profesor, grado;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);
            materia = itemView.findViewById(R.id.textViewmateria);
            nombreProfesor = itemView.findViewById(R.id.textViewnombreProfesor);  // Nombre del profesor
            profesor = itemView.findViewById(R.id.textViewprofesorCorreo);
            grado = itemView.findViewById(R.id.textViewgrado);
        }
    }
}
