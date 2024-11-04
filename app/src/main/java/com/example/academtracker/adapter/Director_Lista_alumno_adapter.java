package com.example.academtracker.adapter;

import android.app.Activity;
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

public class Director_Lista_alumno_adapter extends RecyclerView.Adapter<Director_Lista_alumno_adapter.ViewHolderdatos> {

    ArrayList<String> Listaemail;
    ArrayList<String> Listagrado;
    ArrayList<String> Listagrupo;
    ArrayList<String> Listanombre;
    ArrayList<String> imagealumn;
    ArrayList<String> ListaDocumentIds;
    Activity activity;
    private FirebaseFirestore db;
    private OnAlumnoDeleteListener deleteListener;

    public interface OnAlumnoDeleteListener {
        void onDeleteConfirmed(String documentId, int position);
    }

    public void setOnAlumnoDeleteListener(OnAlumnoDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public Director_Lista_alumno_adapter(ArrayList<String> listaemail, ArrayList<String> listagrado,
                                         ArrayList<String> listagrupo, ArrayList<String> listanombre,
                                         ArrayList<String> imagealumn, ArrayList<String> listaDocumentIds, Activity activity) {
        this.Listaemail = listaemail;
        this.Listagrado = listagrado;
        this.Listagrupo = listagrupo;
        this.Listanombre = listanombre;
        this.imagealumn = imagealumn;
        this.ListaDocumentIds = listaDocumentIds;
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolderdatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_director_lista_alumnos_single, parent, false);
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
                Picasso.get()
                        .load(FotoP)
                        .resize(350, 350)
                        .error(R.drawable.noneuser)
                        .into(holder.fotoalumno);
            } else {
                holder.fotoalumno.setImageResource(R.drawable.noneuser);
            }
        } catch (Exception e) {
            Log.e("PicassoError", "Error loading image: " + e.getMessage());
            holder.fotoalumno.setImageResource(R.drawable.noneuser);
        }

        // Lógica para el botón de eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            String documentId = ListaDocumentIds.get(position);  // Obtener el Document ID
            if (deleteListener != null) {
                deleteListener.onDeleteConfirmed(documentId, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Listanombre.size();
    }

    public class ViewHolderdatos extends RecyclerView.ViewHolder {

        TextView email, nombre, grado, grupo, password;
        ImageView fotoalumno;
        Button btnEliminar;

        public ViewHolderdatos(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewnombre);
            email = itemView.findViewById(R.id.textViewemail);
            grado = itemView.findViewById(R.id.textViewgrado);
            grupo = itemView.findViewById(R.id.textViewgrupo);
            fotoalumno = itemView.findViewById(R.id.imagealumno);
            btnEliminar = itemView.findViewById(R.id.btnEliminarAlumno);
        }
    }

    // Método para eliminar un alumno de Firestore
    public void eliminarAlumno(String documentId, int position) {
        db.collection("Alumnos").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Listaemail.remove(position);
                    Listagrado.remove(position);
                    Listagrupo.remove(position);
                    Listanombre.remove(position);
                    imagealumn.remove(position);
                    ListaDocumentIds.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());

                    Log.d("Firestore", "Alumno eliminado con éxito!");
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al eliminar el alumno", e));
    }
}


