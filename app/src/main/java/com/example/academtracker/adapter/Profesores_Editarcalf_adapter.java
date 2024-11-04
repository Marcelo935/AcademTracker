package com.example.academtracker.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Profesores_Editarcalf_adapter extends RecyclerView.Adapter<Profesores_Editarcalf_adapter.ViewHolderdatos>{

    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listacalif;

    String [] Listaedit;

    public Profesores_Editarcalf_adapter(ArrayList<String> listamateria, ArrayList<String> listaalumno, ArrayList<String> listagrado, ArrayList<Integer> listacalif) {
        this.Listamateria = listamateria;
        this.Listaalumno = listaalumno;
        this.Listagrado = listagrado;
        this.Listacalif = listacalif;
        Listaedit = new String[listamateria.size()];//Se define el arreglo
    }

    @NonNull
    @Override
    public Profesores_Editarcalf_adapter.ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_profesores_editcalif_single,null, false);
        return new Profesores_Editarcalf_adapter.ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Profesores_Editarcalf_adapter.ViewHolderdatos holder, int position) {
        holder.alumno.setText("Alumno: " + Listaalumno.get(position));
        holder.materia.setText("Materia: " + Listamateria.get(position));
        holder.grado.setText("Grado: " + Listagrado.get(position));
        holder.calif.setText("Calificacion: " + Listacalif.get(position));
    }

    @Override
    public int getItemCount() {
        return Listamateria.size();
    }

    public String[] getListaedit() {
        return Listaedit;
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView alumno,materia,grado,calif;
        EditText editar;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);

            alumno = itemView.findViewById(R.id.editalumno);
            materia = itemView.findViewById(R.id.editnombremateria);
            grado = itemView.findViewById(R.id.editgrado);
            calif = itemView.findViewById(R.id.editcalif);
            editar = (EditText)itemView.findViewById(R.id.editnotes);
            editar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Listaedit[getAdapterPosition()] = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
