package com.example.academtracker.model;

public class Alumnos {
    String email;
    String foto;
    String grado;
    String grupo;
    String nombre;
    String password;

    public Alumnos() {

    }

    public Alumnos(String email, String foto, String grado, String grupo,
                   String nombre, String password) {
        this.email = email;
        this.foto = foto;
        this.grado = grado;
        this.grupo = grupo;
        this.nombre = nombre;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

