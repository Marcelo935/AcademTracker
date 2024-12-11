package com.example.academtracker.model;

import java.util.Map;

public class MateriasAlumnos {
    private String nombre;
    private Map<String, Double> calificaciones;

    public MateriasAlumnos(){

    }

    public MateriasAlumnos(String nombre, Map<String, Double> calificaciones){
        this.nombre = nombre;
        this.calificaciones = calificaciones;
    }

    public String getNombre() {
        return nombre;
    }

    public Map<String, Double> getCalificaciones() {
        return calificaciones;
    }

}
