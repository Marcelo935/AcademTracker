package com.example.academtracker.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profesores_Asistencias_adapter extends RecyclerView.Adapter<Profesores_Asistencias_adapter.ViewHolderdatos> {

    ArrayList<String> Listamateria;
    ArrayList<String> Listaalumno;
    ArrayList<String> Listagrado;
    ArrayList<Integer> Listaasist;
    ArrayList<Integer> ListaasistT;
    ArrayList<String> Imagen;
    Activity activity;

    public Profesores_Asistencias_adapter(ArrayList<String> listamateria, ArrayList<String> listaalumno, ArrayList<String> listagrado, ArrayList<Integer> listaasist, ArrayList<Integer> listaasistT, ArrayList<String> imagen, Activity activity) {
        this.Listamateria = listamateria;
        this.Listaalumno = listaalumno;
        this.Listagrado = listagrado;
        this.Listaasist = listaasist;
        this.ListaasistT = listaasistT;
        this.Imagen = imagen;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_profesores_asistencias_single, parent, false);
        return new ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderdatos holder, int position) {
        holder.materia.setText("Materia: " + Listamateria.get(position));
        holder.alumno.setText("Alumno: " + Listaalumno.get(position));
        holder.grado.setText("Grado: " + Listagrado.get(position));
        holder.asistencia.setText("Asistencia: " + Listaasist.get(position));
        holder.totalasistencia.setText("Asistencia Total: " + ListaasistT.get(position));

        String FotoP = Imagen.get(position);

        try {
            if (FotoP != null && !FotoP.isEmpty()) {
                Picasso.get()
                        .load(FotoP)
                        .resize(350, 350)
                        .into(holder.foto);
            } else {
                holder.foto.setImageResource(R.drawable.noneuser);
            }
        } catch (Exception e) {
            Log.e("PicassoError", "Error loading image: " + e.getMessage());
            holder.foto.setImageResource(R.drawable.noneuser);
        }
    }

    @Override
    public int getItemCount() {
        return Listamateria.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView materia, alumno, grado, asistencia, totalasistencia;
        ImageView foto;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);
            materia = itemView.findViewById(R.id.textViewmateria);
            alumno = itemView.findViewById(R.id.textViewalumno);
            grado = itemView.findViewById(R.id.textViewgrado);
            asistencia = itemView.findViewById(R.id.textViewasist);
            foto = itemView.findViewById(R.id.imageView3);
            totalasistencia = itemView.findViewById(R.id.textViewasistT);
        }
    }
}
