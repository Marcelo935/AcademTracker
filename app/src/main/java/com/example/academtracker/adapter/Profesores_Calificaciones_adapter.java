package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Profesores_Calificaciones_adapter extends RecyclerView.Adapter<Profesores_Calificaciones_adapter.ViewHolderdatos> {
    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalificacion;

    public Profesores_Calificaciones_adapter(ArrayList<String> listamateria, ArrayList<String> listaalumno, ArrayList<String> listagrado, ArrayList<Integer> listacalificacion) {
        this.Listamateria = listamateria;
        this.Listaalumno = listaalumno;
        this.Listagrado = listagrado;
        this.Listacalificacion = listacalificacion;
    }

    @NonNull
    @Override
    public Profesores_Calificaciones_adapter.ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_profesores_calificaciones_single,null, false);

        return new Profesores_Calificaciones_adapter.ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Profesores_Calificaciones_adapter.ViewHolderdatos holder, int position) {
        holder.materia.setText("Materia: " + Listamateria.get(position));
        holder.alumno.setText("Alumno: " + Listaalumno.get(position));
        holder.grado.setText("Grado: " + Listagrado.get(position));
        holder.calificacion.setText("Calificacion: " + Listacalificacion.get(position));
    }

    @Override
    public int getItemCount() {
        return Listamateria.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView materia,alumno,grado,calificacion;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);

            materia = itemView.findViewById(R.id.textViewmateria);
            alumno = itemView.findViewById(R.id.textViewalumno);
            grado = itemView.findViewById(R.id.textViewgrado);
            calificacion = itemView.findViewById(R.id.textViewcalf);
        }
    }
}
