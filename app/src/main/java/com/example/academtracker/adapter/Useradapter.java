package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;

import java.util.ArrayList;

public class Useradapter extends RecyclerView.Adapter<Useradapter.Viewholderdatos> {
    //Se crearon arreglos para los distintos datos de la base de datos de tipo "string"
    ArrayList<String> Listanombre;
    ArrayList<String> Listaemail;
    ArrayList<String> Listapassword;

    public Useradapter(ArrayList<String> listanombre, ArrayList<String> listaemail, ArrayList<String> listapassword) {
        //this hace referencia a un solo archivo
        this.Listanombre = listanombre;
        this.Listaemail = listaemail;
        this.Listapassword = listapassword;
    }

    @NonNull
    @Override
    public Viewholderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Esta funcion nos permitira enlazar el adaptador con la single view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_user_single,null, false);
        return new Viewholderdatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholderdatos holder, int position) {
        //Esta funcion se encarga de establecer la comunicacion entre el adaptador y la clase view holder
        holder.nombre.setText(Listanombre.get(position));
        holder.email.setText(Listaemail.get(position));
        holder.password.setText(Listapassword.get(position));
    }

    @Override
    public int getItemCount() {
        return Listanombre.size();
    }

    public class Viewholderdatos extends RecyclerView.ViewHolder {

        TextView nombre , email, password;

        public Viewholderdatos(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.textViewName);
            password = itemView.findViewById(R.id.textViewPassword);
            email = itemView.findViewById(R.id.textViewEmail);

        }
    }
}
