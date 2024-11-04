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

import com.example.academtracker.adapter.Secretarias_Lista_alumno_adapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListaSecretariasAlumnosFragment extends DialogFragment implements Secretarias_Lista_alumno_adapter.OnAlumnoDeleteListener {

    RecyclerView alumnoslista;
    MaterialButton regresar;
    ArrayList<String> listaEmail;
    ArrayList<String> listaGrado;
    ArrayList<String> listaGrupo;
    ArrayList<String> listaNombre;
    ArrayList<String> listaPassword;
    ArrayList<String> listaImagenes;
    ArrayList<String> listaDocumentIds;  // Lista para almacenar los Document IDs
    Secretarias_Lista_alumno_adapter adaptador;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View v = inflater.inflate(R.layout.fragment_lista_alumnos, container, false);
        regresar = v.findViewById(R.id.regresar);

        // Configurar RecyclerView
        alumnoslista = v.findViewById(R.id.listaalumnosdirector);
        alumnoslista.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Inicializar listas
        listaEmail = new ArrayList<>();
        listaGrado = new ArrayList<>();
        listaGrupo = new ArrayList<>();
        listaNombre = new ArrayList<>();
        listaPassword = new ArrayList<>();
        listaImagenes = new ArrayList<>();
        listaDocumentIds = new ArrayList<>();  // Inicializar la lista para Document IDs

        // Obtener referencia de la colección "Alumnos"
        CollectionReference collectionReference = db.collection("Alumnos");

        // Consulta a Firestore
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(@NonNull QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    String email = document.getString("email");
                    String grado = document.getString("grado");
                    String grupo = document.getString("grupo");
                    String nombre = document.getString("nombre");
                    String password = document.getString("password");
                    String imageUrl = document.getString("foto");
                    String documentId = document.getId();  // Obtener el Document ID

                    // Log para verificar que se obtiene la URL correctamente
                    Log.d("Firestore", "Imagen URL: " + imageUrl);

                    // Agregar los datos a las listas
                    listaEmail.add(email);
                    listaGrado.add(grado);
                    listaGrupo.add(grupo);
                    listaNombre.add(nombre);
                    listaPassword.add(password);
                    listaImagenes.add(imageUrl);
                    listaDocumentIds.add(documentId);  // Añadir el Document ID a la lista
                }

                // Crear el adaptador y asignarlo al RecyclerView
                adaptador = new Secretarias_Lista_alumno_adapter(listaEmail, listaGrado, listaGrupo, listaNombre, listaImagenes, listaDocumentIds, getActivity());
                adaptador.setOnAlumnoDeleteListener(ListaSecretariasAlumnosFragment.this);  // Asignar el listener
                alumnoslista.setAdapter(adaptador);
            }
        });

        // Configurar el botón de regresar
        regresar.setOnClickListener(v1 -> dismiss()); // Cierra el DialogFragment

        return v;
    }

    // Implementación de la interfaz para mostrar el diálogo de confirmación
    @Override
    public void onDeleteConfirmed(String documentId, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmación de eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este alumno?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // Llamar al método de eliminación en el adaptador
                    adaptador.eliminarAlumno(documentId, position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}



