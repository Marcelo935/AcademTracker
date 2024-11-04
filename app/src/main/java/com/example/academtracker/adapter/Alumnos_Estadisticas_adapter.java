package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Alumnos_Estadisticas_adapter extends RecyclerView.Adapter<Alumnos_Estadisticas_adapter.ViewHolderdatos>{

    ArrayList<String> Listamaterias;
    ArrayList<String> Listaclave;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalif;
    ArrayList<Integer>Listaasist;
    ArrayList<Integer>Listaporcentaje;

    public Alumnos_Estadisticas_adapter(ArrayList<String> listamaterias, ArrayList<String> listaclave, ArrayList<String> listagrado, ArrayList<Integer> listacalif, ArrayList<Integer> listaasist,ArrayList<Integer> listaporcentaje) {
        this.Listamaterias = listamaterias;
        this.Listaclave = listaclave;
        this.Listagrado = listagrado;
        this.Listacalif = listacalif;
        this.Listaasist = listaasist;
        this.Listaporcentaje = listaporcentaje;
    }

    @NonNull
    @Override
    public Alumnos_Estadisticas_adapter.ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_alumno_estadistica_single,null, false);
        return new Alumnos_Estadisticas_adapter.ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Alumnos_Estadisticas_adapter.ViewHolderdatos holder, int position) {
        holder.NombreMateria.setText("Materia: " + Listamaterias.get(position));
        holder.ClaveMateria.setText("Clave: " +Listaclave.get(position));
        holder.grado.setText("Grado: " +Listagrado.get(position));
        holder.Calificacion.setText("Calificacion: " + Listacalif.get(position));
        holder.Asistencias.setText("Asistencia: " + Listaasist.get(position));
        holder.Porcentaje.setText("% asistencias: " + Listaporcentaje.get(position));
    }

    @Override
    public int getItemCount() {
        return Listamaterias.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView NombreMateria,ClaveMateria,grado,Calificacion,Asistencias, Porcentaje;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);

            NombreMateria = itemView.findViewById(R.id.textViewmateria);
            ClaveMateria = itemView.findViewById(R.id.textViewclave);
            grado = itemView.findViewById(R.id.textViewgrado);
            Calificacion = itemView.findViewById(R.id.textViewcalif);
            Asistencias = itemView.findViewById(R.id.textViewasist);
            Porcentaje =  itemView.findViewById(R.id.textViewporcentajeasis);
        }
    }
}
