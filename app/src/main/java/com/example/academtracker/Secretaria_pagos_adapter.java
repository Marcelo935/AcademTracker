package com.example.academtracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.model.Pagos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class Secretaria_pagos_adapter extends FirestoreRecyclerAdapter<Pagos,Secretaria_pagos_adapter.ViewHolder> {
    AppCompatActivity Activity;
    Context context;

    public Secretaria_pagos_adapter(@NonNull FirestoreRecyclerOptions<Pagos> options, AppCompatActivity activity) {
        super(options);
        this.Activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Pagos pagos) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String DocId = documentSnapshot.getId();
        Log.d("ACTALUM", "Nombres de los alumnos: " + pagos.getAlumnoId());
        viewHolder.Correo.setText(pagos.getAlumnoId());
        viewHolder.MetodoPago.setText(pagos.getMetodoPago());
        viewHolder.Monto.setText(pagos.getMonto());
        viewHolder.Fecha.setText(pagos.getFecha());
    }

    @NonNull
    @Override
    public Secretaria_pagos_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pagos, parent, false);
        return new Secretaria_pagos_adapter.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Correo, MetodoPago, Monto, Fecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Correo = itemView.findViewById(R.id.correo_Alumno);
            MetodoPago = itemView.findViewById(R.id.metodo_Pago);
            Monto = itemView.findViewById(R.id.monto_Pago);
            Fecha = itemView.findViewById(R.id.fecha_pago);
        }
    }
}
