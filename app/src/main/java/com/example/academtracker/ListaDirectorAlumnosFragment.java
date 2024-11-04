package com.example.academtracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.academtracker.adapter.Director_Lista_alumno_adapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListaDirectorAlumnosFragment extends DialogFragment implements Director_Lista_alumno_adapter.OnAlumnoDeleteListener {
    RecyclerView alumnoslista;
    MaterialButton regresar;
    ArrayList<String> Listaemail;
    ArrayList<String> Listagrado;
    ArrayList<String> Listagrupo;
    ArrayList<String> Listanombre;
    ArrayList<String> Listapassword;
    ArrayList<String> imagealumn;
    ArrayList<String> ListaDocumentIds;
    Director_Lista_alumno_adapter adaptador;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista_alumnos, container, false);
        regresar = v.findViewById(R.id.regresar);
        alumnoslista = v.findViewById(R.id.listaalumnosdirector);
        alumnoslista.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        Listaemail = new ArrayList<>();
        Listagrado = new ArrayList<>();
        Listagrupo = new ArrayList<>();
        Listanombre = new ArrayList<>();
        Listapassword = new ArrayList<>();
        imagealumn = new ArrayList<>();
        ListaDocumentIds = new ArrayList<>();

        CollectionReference collectionReference = db.collection("Alumnos");
        collectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                Listaemail.add(document.getString("email"));
                Listagrado.add(document.getString("grado"));
                Listagrupo.add(document.getString("grupo"));
                Listanombre.add(document.getString("nombre"));
                Listapassword.add(document.getString("password"));
                imagealumn.add(document.getString("foto"));
                ListaDocumentIds.add(document.getId());
            }

            adaptador = new Director_Lista_alumno_adapter(Listaemail, Listagrado, Listagrupo, Listanombre, imagealumn, ListaDocumentIds, getActivity());
            adaptador.setOnAlumnoDeleteListener(this);  // Asignar el listener
            alumnoslista.setAdapter(adaptador);
        });

        regresar.setOnClickListener(v1 -> dismiss());
        return v;
    }

    // Implementación de la interfaz para mostrar el diálogo de confirmación
    @Override
    public void onDeleteConfirmed(String documentId, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmación de eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este alumno?")
                .setPositiveButton("Eliminar", (dialog, which) -> adaptador.eliminarAlumno(documentId, position))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

