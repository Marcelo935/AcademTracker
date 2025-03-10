package com.example.academtracker.model;

public class Alumno {
    private String id;
    private String nombre;
    private String grado;
    private String grupo;

    public Alumno() {}

    public Alumno(String id,String nombre, String grado, String grupo) {
        this.id = id;
        this.nombre = nombre;
        this.grado = grado;
        this.grupo = grupo;
    }

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
}


