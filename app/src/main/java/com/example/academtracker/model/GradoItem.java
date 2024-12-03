package com.example.academtracker.model;

public class GradoItem {
    private String grado;
    private Alumno alumno;
    private boolean isEncabezado; // Cambiar el nombre para mayor claridad

    // Constructor
    public GradoItem(String grado, Alumno alumno, boolean isEncabezado) {
        this.grado = grado;
        this.alumno = alumno;
        this.isEncabezado = isEncabezado;
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
    public boolean isEncabezado() { // Cambiar el nombre para mayor claridad
        return isEncabezado;
    }

    // Setter para isEncabezado (opcional, si se necesita cambiar en tiempo de ejecución)
    public void setEncabezado(boolean isEncabezado) {
        this.isEncabezado = isEncabezado;
    }
}
