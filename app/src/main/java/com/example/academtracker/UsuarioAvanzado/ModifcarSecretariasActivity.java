package com.example.academtracker.UsuarioAvanzado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.academtracker.R;
import com.example.academtracker.adapter.AlumnosAdapter;
import com.example.academtracker.model.Alumno;
import com.example.academtracker.model.Alumnos;
import com.example.academtracker.model.GradoItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifcarSecretariasActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private String uid;
    private Spinner spinnerMaterias;
    private Button guardar;
    private RecyclerView recyclerViewAlumnos;

    private ArrayList<String> listaMaterias = new ArrayList<>();
    private ArrayList<GradoItem> listaAlumnos = new ArrayList<>();
    private ArrayList<String> alumnosSeleccionados = new ArrayList<>();
    private String materiaSeleccionada;
    private AlumnosAdapter alumnosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifcar_secretarias);

        mFirestore = FirebaseFirestore.getInstance();
        uid = getIntent().getStringExtra("uid");

        spinnerMaterias = findViewById(R.id.spinner_materias);
        guardar = findViewById(R.id.btn_save);
        recyclerViewAlumnos = findViewById(R.id.recyclerViewAlumnos);

        // Configuración del RecyclerView
        recyclerViewAlumnos.setLayoutManager(new LinearLayoutManager(this));
        alumnosAdapter = new AlumnosAdapter(listaAlumnos, alumnosSeleccionados);
        recyclerViewAlumnos.setAdapter(alumnosAdapter);

        // Cargar materias y alumnos
        cargarMaterias();
        cargarAlumnos();

        // Listener del Spinner
        spinnerMaterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                materiaSeleccionada = listaMaterias.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                materiaSeleccionada = null;
            }
        });

        // Listener del botón Guardar
        guardar.setOnClickListener(v -> {
            if (materiaSeleccionada != null && !alumnosSeleccionados.isEmpty()) {
                // Crear una lista de tareas para obtener los documentos de los alumnos seleccionados
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                // Obtener los documentos de los alumnos seleccionados
                for (String alumnoId : alumnosSeleccionados) {
                    tasks.add(mFirestore.collection("Alumnos").document(alumnoId).get());
                }

                // Esperar que todas las tareas se completen
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(documentSnapshots -> {
                    // Crear una lista de alumnos
                    ArrayList<Alumno> alumnosSeleccionadosList = new ArrayList<>();

                    // Iterar sobre los resultados, que son de tipo Object
                    for (Object obj : documentSnapshots) {
                        DocumentSnapshot documentSnapshot = (DocumentSnapshot) obj;  // Casting explícito a DocumentSnapshot
                        if (documentSnapshot.exists()) {
                            Alumno alumno = documentSnapshot.toObject(Alumno.class);
                            if (alumno != null) {
                                alumnosSeleccionadosList.add(alumno);
                            }
                        }
                    }

                    // Verificar cuántos alumnos se han agregado
                    Log.d("ModifcarSecretarias", "Alumnos seleccionados: " + alumnosSeleccionadosList.size());

                    // Llamar a la función para agregar los alumnos seleccionados
                    if (!alumnosSeleccionadosList.isEmpty()) {
                        agregarAlumnos(materiaSeleccionada, alumnosSeleccionadosList, uid);
                        // Llamamos a la nueva función para agregar la materia a las calificaciones de los alumnos
                        agregarMateriaACalificacionesDeAlumnos(materiaSeleccionada);
                    } else {
                        Toast.makeText(this, "No se pudo cargar la información de los alumnos.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los datos de los alumnos.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al cargar los datos de los alumnos", e);
                });
            } else {
                Toast.makeText(this, "Selecciona una materia y al menos un alumno.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMaterias() {
        mFirestore.collection("Materias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaMaterias.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String materia = document.getString("nombre"); // Asumiendo que cada documento tiene un campo "nombre"
                        if (materia != null) {
                            listaMaterias.add(materia);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ModifcarSecretariasActivity.this,
                            android.R.layout.simple_spinner_item,
                            listaMaterias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMaterias.setAdapter(adapter);

                    if (!listaMaterias.isEmpty()) {
                        materiaSeleccionada = listaMaterias.get(0);
                    } else {
                        Toast.makeText(this, "No hay materias disponibles.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar las materias.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al cargar las materias", e);
                });
    }

    private void cargarAlumnos() {
        mFirestore.collection("Alumnos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaAlumnos.clear(); // Limpia la lista actual

                    // Mapa para agrupar alumnos por grado y grupo
                    Map<String, Map<String, List<Alumno>>> alumnosPorGradoYGrupo = new HashMap<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Alumno alumno = document.toObject(Alumno.class);
                        alumno.setId(document.getId());

                        // Obtener el grado y el grupo del alumno
                        String grado = alumno.getGrado();
                        String grupo = alumno.getGrupo(); // Asegúrate de que este campo exista en el modelo Alumno

                        // Si el mapa de grado no contiene este grado, lo inicializamos
                        if (!alumnosPorGradoYGrupo.containsKey(grado)) {
                            alumnosPorGradoYGrupo.put(grado, new HashMap<>());
                        }

                        // Si el mapa de grupo no contiene este grupo, lo inicializamos
                        if (!alumnosPorGradoYGrupo.get(grado).containsKey(grupo)) {
                            alumnosPorGradoYGrupo.get(grado).put(grupo, new ArrayList<>());
                        }

                        // Agregar el alumno a la lista del grupo correspondiente
                        alumnosPorGradoYGrupo.get(grado).get(grupo).add(alumno);
                    }

                    // Crear la lista de GradoItem con encabezados y alumnos
                    for (Map.Entry<String, Map<String, List<Alumno>>> entryGrado : alumnosPorGradoYGrupo.entrySet()) {
                        String grado = entryGrado.getKey();
                        Map<String, List<Alumno>> grupos = entryGrado.getValue();

                        // Agregar un encabezado para este grado
                        listaAlumnos.add(new GradoItem(grado, null, true));

                        for (Map.Entry<String, List<Alumno>> entryGrupo : grupos.entrySet()) {
                            String grupo = entryGrupo.getKey();
                            List<Alumno> alumnosDelGrupo = entryGrupo.getValue();

                            // Agregar un encabezado para este grupo dentro del grado
                            listaAlumnos.add(new GradoItem(grupo, null, true));

                            // Agregar cada alumno del grupo
                            for (Alumno alumno : alumnosDelGrupo) {
                                listaAlumnos.add(new GradoItem(null, alumno, false));
                            }
                        }
                    }

                    // Notificar al adaptador
                    alumnosAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar los alumnos.", Toast.LENGTH_SHORT).show();
                    Log.e("ModifcarSecretarias", "Error al cargar los alumnos", e);
                });
    }



    private void agregarMateriaACalificacionesDeAlumnos(String idMateria) {
        CollectionReference alumnosRef = mFirestore.collection("Alumnos");
        alumnosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot alumnoSnapshot : queryDocumentSnapshots) {
                String alumnoId = alumnoSnapshot.getId();

                DocumentReference calificacionesRef = alumnosRef.document(alumnoId)
                        .collection("calificaciones")
                        .document(idMateria);

                Map<String, Object> calificacionesIniciales = new HashMap<>();
                calificacionesIniciales.put("1er parcial", 0);
                calificacionesIniciales.put("2do parcial", 0);
                calificacionesIniciales.put("3er parcial", 0);

                calificacionesRef.set(calificacionesIniciales)
                        .addOnSuccessListener(aVoid -> Log.d("ModifcarSecretarias", "Materia agregada a calificaciones de " + alumnoId))
                        .addOnFailureListener(e -> Log.e("ModifcarSecretarias", "Error al agregar materia a calificaciones de " + alumnoId, e));
            }
            Toast.makeText(this, "Materia agregada a calificaciones de todos los alumnos.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener los alumnos.", Toast.LENGTH_SHORT).show();
            Log.e("ModifcarSecretarias", "Error al obtener los alumnos", e);
        });
    }

    private void agregarAlumnos(String idMateria, ArrayList<Alumno> alumnos, String idProfesor) {
        // Referencia a la materia del profesor
        DocumentReference addStudent = mFirestore.collection("Profesores")
                .document(idProfesor)
                .collection("materias")
                .document(idMateria);

        // Obtener el documento de la materia
        addStudent.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Si la materia ya existe, obtenemos el mapa de alumnos
                        Map<String, Object> alumnosMap = (Map<String, Object>) documentSnapshot.get("alumnos");

                        // Si el mapa de alumnos es nulo, inicialízalo
                        if (alumnosMap == null) {
                            alumnosMap = new HashMap<>();
                        }

                        // Iterar sobre los alumnos seleccionados y agregarlos al mapa
                        for (Alumno alumno : alumnos) {
                            // Generar un ID único para cada alumno basado en el ID de la materia y el tiempo actual
                            String idAlumno = addStudent.getId() + "_" + System.currentTimeMillis();

                            // Crear un mapa con los detalles del alumno
                            Map<String, Object> alumnoMap = new HashMap<>();
                            alumnoMap.put("nombre", alumno.getNombre());
                            alumnoMap.put("grado", alumno.getGrado());
                            // Agregar el alumno al mapa
                            alumnosMap.put(idAlumno, alumnoMap);
                        }

                        // Actualizar el documento de la materia con los nuevos alumnos
                        addStudent.update("alumnos", alumnosMap)
                                .addOnSuccessListener(aVoid -> Log.d("AgregarAlumnos", "Alumnos agregados correctamente"))
                                .addOnFailureListener(e -> Log.e("AgregarAlumnos", "Error al actualizar alumnos", e));
                    } else {
                        // Si la materia no existe, se crea el documento con los alumnos seleccionados
                        Map<String, Object> materiaData = new HashMap<>();
                        Map<String, Object> alumnosMap = new HashMap<>();

                        // Agregar alumnos al mapa
                        for (Alumno alumno : alumnos) {
                            String idAlumno = idMateria + "_" + System.currentTimeMillis();
                            Map<String, Object> alumnoMap = new HashMap<>();
                            alumnoMap.put("nombre", alumno.getNombre());
                            alumnoMap.put("grado", alumno.getGrado());
                            alumnosMap.put(idAlumno, alumnoMap);
                        }
                        // Configurar datos de la materia
                        materiaData.put("alumnos", alumnosMap);
                        // Crear el documento de la materia
                        addStudent.set(materiaData)
                                .addOnSuccessListener(aVoid -> Log.d("AgregarAlumnos", "Materia creada correctamente con alumnos"))
                                .addOnFailureListener(e -> Log.e("AgregarAlumnos", "Error al crear la materia", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("AgregarAlumnos", "Error al verificar la materia", e));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SecretariasActivity.class));
        finish();
    }
}