package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.Pagos;

import java.util.List;

public class AlumnosPagos_adapter extends RecyclerView.Adapter<AlumnosPagos_adapter.PagoViewHolder> {

    private List<Pagos> listaPagos;

    // Constructor
    public AlumnosPagos_adapter(List<Pagos> listaPagos) {
        this.listaPagos = listaPagos;
    }

    @NonNull
    @Override
    public PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño del item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pago, parent, false);
        return new PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoViewHolder holder, int position) {
        // Obtenemos el pago actual
        Pagos pago = listaPagos.get(position);

        // Configuramos los valores en las vistas
        holder.tvMonto.setText("Monto: $" + pago.getMonto());
        holder.tvMetodoPago.setText("Método: " + pago.getMetodoPago());
        holder.tvFecha.setText("Fecha: " + pago.getFecha());
        holder.tvConcepto.setText("Concepto: " + pago.getconceptoPago());
    }

    @Override
    public int getItemCount() {
        return listaPagos.size();
    }

    // Clase ViewHolder
    static class PagoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonto, tvMetodoPago, tvFecha, tvConcepto;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonto = itemView.findViewById(R.id.tvMonto);
            tvMetodoPago = itemView.findViewById(R.id.tvMetodoPago);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvConcepto = itemView.findViewById(R.id.tvConceptoPago);
        }
    }
}

