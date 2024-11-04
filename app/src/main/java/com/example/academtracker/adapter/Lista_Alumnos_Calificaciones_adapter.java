package com.example.academtracker.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.UsuarioAvanzado.ModifcarSecretariasActivity;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Lista_Alumnos_Calificaciones_adapter extends RecyclerView.Adapter<Lista_Alumnos_Calificaciones_adapter.ViewHolderdatos> {
    ArrayList<String> Listaemail;
    ArrayList<String> Listagrado;
    ArrayList<String> Listagrupo;
    ArrayList<String> Listanombre;
    ArrayList<String> imagealumn;
    Activity activity;

    public Lista_Alumnos_Calificaciones_adapter(ArrayList<String> listaemail, ArrayList<String> listagrado,
                                                ArrayList<String> listagrupo, ArrayList<String> listanombre,
                                                ArrayList<String> imagealumn, Activity activity) {
        this.Listaemail = listaemail;
        this.Listagrado = listagrado;
        this.Listagrupo = listagrupo;
        this.Listanombre = listanombre;
        this.imagealumn = imagealumn;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_secretarias_calif_single, parent, false);
        return new ViewHolderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderdatos holder, int position) {
        holder.email.setText(Listaemail.get(position));
        holder.nombre.setText(Listanombre.get(position));
        holder.grado.setText("Grado: " + Listagrado.get(position));
        holder.grupo.setText("Grupo: " + Listagrupo.get(position));

        String FotoP = imagealumn.get(position);
        try {
            if (FotoP != null && !FotoP.isEmpty()) {
                Log.d("Picasso", "Cargando imagen desde URL: " + FotoP);
                Picasso.get()
                        .load(FotoP)
                        .resize(350, 350)
                        .error(R.drawable.noneuser)  // Imagen si falla la carga
                        .into(holder.fotoalumno);
            } else {
                holder.fotoalumno.setImageResource(R.drawable.noneuser);
            }
        } catch (Exception e) {
            Log.e("PicassoError", "Error loading image: " + e.getMessage());
            holder.fotoalumno.setImageResource(R.drawable.noneuser);
        }

        // Establecer el click listener para el botón de calificaciones
        holder.calificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la actividad de modificación
                Intent intent = new Intent(activity, ModifcarSecretariasActivity.class);
                // Puedes pasar datos adicionales si es necesario
                // intent.putExtra("email", Listaemail.get(position)); // Ejemplo de pasar datos
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Listanombre.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {
        TextView email, nombre, grado, grupo;
        ImageView fotoalumno;
        MaterialButton calificaciones;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewnombre);
            email = itemView.findViewById(R.id.textViewemail);
            grado = itemView.findViewById(R.id.textViewgrado);
            grupo = itemView.findViewById(R.id.textViewgrupo);
            fotoalumno = itemView.findViewById(R.id.imagealumno);
            calificaciones = itemView.findViewById(R.id.btncalificaciones); // Asegúrate de que el ID sea correcto
        }
    }
}
