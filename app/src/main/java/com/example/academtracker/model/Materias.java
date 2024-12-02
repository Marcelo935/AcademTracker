package com.example.academtracker.model;

public class Materias {
    private String nombre;
    private String grado;
    private String grupo;

    public Materias() {}

    public Materias(String nombre, String grado, String grupo) {
        this.nombre = nombre;
        this.grado = grado;
        this.grupo = grupo;
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

