package com.example.academtracker.model;

import java.util.List;

public class Materias {
    private String nombre;
    private String grado;
    private String grupo;
    private List<String> alumnos; // Nueva lista para almacenar los alumnos

    // Constructor vacío (necesario para Firestore)
    public Materias() {}

    // Constructor sin alumnos (para compatibilidad con código anterior)
    public Materias(String nombre, String grado, String grupo) {
        this.nombre = nombre;
        this.grado = grado;
        this.grupo = grupo;
    }

    // Nuevo constructor con alumnos
    public Materias(String nombre, String grado, String grupo, List<String> alumnos) {
        this.nombre = nombre;
        this.grado = grado;
        this.grupo = grupo;
        this.alumnos = alumnos;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public List<String> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<String> alumnos) {
        this.alumnos = alumnos;
    }
}
