package com.example.academtracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.example.academtracker.model.Materia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MateriasAdapter extends RecyclerView.Adapter<MateriasAdapter.MateriaViewHolder> {

    private List<Materia> materias;
    private FirebaseFirestore firestore;
    private String uid;

    // Constructor
    public MateriasAdapter(List<Materia> materias, FirebaseFirestore firestore, String uid) {
        this.materias = materias;
        this.firestore = firestore;
        this.uid = uid;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia_card, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = materias.get(position);

        // Asignar datos
        holder.nombre.setText(materia.getNombre());
        holder.etParcial1.setText(materia.getParcial1());
        holder.etParcial2.setText(materia.getParcial2());
        holder.etParcial3.setText(materia.getParcial3());

        // Evento para el botón guardar
        holder.guardarButton.setOnClickListener(v -> {
            String parcial1 = holder.etParcial1.getText().toString();
            String parcial2 = holder.etParcial2.getText().toString();
            String parcial3 = holder.etParcial3.getText().toString();

            if (uid == null || uid.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "UID no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            if (!parcial1.isEmpty()) {
                updates.put("1er parcial", parcial1);
            }
            if (!parcial2.isEmpty()) {
                updates.put("2do parcial", parcial2);
            }
            if (!parcial3.isEmpty()) {
                updates.put("3er parcial", parcial3);
            }

            if (!updates.isEmpty()) {
                firestore.collection("Alumnos")
                        .document(uid)
                        .collection("calificaciones")
                        .document(materia.getNombre())
                        .update(updates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(holder.itemView.getContext(), "Calificaciones guardadas", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error al guardar", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(holder.itemView.getContext(), "No hay datos para actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }

    static class MateriaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        EditText etParcial1, etParcial2, etParcial3;
        Button guardarButton;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.materia_nombre);
            etParcial1 = itemView.findViewById(R.id.et_algebra1);
            etParcial2 = itemView.findViewById(R.id.et_algebra2);
            etParcial3 = itemView.findViewById(R.id.et_algebra3);
            guardarButton = itemView.findViewById(R.id.button_algebra);
        }
    }
}
