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

    //Creamos el constructor vacio
    public ProductoModelo() {

    }

    //Creamos el constructor solo con los not null
    public ProductoModelo(String id, int cantidad, String tipo, String nombre, String ubicacion) {
        this.id = id;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public ProductoModelo(String nombre, int cantidad, double precio, String ubicacion, String tipo, String fecha) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    //Para cuando sea editar
    public ProductoModelo(String nombre, int cantidad, double precio, String tipo, String fecha) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    //Creamos los getters y setters
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

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
    }


}
