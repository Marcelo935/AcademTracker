package com.example.academtracker.model;

public class GradoItem {
    private String grado; // El nombre del grado (para encabezados)
    private Alumno alumno; // Un objeto Alumno (para elementos normales)
    private boolean isHeader; // Bandera para determinar si este es un encabezado

    // Constructor
    public GradoItem(String grado, Alumno alumno, boolean isHeader) {
        this.grado = grado;
        this.alumno = alumno;
        this.isHeader = isHeader;
    }

    // Getter para el grado (se usa en encabezados)
    public String getGrado() {
        return grado;
    }

    // Getter para el alumno (se usa en elementos normales)
    public Alumno getAlumno() {
        return alumno;
    }

    // Getter para determinar si este es un encabezado
    public boolean isHeader() {
        return isHeader;
    }
}
