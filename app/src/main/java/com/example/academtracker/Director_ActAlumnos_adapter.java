package com.example.academtracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.model.Alumnos;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Director_ActAlumnos_adapter extends FirestoreRecyclerAdapter<Alumnos, Director_ActAlumnos_adapter.ViewHolder> {
    AppCompatActivity Activity;
    Context context;

    public Director_ActAlumnos_adapter(@NonNull FirestoreRecyclerOptions<Alumnos> options, AppCompatActivity activity) {
        super(options);
        this.Activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Alumnos alumnos) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String DocId = documentSnapshot.getId();
        Log.d("ACTALUM", "Nombres de los alumnos: " + alumnos.getNombre());
        viewHolder.Nombre.setText(alumnos.getNombre());
        viewHolder.Grupo.setText(alumnos.getGrupo());
        viewHolder.Grado.setText(alumnos.getGrado());
        viewHolder.Mail.setText(alumnos.getEmail());
        String foto = alumnos.getFoto();

        if (alumnos.getFoto() != null && !alumnos.getFoto().isEmpty()) {
            Picasso.get()
                    .load(foto)
                    .resize(350, 350)
                    .into(viewHolder.circleImageView);
        } else {
            viewHolder.circleImageView.setImageResource(R.drawable.noneuser);
        }

        viewHolder.IrAEditarAlumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EditarAlumno_Director.class);
                intent.putExtra("uid", DocId);
                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_alum_edit, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Nombre, Grupo, Grado, Mail;
        CircleImageView circleImageView;
        LinearLayout IrAEditarAlumnos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.Nombre_EditAlum);
            Grupo = itemView.findViewById(R.id.Grupo_EditAlum);
            Grado = itemView.findViewById(R.id.Grado_EditAlum);
            Mail = itemView.findViewById(R.id.Mail_EditAlum);
            circleImageView = itemView.findViewById(R.id.foto_EditAlum);
            IrAEditarAlumnos = itemView.findViewById(R.id.linear_editAlums);
        }
    }
}
