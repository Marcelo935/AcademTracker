package com.example.academtracker.model;

public class Pagos {
    String alumnoId;
    String fecha;
    String metodoPago;
    String monto;

    public Pagos(){

    }

    public Pagos (String alumnoId, String fecha, String metodoPago, String monto){
        this.alumnoId = alumnoId;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.monto = monto;
    }

    public String getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(String alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

}
