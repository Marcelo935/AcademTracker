package com.example.academtracker.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Secretarias_Lista_alumno_adapter extends RecyclerView.Adapter<Secretarias_Lista_alumno_adapter.ViewHolderDatos> {

    ArrayList<String> listaEmail;
    ArrayList<String> listaGrado;
    ArrayList<String> listaGrupo;
    ArrayList<String> listaNombre;
    ArrayList<String> listaImagenes;
    ArrayList<String> listaDocumentIds;
    Activity activity;
    private FirebaseFirestore db;
    private OnAlumnoDeleteListener deleteListener;

    public interface OnAlumnoDeleteListener {
        void onDeleteConfirmed(String documentId, int position);
    }

    public void setOnAlumnoDeleteListener(OnAlumnoDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public Secretarias_Lista_alumno_adapter(ArrayList<String> listaEmail, ArrayList<String> listaGrado,
                                            ArrayList<String> listaGrupo, ArrayList<String> listaNombre,
                                            ArrayList<String> listaImagenes, ArrayList<String> listaDocumentIds, Activity activity) {
        this.listaEmail = listaEmail;
        this.listaGrado = listaGrado;
        this.listaGrupo = listaGrupo;
        this.listaNombre = listaNombre;
        this.listaImagenes = listaImagenes;
        this.listaDocumentIds = listaDocumentIds;
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_secretarias_lista_alumnos_single, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.email.setText(listaEmail.get(position));
        holder.nombre.setText(listaNombre.get(position));
        holder.grado.setText("Grado: " + listaGrado.get(position));
        holder.grupo.setText("Grupo: " + listaGrupo.get(position));

        String fotoUrl = listaImagenes.get(position);
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Picasso.get().load(fotoUrl).resize(350, 350)
                    .error(R.drawable.noneuser).into(holder.fotoAlumno);
        } else {
            holder.fotoAlumno.setImageResource(R.drawable.noneuser);
        }

        // Configurar el botón de eliminar con confirmación
        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(activity)
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Está seguro de que desea eliminar este alumno?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        String documentId = listaDocumentIds.get(position);
                        if (deleteListener != null) {
                            deleteListener.onDeleteConfirmed(documentId, position);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaNombre.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView email, nombre, grado, grupo;
        ImageView fotoAlumno;
        Button btnEliminar;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.SecretariaViewnombre);
            email = itemView.findViewById(R.id.SecretariaViewemail);
            grado = itemView.findViewById(R.id.SecretariaViewgrado);
            grupo = itemView.findViewById(R.id.SecretariaViewgrupo);
            fotoAlumno = itemView.findViewById(R.id.Secretariaimagealumno);
            btnEliminar = itemView.findViewById(R.id.SecretariabtnEliminarAlumno);
        }
    }

    // Método para eliminar un alumno de Firestore
    public void eliminarAlumno(String documentId, int position) {
        db.collection("Alumnos").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar el alumno de las listas locales
                    listaEmail.remove(position);
                    listaGrado.remove(position);
                    listaGrupo.remove(position);
                    listaNombre.remove(position);
                    listaImagenes.remove(position);
                    listaDocumentIds.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                    Log.d("Firestore", "Alumno eliminado con éxito!");
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al eliminar el alumno", e));
    }
}



