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

import com.example.academtracker.UsuarioAvanzado.ModifcarSecretariasActivity;
import com.example.academtracker.model.Profesores;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class Secretarias_materias_adapter extends FirestoreRecyclerAdapter<Profesores,Secretarias_materias_adapter.ViewHolder > {
    AppCompatActivity Activity;
    Context context;

    public Secretarias_materias_adapter(@NonNull FirestoreRecyclerOptions<Profesores> options,AppCompatActivity activity) {
        super(options);
        this.Activity = activity;
        this.context = activity.getApplicationContext();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Profesores profesores) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
        final String DocId = documentSnapshot.getId();
        Log.d("ACTALUM", "Nombres de los alumnos: " + profesores.getNombre());
        viewHolder.Nombre.setText(profesores.getNombre());
        viewHolder.Mail.setText(profesores.getEmail());

        viewHolder.IrVerProfesores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, ModifcarSecretariasActivity.class);
                intent.putExtra("uid", DocId);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public Secretarias_materias_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profesores, parent, false);
        return new Secretarias_materias_adapter.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Nombre, Mail;
        LinearLayout IrVerProfesores;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Nombre = itemView.findViewById(R.id.Nombre_Profesor);
            Mail = itemView.findViewById(R.id.Mail_Profesor);
            IrVerProfesores = itemView.findViewById(R.id.linear_verprofesores);
        }
    }
}
