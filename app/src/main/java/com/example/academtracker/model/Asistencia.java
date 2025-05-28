package com.example.academtracker.model;

import com.google.firebase.Timestamp;

public class Asistencia {
    private String alumnoID;
    private String materia;
    private Timestamp fecha;
    private boolean presente;

    public Asistencia() {} // Requerido por Firestore

    public String getAlumnoID() {
        return alumnoID;
    }

    public void setAlumnoID(String alumnoID) {
        this.alumnoID = alumnoID;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}
