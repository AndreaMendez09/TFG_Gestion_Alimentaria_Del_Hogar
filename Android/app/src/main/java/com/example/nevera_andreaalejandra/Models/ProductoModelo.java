package com.example.nevera_andreaalejandra.Models;

import java.io.Serializable;
import java.util.Date;

public class ProductoModelo implements Serializable {
    private String id;
    private int cantidad;
    private String tipo;
    private String nombre;
    private String fecha;
    private double precio;
    private String ubicacion;
    private String usuario;

    //Creamos el constructor vacio
    public ProductoModelo() {

    }

    //Para añadir
    public ProductoModelo(String id, String nombre, int cantidad, double precio,String ubicacion, String tipo, String fecha, String UID_usuario) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
        this.usuario = UID_usuario;
        this.ubicacion = ubicacion;
    }

    //Para addEdit
    public ProductoModelo( String nombre, int cantidad, double precio,String ubicacion, String tipo, String fecha, String UID_usuario) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
        this.usuario = UID_usuario;
        this.ubicacion = ubicacion;
    }

    //Para cuando sea editar, porque no debe tener ni id, ni ubicacion, ni usuario
    public ProductoModelo(String nombre, int cantidad, double precio, String tipo, String fecha) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    //Creamos los getters y setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUID_usuario() {
        return usuario;
    }

    public void setUID_usuario(String UID_usuario) {
        this.usuario = UID_usuario;
    }
}
