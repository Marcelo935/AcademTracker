package com.example.academtracker.model;

public class Materia {
    private String nombre;
    private String parcial1;
    private String parcial2;
    private String parcial3;

    // Constructor
    public Materia(String nombre, String parcial1, String parcial2, String parcial3) {
        this.nombre = nombre;
        this.parcial1 = parcial1;
        this.parcial2 = parcial2;
        this.parcial3 = parcial3;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getParcial1() {
        return parcial1;
    }

    public void setParcial1(String parcial1) {
        this.parcial1 = parcial1;
    }

    public String getParcial2() {
        return parcial2;
    }

    public void setParcial2(String parcial2) {
        this.parcial2 = parcial2;
    }

    public String getParcial3() {
        return parcial3;
    }

    public void setParcial3(String parcial3) {
        this.parcial3 = parcial3;
    }
}
