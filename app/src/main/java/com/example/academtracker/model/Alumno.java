package com.example.academtracker.model;

public class Alumno {
    private String id;
    private String nombre;

    public Alumno() {} // Constructor vacío requerido por Firestore

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}


