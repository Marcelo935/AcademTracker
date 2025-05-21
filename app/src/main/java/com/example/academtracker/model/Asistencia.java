package com.example.academtracker.model;

import java.util.Date;

public class Asistencia {
    private String alumnoID;
    private String materia;
    private Date fecha;
    private boolean presente;
    private String profesorEmail;

    public Asistencia() { }

    public Asistencia(String alumnoID, String materia, Date fecha, boolean presente, String profesorEmail) {
        this.alumnoID = alumnoID;
        this.materia = materia;
        this.fecha = fecha;
        this.presente = presente;
        this.profesorEmail = profesorEmail;
    }

    // Getters y Setters
    public String getAlumnoID() { return alumnoID; }
    public String getMateria() { return materia; }
    public Date getFecha() { return fecha; }
    public boolean isPresente() { return presente; }
    public String getProfesorEmail() { return profesorEmail; }
}

