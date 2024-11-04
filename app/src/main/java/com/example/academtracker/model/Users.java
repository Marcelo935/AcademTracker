package com.example.academtracker.model;

public class Users {
    String nombre, password, email;

    public Users(String nombre, String password, String email) {
        this.nombre = nombre;
        this.password = password;
        this.email = email;

    }

    public String getNombre() {//devuelve el valor de la variable
        return nombre;
    }

    public void setNombre(String nombre) { //Establece el valor de la variable
        this.nombre = nombre;//toma la variable del archivo
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}