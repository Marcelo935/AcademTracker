package com.example.academtracker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Materia implements Parcelable {
    private String nombre;
    private double parcial1;
    private double parcial2;
    private double parcial3;

    public Materia(String nombre, String parcial1, String parcial2, String parcial3) {
        this.nombre = nombre;
        this.parcial1 = Double.parseDouble(parcial1.isEmpty() ? "0" : parcial1);
        this.parcial2 = Double.parseDouble(parcial2.isEmpty() ? "0" : parcial2);
        this.parcial3 = Double.parseDouble(parcial3.isEmpty() ? "0" : parcial3);
    }

    protected Materia(Parcel in) {
        nombre = in.readString();
        parcial1 = in.readDouble();
        parcial2 = in.readDouble();
        parcial3 = in.readDouble();
    }

    public static final Creator<Materia> CREATOR = new Creator<Materia>() {
        @Override
        public Materia createFromParcel(Parcel in) {
            return new Materia(in);
        }

        @Override
        public Materia[] newArray(int size) {
            return new Materia[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public double getParcial1() {
        return parcial1;
    }

    public double getParcial2() {
        return parcial2;
    }

    public double getParcial3() {
        return parcial3;
    }

    public double getPromedio() {
        return (parcial1 + parcial2 + parcial3) / 3.0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(nombre);
        parcel.writeDouble(parcial1);
        parcel.writeDouble(parcial2);
        parcel.writeDouble(parcial3);
    }
}
